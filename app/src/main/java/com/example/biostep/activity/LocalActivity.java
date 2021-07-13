package com.example.biostep.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.biostep.R;
import com.example.biostep.activity.config.configurationFirebase;
import com.example.biostep.activity.helper.Base64Custom;
import com.example.biostep.activity.model.Movimentacao;
import com.example.biostep.activity.model.Usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.biostep.activity.Constants;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LocalActivity extends AppCompatActivity {

    Calendar calendar;
    java.util.Date time;
    SimpleDateFormat simpleDateFormat, simpleTimeFormat;
    String Date, Time;
    TextView GetDate;
    TextView GeTime;
    TextView textAddress;
    TextView infoMais,textLatLong;
    FusedLocationProviderClient client;
    private Movimentacao movimentacao;
    private FirebaseAuth autenticacao = configurationFirebase.getFireBaseAutenticacao();
    private DatabaseReference firebaseRef = configurationFirebase.getFirebaseDatabase();
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ProgressBar progressBar;
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        resultReceiver = new AddressResultReceiver(new Handler());
        textAddress = findViewById(R.id.textAddress);
        textLatLong = findViewById(R.id.textLat);
        GetDate = findViewById(R.id.getDate);
        GeTime = findViewById(R.id.geTime);
        infoMais = findViewById(R.id.infoMais);
        progressBar=findViewById(R.id.progressBar);

        getDataHora();
        validacaoLocation();



    }

    @SuppressLint("SimpleDateFormat")
    public void getDataHora() {

        GetDate = findViewById(R.id.getDate);
        GeTime = findViewById(R.id.geTime);

        calendar = Calendar.getInstance();
        time = Calendar.getInstance().getTime();

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        simpleTimeFormat = new SimpleDateFormat("HH:mm");

        Date = simpleDateFormat.format(calendar.getTime());
        Time = simpleTimeFormat.format(calendar.getTime());

        GetDate.setText(Date);
        GeTime.setText(Time);
    }

    public void salvarLocal(View view) {

        movimentacao = new Movimentacao();

        String data = GetDate.getText().toString();
        String localRecuperado = textAddress.getText().toString();

        movimentacao.setLocal(localRecuperado);
        movimentacao.setData(data);
        movimentacao.setHora(GeTime.getText().toString());
        movimentacao.setInformacoes(infoMais.getText().toString());
        atualizarLocal(localRecuperado);
        movimentacao.salvar(data);
        finish();

    }

    public void atualizarLocal(String local) {

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        usuarioRef.child("ultimoLocal").setValue(local);
    }

    private void getCurrentLocation(){

        progressBar.setVisibility(View.VISIBLE);
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(LocalActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                LocationServices.getFusedLocationProviderClient
                                        (LocalActivity.this)
                                        .removeLocationUpdates(this);
                                if (locationResult != null && locationResult.getLocations().size()
                                        > 0 ){
                                    int latestLocationIndex = locationResult.getLocations()
                                            .size()-1;

                                    double latitude = locationResult.getLastLocation()
                                            .getLatitude();

                                    double longitude = locationResult.getLastLocation()
                                            .getLongitude();

                                    textLatLong.setText(String.format("Latitude: %s\nLongitude:%s",
                                            latitude,longitude)
                                    );

                                    Location location = new Location("providerNA");
                                    location.setLatitude(latitude);
                                    location.setLongitude(longitude);
                                    fetchAddressFromLatLong(location);

                                }else{
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                        , Looper.getMainLooper());
    }

    private void fetchAddressFromLatLong(Location location){
        Intent intent = new Intent(this,FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);
    }

    private  void validacaoLocation(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    LocalActivity.this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
        else{
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if (grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(this,"Permission denied!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode == Constants.SUCCESS_RESULT){
                textAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            }else{
                Toast.makeText(LocalActivity.this,
                        resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

}

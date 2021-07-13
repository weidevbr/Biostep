package com.example.biostep.activity.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class configurationFirebase {

    private static FirebaseAuth authenticate;
    private static DatabaseReference firebase;

    public static DatabaseReference getFirebaseDatabase(){
        if(firebase == null){
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return firebase;
    }

    public static FirebaseAuth getFireBaseAutenticacao(){
        if(authenticate == null){
            authenticate = FirebaseAuth.getInstance();
        }
        return authenticate;
    }
}

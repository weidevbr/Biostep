package com.example.biostep.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.biostep.R;
import com.example.biostep.activity.model.Movimentacao;

import java.util.List;

public class AdapterMovimentacao extends RecyclerView.Adapter<AdapterMovimentacao.MyViewHolder> {

    List<Movimentacao> movimentacoes;
    Context context;

    public AdapterMovimentacao(List<Movimentacao> movimentacoes, Context context) {
        this.movimentacoes = movimentacoes;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_movimentacao, parent, false);
        return new MyViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movimentacao movimentacao = movimentacoes.get(position);

        holder.local.setText(movimentacao.getLocal());
        holder.data.setText(String.valueOf(movimentacao.getData()));
        holder.hora.setText(movimentacao.getHora());
        holder.info.setText(movimentacao.getInformacoes());


    }


    @Override
    public int getItemCount() {
        return movimentacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView local,data,hora,info;

        public MyViewHolder(View itemView) {
            super(itemView);

            local = itemView.findViewById(R.id.textAdapterLocal);
            data = itemView.findViewById(R.id.textAdapterData);
            hora = itemView.findViewById(R.id.textAdapterHora);
            info = itemView.findViewById(R.id.textAdapterInfo);
        }

    }

}

package com.example.biostep.activity.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.example.biostep.activity.config.configurationFirebase;

public class Usuario {

    private String idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String ultimoLocal="";


    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebase = configurationFirebase.getFirebaseDatabase();
        firebase.child("usuarios")
                .child( this.idUsuario )
                .setValue( this );
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUltimoLocal() {
        return ultimoLocal;
    }

    public void setUltimoLocal(String ultimoLocal) {
        this.ultimoLocal = ultimoLocal;
    }
}

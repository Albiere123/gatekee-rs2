package br.ufpb.dcx.lima.albiere.sistema;

import java.util.Objects;

public class Registrador {
    private String aplicativo;
    private String usuario;
    private String email;
    private String senha;

    public Registrador(String aplicativo, String usuario, String email, String senha){
        this.aplicativo = aplicativo;
        this.usuario = usuario;
        this.email = email;
        this.senha = senha;
    }


    public String getAplicativo(){
        return this.aplicativo;
    }
    public String getUsuario(){
        return this.usuario;
    }
    public String getEmail(){
        return this.email;
    }
    public String getSenha(){
        return this.senha;
    }

    public void setAplicativo(String aplicativo){
        this.aplicativo = aplicativo;
    }
    public void setUsuario(String usuario){
        this.usuario = usuario;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setSenha(String senha){
        this.senha = senha;
    }


    public boolean equals(Registrador reg) {
        return reg.getAplicativo().equals(this.aplicativo) && reg.getUsuario().equals(this.usuario) && reg.getSenha().equals(this.senha);
    }

    public boolean equals(String senha) {
        return senha.equals(this.senha);
    }
    public int hashCode(String senha1) {
        return Objects.hashCode(senha1);
    }

}
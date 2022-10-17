/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.curso.model;

import br.com.curso.utils.Conversao;
import java.sql.Date;
import java.text.ParseException;

/**
 *
 * @author johat
 */
public class Administrador extends Pessoa{
    private int idAdministrador;
    private String permiteLogin;
    private String situacao;

    public Administrador(int idAdministrador, String permiteLogin, String situacao, int idPessoa, String cpfCnpj, String nome, Date dataNascimento, Cidade cidade, String login, String senha, String foto) {
        super(idPessoa, cpfCnpj, nome, dataNascimento, cidade, login, senha, foto);
        this.idAdministrador = idAdministrador;
        this.permiteLogin = permiteLogin;
        this.situacao = situacao;
    }
    
    public static Administrador administradorVazio() throws ParseException{
        Cidade oCidade = new Cidade();
        Date dataNascimento = (Date) Conversao.dataAtual();
        Administrador oAdministrador = new Administrador(0,"S","A",0,"","",dataNascimento,oCidade,"","",null);
         return oAdministrador;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getPermiteLogin() {
        return permiteLogin;
    }

    public void setPermiteLogin(String permiteLogin) {
        this.permiteLogin = permiteLogin;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
    
    
    
    
}

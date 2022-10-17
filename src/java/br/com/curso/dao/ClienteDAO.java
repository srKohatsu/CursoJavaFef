/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.curso.dao;

import br.com.curso.model.Cidade;
import br.com.curso.model.Cliente;
import br.com.curso.utils.SingleConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author johat
 */
public class ClienteDAO implements GenericDAO{
private Connection conexao;

    public ClienteDAO() throws Exception {
        conexao = SingleConnection.getConnection();
    }
    
    @Override
    public Boolean cadastrar(Object objeto) {
        Boolean retorno = false;
        try{
            Cliente oCliente = (Cliente) objeto;
            if(oCliente.getIdCliente()==0){
                int idCliente = this.verificarCpf(oCliente.getCpfCnpj());
                if(idCliente==0){
                    retorno = this.inserir(oCliente);
                }else{
                    oCliente.setIdCliente(idCliente);
                    retorno = this.alterar(oCliente);
                }
            }else{
                retorno = this.alterar(oCliente);
            }
        }catch(Exception ex){
            System.out.println("Problemas ao incluir cliente Erro:"+ex.getMessage());
        }
        return retorno;
    }

    @Override
    public Boolean inserir(Object objeto) {
        Cliente oCliente = (Cliente) objeto;
        PreparedStatement stmt = null;
        String sql = "insert into cliente (idPessoa, observacao, situacao ,permitelogin)"
                + " values (?, ?, ?, ?)";
        try{
            PessoaDAO oPessoaDAO = new PessoaDAO();
            //manda informações para o cadastrar de pessoa.
            int idPessoa = oPessoaDAO.cadastrar(oCliente);
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idPessoa);
            stmt.setString(2, oCliente.getObservacao());
            stmt.setString(3, "A");
            stmt.setString(4, oCliente.getPermiteLogin());
            stmt.execute();
            conexao.commit();
            return true;
        }catch(Exception e){
            try {
                System.out.println("Problemas ao cadastrar Cliente!Erro: " + e.getMessage());
                e.printStackTrace();
                conexao.rollback(); 
            } catch (SQLException ex) {
                System.out.println("Problemas ao executar rollback" + ex.getMessage());
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public Boolean alterar(Object objeto) {
        Cliente oCliente = (Cliente) objeto;
        PreparedStatement stmt = null;
        String sql = "update cliente set observacao=?,situacao=? where idCliente=?";
        try{
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, oCliente.getObservacao());
            stmt.setString(2, "A");
            stmt.setInt(3, oCliente.getIdCliente());
            stmt.execute();
            conexao.commit();
            return true;
        }catch (Exception ex) {
            try {
                System.out.println("Problemas ao alterar a Tipo de Trabalho! Erro: " + ex.getMessage());
                ex.printStackTrace();
                conexao.rollback();
            } catch (SQLException e) {
                System.out.println("Erro:" + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public Boolean excluir(int numero) {
        PreparedStatement stmt = null;
        try{
            //carrega dados de administrador
            ClienteDAO oClienteDAO = new ClienteDAO();
            Cliente oCliente = (Cliente) oClienteDAO.carregar(numero);
            String situacao="A";//verifica e troca a situação do administrador
            if(oCliente.getSituacao().equals(situacao))
                situacao = "I";
            else situacao = "A";
            
            String sql = "update cliente set situacao=? where idCliente=?";
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, situacao);
            stmt.setInt(2, oCliente.getIdCliente());
            stmt.execute();
            conexao.commit();
            return true;
        }catch (Exception e){
            try {
                System.out.println("Problemas ao excluir Cliente!Erro: " + e.getMessage());
                e.printStackTrace();
                conexao.rollback(); 
            } catch (SQLException ex) {
                System.out.println("Problemas ao executar rollback" + ex.getMessage());
                ex.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public Object carregar(int numero) {
        int idCliente = numero;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Cliente oCliente = null;
        String sql = "Select * from cliente c, pessoa p "
                + "where c.idpessoa = p.idpessoa and c.idcliente=?";
        try{
            stmt=conexao.prepareStatement(sql);
            stmt.setInt(1, idCliente);
            rs=stmt.executeQuery();
            while(rs.next()){
                //Busca a cidade
                Cidade oCidade = null;
                try{
                   CidadeDAO oCidadeDAO = new CidadeDAO();
                   oCidade = (Cidade) oCidadeDAO.carregar(rs.getInt("idcidade"));
                }catch(Exception ex){
                   System.out.println("Problemas ao carregar cidade!Erro:"+ex.getMessage());
                }
                oCliente = new Cliente(rs.getInt("idcliente"),
                                       rs.getString("observacao"),
                                       rs.getString("permitelogin"),
                                       rs.getString("situacao"),
                                       rs.getInt("idpessoa"),
                                       rs.getString("cpfcnpj"),
                                       rs.getString("nome"),
                                       rs.getDate("datanascimento"),
                                       oCidade,
                                       rs.getString("login"),
                                       rs.getString("senha"),
                                       rs.getString("foto"));
            }
        }catch(SQLException e){
            System.out.println("Problemas ao carregar Cliente!Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return oCliente;
    }

    @Override
    public List<Object> listar() {
        List<Object> resultado = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql= "Select p.*, c.idcliente, c.observacao,c.situacao, c.permitelogin "
                + "from cliente c, pessoa p "
                + "where c.idpessoa = p.idpessoa order by idPessoa";
        try{
            stmt = conexao.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()){
                Cidade oCidade = null;//busca cidade
                try{
                   CidadeDAO oCidadeDAO = new CidadeDAO();
                   oCidade = (Cidade) oCidadeDAO.carregar(rs.getInt("idcidade"));
                }catch(Exception ex){
                   System.out.println("Problemas ao carregar usuario!Erro:"+ex.getMessage());
                }
                Cliente oCliente = new Cliente(rs.getInt("idcliente"),
                                       rs.getString("observacao"),
                                       rs.getString("situacao"),
                                       rs.getString("permitelogin"),
                                       rs.getInt("idpessoa"),
                                       rs.getString("cpfcnpj"),
                                       rs.getString("nome"),
                                       rs.getDate("datanascimento"),
                                       oCidade,
                                       rs.getString("login"),
                                       rs.getString("senha"),
                                       rs.getString("foto"));
                resultado.add(oCliente);
            }
        }catch(SQLException ex){
            System.out.println("Problemas ao listar Cliente! Erro "+ex.getMessage());
        }
        return resultado;
    }
    
    public int verificarCpf(String cpf){
        PreparedStatement stmt = null;
        ResultSet rs= null;
        int idCliente = 0;
        String sql = "Select c.* from cliente c, pessoa p "
                + "where c.idpessoa = p.idPessoa and p.cpfcnpj=?;";
        try{
            stmt=conexao.prepareStatement(sql);
            stmt.setString(1, cpf);
            rs=stmt.executeQuery();
            while(rs.next()){
                idCliente = rs.getInt("idcliente");
            }
            return idCliente;
        }catch(SQLException ex){
            System.out.println("Problemas ai carregar cliente! Erro: "+ex.getMessage());
            return idCliente;
        }
    } 
}

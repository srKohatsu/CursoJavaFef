/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.curso.dao;

import br.com.curso.model.Cidade;
import br.com.curso.model.Fornecedor;
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
public class FornecedorDAO implements GenericDAO{
    private Connection conexao;
    public FornecedorDAO() throws Exception{
        conexao = SingleConnection.getConnection();
    }
    @Override
    public Boolean cadastrar(Object objeto) {
        Boolean retorno = false;
        try {
            Fornecedor oFornecedor = (Fornecedor) objeto;
            if (oFornecedor.getIdFornecedor()==0) { //inserção
                //verifica se já existe pessoa com este CPF cadastrada.
                int Idfornecedor = this.verificarCpf(oFornecedor.getCpfCnpj());
                if (Idfornecedor==0) {
                    //se não encontrou insere
                    retorno = this.inserir(oFornecedor);
                }else{
                    //se encontrou administrador com o cpf altera
                    oFornecedor.setIdFornecedor(Idfornecedor);
                    retorno = this.alterar(oFornecedor);
                }
            } else {
              retorno = this.alterar(oFornecedor);
            }
        } catch (Exception ex){
            System.out.println("Problemas ao incluir Fornecedor! Erro "+ex.getMessage());            
        }
        return retorno;
    }

    @Override
    public Boolean inserir(Object objeto) {
        Fornecedor oFornecedor = (Fornecedor) objeto;
        PreparedStatement stmt = null;
        String sql = "insert into fornecedor (idPessoa, enderecoweb,situacao, permitelogin)"
                + " values (?, ?, ?, ?)";
        try{
            PessoaDAO oPessoaDAO = new PessoaDAO();
            //manda informações para o cadastrar de pessoa.
            int idPessoa = oPessoaDAO.cadastrar(oFornecedor);
            stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idPessoa);
            stmt.setString(2, oFornecedor.getEnderecoweb());
            stmt.setString(3, "A");
            stmt.setString(4, oFornecedor.getPermiteLogin());
            stmt.execute();
            conexao.commit();
            return true;
        }catch(Exception e){
            try {
                System.out.println("Problemas ao cadastrar Fornecedor!Erro: " + e.getMessage());
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
        Fornecedor oFornecedor = (Fornecedor) objeto;
        PreparedStatement stmt = null;
        String sql = "update fornecedor set enderecoweb=?,situacao=? where idFornecedor=?";
        try{
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, oFornecedor.getEnderecoweb());
            stmt.setString(2, "A");
            stmt.setInt(3, oFornecedor.getIdFornecedor());
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
            FornecedorDAO oFornecedorDAO = new FornecedorDAO();
            Fornecedor oFornecedor = (Fornecedor) oFornecedorDAO.carregar(numero);
            String situacao="A";//verifica e troca a situação do administrador
            if(oFornecedor.getSituacao().equals(situacao))
                situacao = "I";
            else situacao = "A";
            
            String sql = "update fornecedor set situacao=? where idFornecedor=?";
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, situacao);
            stmt.setInt(2, oFornecedor.getIdFornecedor());
            stmt.execute();
            conexao.commit();
            return true;
        }catch (Exception e){
            try {
                System.out.println("Problemas ao excluir Fornecedor!Erro: " + e.getMessage());
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
        int Idfornecedor = numero;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Fornecedor oFornecedor = null;
        String sql = "Select * from fornecedor c, pessoa p "
                + "where c.idpessoa = p.idpessoa and c.idfornecedor=?";
        try{
            stmt=conexao.prepareStatement(sql);
            stmt.setInt(1, Idfornecedor);
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
                oFornecedor = new Fornecedor(rs.getInt("idfornecedor"),
                                       rs.getString("enderecoweb"),
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
            System.out.println("Problemas ao carregar Fornecedor!Erro: " + e.getMessage());
            e.printStackTrace();
        }
        return oFornecedor;
    }

    @Override
    public List<Object> listar() {
        List<Object> resultado = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql= "Select p.*, c.idfornecedor, c.enderecoweb,c.situacao, c.permitelogin "
                + "from fornecedor c, pessoa p "
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
                Fornecedor oFornecedor = new Fornecedor(rs.getInt("idfornecedor"),
                                       rs.getString("enderecoweb"),
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
                resultado.add(oFornecedor);
            }
        }catch(SQLException ex){
            System.out.println("Problemas ao listar Fornecedor! Erro "+ex.getMessage());
        }
        return resultado;
    }
    
    public int verificarCpf(String cpf){
        PreparedStatement stmt = null;
        ResultSet rs= null;
        int idFornecedor = 0;
        String sql = "Select c.* from fornecedor c, pessoa p "
                + "where c.idpessoa = p.idPessoa and p.cpfcnpj=?;";
        try{
            stmt=conexao.prepareStatement(sql);
            stmt.setString(1, cpf);
            rs=stmt.executeQuery();
            while(rs.next()){
                idFornecedor = rs.getInt("idfornecedor");
            }
            return idFornecedor;
        }catch(SQLException ex){
            System.out.println("Problemas ai carregar pessoa! Erro: "+ex.getMessage());
            return idFornecedor;
        }
    }   
}

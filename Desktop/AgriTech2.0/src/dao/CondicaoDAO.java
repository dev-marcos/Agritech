package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Condicao;
import connectionfactory.ConnectionFactory;

public class CondicaoDAO {
    Connection con;
    public Condicao cond = new Condicao();

    public CondicaoDAO() {
        con = ConnectionFactory.getConnection();
    }

    public void Inserir(Condicao cond) throws SQLException {
        String sql = "INSERT INTO condicao (idProgramacao, tipoDado,logica,valor1,condicao,valor2) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, cond.getIdProgramacao());
            st.setInt(2, cond.getTipoDado());
            st.setInt(3, cond.getLogica());
            st.setString(4, cond.getValor1());
            st.setInt(5, cond.getCondicao());
            st.setString(6, cond.getValor2());
            st.execute();
        }
    }

    public void Alterar(Condicao cond) throws SQLException {
        String sql = "UPDATE condicao SET \r\n" + 
                            "idProgramacao = ?,\r\n" + 
                            "tipoDado = ?,\r\n" + 
                            "logica = ?,\r\n" + 
                            "valor1 = ?,\r\n" + 
                            "condicao = ?,\r\n" + 
                            "valor2 = ?\r\n" + 
                            "WHERE id = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, cond.getIdProgramacao());
            st.setInt(2, cond.getTipoDado());
            st.setInt(3, cond.getLogica());
            st.setString(4, cond.getValor1());
            st.setInt(5, cond.getCondicao());
            st.setString(6, cond.getValor2());
            st.setInt(7, cond.getId());
            st.execute();
        }
    }

    public List<Condicao> listAll(int iProg) throws SQLException{
        String sql = "SELECT * FROM CONDICAO WHERE idProgramacao=?";

        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, iProg);
        ResultSet result = st.executeQuery();

        List<Condicao> lista = new ArrayList<>();
        while (result.next()) {
            Condicao cond = new Condicao();
            cond.setId(result.getInt("id"));
            cond.setIdProgramacao(result.getInt("idProgramacao"));
            cond.setTipoDado(result.getInt("tipoDado"));
            cond.setLogica(result.getInt("logica"));
            cond.setValor1(result.getString("valor1"));
            cond.setCondicao(result.getInt("condicao"));
            cond.setValor2(result.getString("valor2"));

            lista.add(cond);
        }

        return lista;
    }

    public void Excluir(int id) throws SQLException {
        String sql = "DELETE FROM condicao WHERE id=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.execute();
        }
    }

    public void ExcluirProg(int id) throws SQLException {
        String sql = "DELETE FROM condicao WHERE idProgramacao=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.execute();
        }
    }

    public List<String> GeraArq(int id) throws SQLException{
        String sql = "SELECT c.tipoDado,\r\n" + 
                    "       c.logica,\r\n" + 
                    "       c.valor1,\r\n" + 
                    "       c.condicao,\r\n" + 
                    "       c.valor2\r\n" + 
                    "  FROM condicao c\r\n" + 
                    "  INNER JOIN programacao p ON p.id = c.idProgramacao\r\n" + 
                    "  WHERE p.ativo = 1 " +
                    "  AND c.idProgramacao = ?;";
  
        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet result = st.executeQuery();

        List<String> lista = new ArrayList<>();
        while (result.next()) {
            lista.add("|"+result.getString("logica")+
                    "|"+result.getString("tipoDado")+                        
                        "|"+result.getString("valor1")+
                        "|"+result.getString("condicao")+
                        "|"+result.getString("valor2")+"|");	
        }

        return lista;
    }
	
}
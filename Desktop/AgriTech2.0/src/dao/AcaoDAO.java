package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Acao;
import connectionfactory.ConnectionFactory;

public class AcaoDAO {
    Connection con;
    public Acao acao = new Acao();

    public AcaoDAO() {
        con = ConnectionFactory.getConnection();
    }

    public void Inserir(Acao acao) throws SQLException {
        String sql = "INSERT INTO acao (idProgramacao, dispositivo, acao) VALUES (?,?,?)";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, acao.getIdProgramacao());
            st.setInt(2, acao.getDispositivo());
            st.setInt(3, acao.getAcao());
            st.execute();
        }
    }

    public void Alterar(Acao acao) throws SQLException {
        String sql = "UPDATE acao SET idProgramacao = ?, dispositivo = ?, acao = ? WHERE id = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, acao.getIdProgramacao());
            st.setInt(2, acao.getDispositivo());
            st.setInt(3, acao.getAcao());
            st.setInt(4, acao.getId());
            st.execute();
        }
    }

    public List<Acao> listAll(int iProg) throws SQLException{
        String sql = "SELECT * FROM acao WHERE idProgramacao=?";

        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, iProg);
        ResultSet result = st.executeQuery();

        List<Acao> lista = new ArrayList<>();
        while (result.next()) {
            Acao acao = new Acao();
            acao.setId(result.getInt("id"));
            acao.setIdProgramacao(result.getInt("idProgramacao"));
            acao.setDispositivo(result.getInt("dispositivo"));
            acao.setAcao(result.getInt("acao"));

            lista.add(acao);
        }

        return lista;
    }

    public void Excluir(int id) throws SQLException {
        String sql = "DELETE FROM acao WHERE ID=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.execute();
        }
    }

    public void ExcluirProg(int id) throws SQLException {
        String sql = "DELETE FROM acao WHERE idProgramacao=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.execute();
        }
    }


    public List<String> GeraArq(int id) throws SQLException{
        String sql = "SELECT a.idProgramacao, a.dispositivo, a.acao\r\n" + 
                        "  FROM acao a\r\n" + 
                        "  INNER JOIN programacao p ON p.id = a.idProgramacao\r\n" + 
                        "  WHERE p.ativo = 1 " +
                        "  AND a.idProgramacao = ?;";

        PreparedStatement st = con.prepareStatement(sql);
        st.setInt(1, id);
        ResultSet result = st.executeQuery();

        List<String> lista = new ArrayList<>();
        while (result.next()) {
            //|Dispositivo|Ação|
            lista.add("|"+result.getString("dispositivo")+
                    "|"+result.getString("acao")+"|");	
        }

        return lista;
    }
	
}
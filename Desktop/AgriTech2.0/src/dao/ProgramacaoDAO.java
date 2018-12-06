package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Programacao;
import connectionfactory.ConnectionFactory;

public class ProgramacaoDAO {
    Connection con;
    public Programacao cond = new Programacao();

    public ProgramacaoDAO() {
            con = ConnectionFactory.getConnection();
    }

    public void Inserir(Programacao prog) throws SQLException {
            String sql = "INSERT INTO programacao (descricao, ativo) VALUES (?,?)";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, prog.getDescricao());
            st.setBoolean(2, prog.isAtivo());
            st.execute();
        }
    }

    public void Alterar(Programacao prog) throws SQLException {
            String sql = "UPDATE programacao SET\r\n" + 
                                    "descricao = ?,\r\n" + 
                                    "ativo = ?\r\n" + 
                                    " WHERE id = ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, prog.getDescricao());
            st.setBoolean(2, prog.isAtivo());
            st.setInt(3, prog.getId());
            st.execute();
        }
    }


    public void Excluir(int id) throws SQLException {
        String sql = "DELETE FROM programacao WHERE id=?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.execute();
        }
    }
    
    public void ExcluirProg(int id) throws SQLException {
        AcaoDAO daoAcao = new AcaoDAO();
        daoAcao.ExcluirProg(id);

        CondicaoDAO daoCond = new CondicaoDAO();
        daoCond.ExcluirProg(id);

        String sql = "DELETE FROM programacao WHERE id=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.execute();
        }
    }

    
    
    public List<Programacao> listAll() throws SQLException{
            String sql = "SELECT * FROM PROGRAMACAO";

            PreparedStatement st = con.prepareStatement(sql);
            ResultSet result = st.executeQuery();

            List<Programacao> lista = new ArrayList<>();
            while (result.next()) {
                    Programacao prog = new Programacao();
                    prog.setId(result.getInt("id"));
                    prog.setDescricao(result.getString("descricao"));
                    prog.setAtivo(result.getBoolean("ativo"));

                    lista.add(prog);
            }

            return lista;
    }
       

}

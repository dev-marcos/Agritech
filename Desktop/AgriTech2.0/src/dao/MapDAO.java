package dao;

//import bean.Map;
import connectionfactory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapDAO {
    Connection con;
    //public Map map = new Map();

    public MapDAO() {
            con = ConnectionFactory.getConnection();
    }

    public Integer getMin(String porta) throws SQLException {
            String sql = "SELECT min FROM map WHERE porta=?";

            try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setString(1, porta);
                    st.execute();
                    ResultSet result = st.executeQuery();

                    result.next();
                    return result.getInt("min");	
            }
    }


    public Integer getMax(String porta) throws SQLException {
            String sql = "SELECT max FROM map WHERE porta=?";

            try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setString(1, porta);
                    st.execute();
                    ResultSet result = st.executeQuery();

                    result.next();
                    return result.getInt("max");	
            }
    }


    public void setMin(String porta, Integer valor) throws SQLException {
            String sql="";
            if (this.exists(porta)){          
                    sql = "UPDATE map SET min = ? WHERE porta = ?";
            }else{
                    sql = "INSERT INTO map (min, porta) VALUES (?,?)";
            }

            try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setInt(1, valor);
                    st.setString(2, porta);
                    st.execute();
            }
    }

    public void setMax(String porta, Integer valor) throws SQLException {
            String sql="";
            if (this.exists(porta)){          
                    sql = "UPDATE map SET max = ? WHERE porta = ?";
            }else{
                    sql = "INSERT INTO map (max, porta) VALUES (?,?)";
            }

            try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setInt(1, valor);
                    st.setString(2, porta);
                    st.execute();
            }
    }


    public Boolean exists(String porta) throws SQLException {
    String sql = "SELECT id FROM map WHERE porta=?";

            try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setString(1, porta);
                    st.execute();
                    ResultSet result = st.executeQuery();
                    return result.next();                          
            }
    }


    public List<String> GeraArq() throws SQLException{
        String sql = "SELECT porta, min, max FROM map ORDER BY porta";

        PreparedStatement st = con.prepareStatement(sql);
        ResultSet result = st.executeQuery();

        List<String> lista = new ArrayList<>();
        while (result.next()) {
            lista.add("|"+result.getString("porta")+
                    "|"+result.getString("min")+
                    "|"+result.getString("max")+"|");	
        }

        return lista;
    }

}
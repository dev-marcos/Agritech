package dao;

//import bean.Config;
import connectionfactory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigDAO {
	Connection con;
	//public Config config = new Config();

	public ConfigDAO() {
		con = ConnectionFactory.getConnection();
	}
	
	public String get(String chave) throws SQLException {
		String sql = "SELECT id, chave, valor FROM config WHERE chave=?";
		
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, chave);
                st.execute();
                ResultSet result = st.executeQuery();
                
		result.next();
                return result.getString("valor") != null? result.getString("valor"): "";	
            }
	}
        
        public String Read(String chave) throws SQLException{
            return "|"+chave+"|"+ this.get(chave)+ "|";
        }
	
	public void set(String chave, String valor) throws SQLException {
            String sql="";
            if (this.exists(chave)){          
                sql = "UPDATE config SET valor = ? WHERE chave = ?";
            }else{
                sql = "INSERT INTO config (valor, chave) VALUES (?,?)";
            }
            
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, valor);
                st.setString(2, chave);
                st.execute();
            }
	}
	
	
        public Boolean exists(String chave) throws SQLException {
		String sql = "SELECT id, chave, valor FROM config WHERE chave=?";
	
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setString(1, chave);
                st.execute();
                ResultSet result = st.executeQuery();
                return result.next();                          
            }
	}

    public List<String> GeraArq() throws SQLException{
        String sql = "SELECT id, chave, valor FROM config ORDER BY id";

        PreparedStatement st = con.prepareStatement(sql);
        ResultSet result = st.executeQuery();

        List<String> lista = new ArrayList<>();
        while (result.next()) {
            lista.add("|"+result.getString("chave")+
                    "|"+result.getString("valor")+"|");	
        }

        return lista;
    }
	
	
	
}
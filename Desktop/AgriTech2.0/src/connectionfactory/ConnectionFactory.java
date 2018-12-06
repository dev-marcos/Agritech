package connectionfactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

// Conecta com o Banco
public class ConnectionFactory {
   // static String path = new File("").getAbsolutePath();
    private static String url; 
    static int qtSql = 11;

    public static Connection getConnection(){
        try {
            Connection con = null;
            File file = new File("Irrigacao.db");
            url = "jdbc:sqlite:Irrigacao.db";

            if (!file.exists()) {
                con = DriverManager.getConnection(url);
                for (int i = 1; i <= qtSql; i++) {
                    try (PreparedStatement st = con.prepareStatement(sql(i))) {
                        st.execute();
                    }					
                }
            }else{
                con = DriverManager.getConnection(url);
            }

            return con;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro na Conexão com o BD \n"+e);
            throw new RuntimeException();// necessario para que não de erro.
        }
    }

    private static String sql(int i) {
        String ret = "";

        switch (i) {
        case 1: ret= "CREATE TABLE programacao (id INTEGER PRIMARY KEY AUTOINCREMENT, descricao VARCHAR (50), ativo BOOLEAN DEFAULT (1))";
            break;
        case 2: ret= "CREATE TABLE acao (id INTEGER PRIMARY KEY AUTOINCREMENT, idProgramacao INTEGER REFERENCES programacao (id), dispositivo INTEGER, acao INTEGER)";
            break;
        case 3: ret= "CREATE TABLE condicao (id INTEGER PRIMARY KEY AUTOINCREMENT, idProgramacao INTEGER REFERENCES programacao (id), tipoDado INTEGER, logica INTEGER, valor1 VARCHAR (10), condicao INTEGER, valor2 VARCHAR (10))";
            break;      
        case 4: ret= "CREATE TABLE map (id INTEGER PRIMARY KEY AUTOINCREMENT, porta VARCHAR (4), min INTEGER, max INTEGER)";
            break;
        case 5: ret= "INSERT INTO map (id, porta, min, max) VALUES (1, '$A0', 0, 100)";
            break;
        case 6: ret= "INSERT INTO map (id, porta, min, max) VALUES (2, '$A1', 0, 100)";
            break;
        case 7: ret= "INSERT INTO map (id, porta, min, max) VALUES (3, '$A2', 0, 100)";
            break;
        case 8: ret= "INSERT INTO map (id, porta, min, max) VALUES (4, '$A3', 0, 100)";
            break;
        case 9: ret= "CREATE TABLE config (id INTEGER PRIMARY KEY AUTOINCREMENT, chave VARCHAR (20), valor VARCHAR (50))";
            break;
        case 10: ret= "INSERT INTO config (valor, chave) VALUES (10, 'IntervaloRep')";
            break;
        case 11: ret= "INSERT INTO config (valor, chave) VALUES (1800, 'IntervaloLog')";
            break;

        default: ret= "";
            break;
        }

        return ret;
    }
}
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // Parámetros de conexión para Oracle
    //private static final String URL = "jdbc:oracle:thin:@10.34.1.33:1521:INGSIS";
    private static final String USER = "is213810".trim();
    private static final String PASS = "ZK38MxH29lntOen".trim();

    // Alternativa usando el service name (PDB)
     private static final String URL = "jdbc:oracle:thin:@//orion.javeriana.edu.co:1521/LAB";

    public static Connection getConnection() throws SQLException {
        try {
            // 1. Registrar el driver JDBC (importante para Oracle)
            Class.forName("oracle.jdbc.OracleDriver");

            // 2. Establecer la conexión
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("¡Conexión exitosa a la base de datos Oracle!");
            return conn;

        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de Oracle");
            throw new SQLException("Driver JDBC no encontrado", e);
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos Oracle");
            System.err.println("URL: " + URL);
            System.err.println("Usuario: " + USER);
            throw e; // Relanzamos la excepción para manejo externo
        }
    }
    public static String getUrl() {
        return URL;
    }
    public static String getUser() {
        return USER;
    }

}

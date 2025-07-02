import java.sql.Connection;

public class TesteConexao {
    public static void main(String[] args) {
        Connection conn = PostgresConnection.conectar();
        if (conn != null) {
            System.out.println("Conexão bem-sucedida!");
        } else {
            System.out.println("Falha na conexão.");
        }
    }
}

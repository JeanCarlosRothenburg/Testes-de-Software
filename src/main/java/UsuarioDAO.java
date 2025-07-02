import java.sql.*;

public class UsuarioDAO {

    public boolean criarConta(String email, String nome, String senha) {
        String sql = "INSERT INTO usuarios (email, nome, senha) VALUES (?, ?, ?)";
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, nome);
            stmt.setString(3, senha);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // e.printStackTrace(); // Pode ser usado para debug
            return false;
        }
    }

    public boolean fazerLogin(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // se encontrou o usuário, login é válido

        } catch (SQLException e) {
            return false;
        }
    }

    public void excluirContaPorEmail(String email) {
        String sql = "DELETE FROM usuarios WHERE email = ?";
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Ignorar
        }
    }
}

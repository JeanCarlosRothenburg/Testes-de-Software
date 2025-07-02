import java.sql.*;

public class CompraDAO {

    public String comprarNovamente(int idCompra) {
        String sql = "INSERT INTO compras (email, produto, status) " +
                "SELECT email, produto, 'refeita' FROM compras WHERE id = ?";
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCompra);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                return "Compra refeita com sucesso";
            } else {
                return "Compra original não encontrada";
            }
        } catch (SQLException e) {
            return "Erro ao refazer compra";
        }
    }

        public int criarCompraDeTeste (String email, String produto){
            String sql = "INSERT INTO compras (email, produto, status) VALUES (?, ?, 'entregue') RETURNING id";
            try (Connection conn = PostgresConnection.conectar();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, email);
                stmt.setString(2, produto);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id"); // retorna o ID gerado
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1; // erro
        }

    }


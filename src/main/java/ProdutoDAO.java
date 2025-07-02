import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public List<String> aplicarFiltro(String categoria, double precoMin, double precoMax, Integer notaMin) {
        List<String> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT nome FROM produtos WHERE categoria = ? AND preco BETWEEN ? AND ?");
        if (notaMin != null) {
            sql.append(" AND nota >= ?");
        }

        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, categoria);
            stmt.setDouble(2, precoMin);
            stmt.setDouble(3, precoMax);
            if (notaMin != null) {
                stmt.setInt(4, notaMin);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultados.add(rs.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }
}



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompraDAO {

    public double calcularTotalComFrete(double valorCarrinho, String cep) {
        double frete = 0.0;
        if ("11001-000".equals(cep)) {
            frete = 15.00;
        }
        return valorCarrinho + frete;
    }

    public String finalizarCompraPIX(String email, String produto, double valorTotal) {
        String sql = "INSERT INTO compras (email, produto, valor_total, forma_pagamento, status) " +
                "VALUES (?, ?, ?, 'PIX', 'confirmada')";
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, produto);
            stmt.setDouble(3, valorTotal);
            stmt.executeUpdate();
            return "Compra realizada com sucesso via PIX";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao finalizar compra";
        }
    }

    public boolean verificarCompra(String email, String produto) {
        String sql = "SELECT * FROM compras WHERE email = ? AND produto = ?";
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, produto);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }

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

    public int criarCompraDeTeste(String email, String produto) {
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



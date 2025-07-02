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

}

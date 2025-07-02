import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO para gerenciar assinaturas - Implementação híbrida (DB real + stub para testes)
 * RF032 - O sistema deve permitir gerenciar inscrições e assinaturas
 */
public class AssinaturaDAO {
    
    private boolean usarBancoDados;
    private Map<String, List<Assinatura>> assinaturasStub; // Para testes
    
    public AssinaturaDAO() {
        this.usarBancoDados = true;
        this.assinaturasStub = new HashMap<>();
    }
    
    public AssinaturaDAO(boolean usarBancoDados) {
        this.usarBancoDados = usarBancoDados;
        this.assinaturasStub = new HashMap<>();
    }

    /**
     * Busca todas as assinaturas de um usuário
     */
    public List<Assinatura> buscarAssinaturasPorUsuario(String usuarioId) {
        if (usarBancoDados) {
            return buscarAssinaturasBanco(usuarioId);
        } else {
            return buscarAssinaturasStub(usuarioId);
        }
    }

    /**
     * Busca uma assinatura específica por ID
     */
    public Assinatura buscarAssinaturaPorId(String assinaturaId) {
        if (usarBancoDados) {
            return buscarAssinaturaBanco(assinaturaId);
        } else {
            return buscarAssinaturaStub(assinaturaId);
        }
    }

    /**
     * Atualiza o status de uma assinatura
     */
    public boolean atualizarStatusAssinatura(String assinaturaId, Assinatura.StatusAssinatura novoStatus) {
        if (usarBancoDados) {
            return atualizarStatusBanco(assinaturaId, novoStatus);
        } else {
            return atualizarStatusStub(assinaturaId, novoStatus);
        }
    }

    /**
     * Salva uma nova assinatura
     */
    public boolean salvarAssinatura(Assinatura assinatura) {
        if (usarBancoDados) {
            return salvarAssinaturaBanco(assinatura);
        } else {
            return salvarAssinaturaStub(assinatura);
        }
    }

    // ===== IMPLEMENTAÇÃO COM BANCO DE DADOS REAL =====
    
    private List<Assinatura> buscarAssinaturasBanco(String usuarioId) {
        List<Assinatura> assinaturas = new ArrayList<>();
        String sql = "SELECT * FROM assinaturas WHERE usuario_id = ? ORDER BY data_inicio DESC";
        
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Assinatura assinatura = criarAssinaturaFromResultSet(rs);
                assinaturas.add(assinatura);
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar assinaturas: " + e.getMessage());
        }
        
        return assinaturas;
    }
    
    private Assinatura buscarAssinaturaBanco(String assinaturaId) {
        String sql = "SELECT * FROM assinaturas WHERE id = ?";
        
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, assinaturaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return criarAssinaturaFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao buscar assinatura: " + e.getMessage());
        }
        
        return null;
    }
    
    private boolean atualizarStatusBanco(String assinaturaId, Assinatura.StatusAssinatura novoStatus) {
        String sql = "UPDATE assinaturas SET status = ? WHERE id = ?";
        
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novoStatus.name());
            stmt.setString(2, assinaturaId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
            return false;
        }
    }
    
    private boolean salvarAssinaturaBanco(Assinatura assinatura) {
        String sql = "INSERT INTO assinaturas (id, nome, descricao, preco, tipo, status, " +
                    "data_inicio, data_fim, usuario_id, auto_renovacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = PostgresConnection.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, assinatura.getId());
            stmt.setString(2, assinatura.getNome());
            stmt.setString(3, assinatura.getDescricao());
            stmt.setBigDecimal(4, assinatura.getPreco());
            stmt.setString(5, assinatura.getTipo().name());
            stmt.setString(6, assinatura.getStatus().name());
            stmt.setTimestamp(7, Timestamp.valueOf(assinatura.getDataInicio()));
            stmt.setTimestamp(8, Timestamp.valueOf(assinatura.getDataFim()));
            stmt.setString(9, assinatura.getUsuarioId());
            stmt.setBoolean(10, assinatura.isAutoRenovacao());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao salvar assinatura: " + e.getMessage());
            return false;
        }
    }
    
    private Assinatura criarAssinaturaFromResultSet(ResultSet rs) throws SQLException {
        Assinatura assinatura = new Assinatura();
        assinatura.setId(rs.getString("id"));
        assinatura.setNome(rs.getString("nome"));
        assinatura.setDescricao(rs.getString("descricao"));
        assinatura.setPreco(rs.getBigDecimal("preco"));
        assinatura.setTipo(Assinatura.TipoAssinatura.valueOf(rs.getString("tipo")));
        assinatura.setStatus(Assinatura.StatusAssinatura.valueOf(rs.getString("status")));
        assinatura.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        assinatura.setDataFim(rs.getTimestamp("data_fim").toLocalDateTime());
        assinatura.setUsuarioId(rs.getString("usuario_id"));
        assinatura.setAutoRenovacao(rs.getBoolean("auto_renovacao"));
        return assinatura;
    }

    // ===== IMPLEMENTAÇÃO COM STUB (PARA TESTES) =====
    
    private List<Assinatura> buscarAssinaturasStub(String usuarioId) {
        return assinaturasStub.getOrDefault(usuarioId, new ArrayList<>());
    }
    
    private Assinatura buscarAssinaturaStub(String assinaturaId) {
        for (List<Assinatura> assinaturas : assinaturasStub.values()) {
            for (Assinatura assinatura : assinaturas) {
                if (assinatura.getId().equals(assinaturaId)) {
                    return assinatura;
                }
            }
        }
        return null;
    }
    
    private boolean atualizarStatusStub(String assinaturaId, Assinatura.StatusAssinatura novoStatus) {
        for (List<Assinatura> assinaturas : assinaturasStub.values()) {
            for (Assinatura assinatura : assinaturas) {
                if (assinatura.getId().equals(assinaturaId)) {
                    assinatura.setStatus(novoStatus);
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean salvarAssinaturaStub(Assinatura assinatura) {
        String usuarioId = assinatura.getUsuarioId();
        List<Assinatura> assinaturasUsuario = assinaturasStub.get(usuarioId);
        if (assinaturasUsuario == null) {
            assinaturasUsuario = new ArrayList<>();
            assinaturasStub.put(usuarioId, assinaturasUsuario);
        }
        assinaturasUsuario.add(assinatura);
        return true;
    }

    // ===== MÉTODOS UTILITÁRIOS PARA TESTES =====
    
    /**
     * Configura dados de teste (só funciona em modo stub)
     */
    public void configurarDadosTeste(String usuarioId, List<Assinatura> assinaturas) {
        if (!usarBancoDados) {
            assinaturasStub.put(usuarioId, new ArrayList<>(assinaturas));
        }
    }
    
    /**
     * Limpa todos os dados (só funciona em modo stub)
     */
    public void limparDados() {
        if (!usarBancoDados) {
            assinaturasStub.clear();
        }
    }
    
    /**
     * Define se deve usar banco de dados ou stub
     */
    public void setUsarBancoDados(boolean usarBancoDados) {
        this.usarBancoDados = usarBancoDados;
    }
    
    public boolean isUsandoBancoDados() {
        return usarBancoDados;
    }
}

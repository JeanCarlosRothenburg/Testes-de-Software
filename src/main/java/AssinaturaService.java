import java.util.List;
import java.util.ArrayList;

/**
 * Serviço para gerenciar inscrições e assinaturas dos usuários
 * RF032 - O sistema deve permitir gerenciar inscrições e assinaturas
 */
public class AssinaturaService {
    
    private AssinaturaDAO assinaturaDAO;
    private boolean sistemaAtivo;
    private String usuarioLogado;

    public AssinaturaService() {
        this.assinaturaDAO = new AssinaturaDAO(false); // Usar stub por padrão para testes
        this.sistemaAtivo = true;
        this.usuarioLogado = null;
    }

    public AssinaturaService(AssinaturaDAO assinaturaDAO) {
        this.assinaturaDAO = assinaturaDAO;
        this.sistemaAtivo = true;
        this.usuarioLogado = null;
    }

    /**
     * Lista todas as assinaturas de um usuário
     * @param usuarioId ID do usuário
     * @return Lista de assinaturas
     */
    public List<Assinatura> listarAssinaturasUsuario(String usuarioId) {
        if (!sistemaAtivo) {
            return new ArrayList<>();
        }

        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            List<Assinatura> assinaturas = assinaturaDAO.buscarAssinaturasPorUsuario(usuarioId);
            
            // Atualizar status de assinaturas vencidas
            for (Assinatura assinatura : assinaturas) {
                if (assinatura.isVencida() && assinatura.getStatus() == Assinatura.StatusAssinatura.ATIVA) {
                    assinatura.setStatus(Assinatura.StatusAssinatura.VENCIDA);
                    assinaturaDAO.atualizarStatusAssinatura(assinatura.getId(), Assinatura.StatusAssinatura.VENCIDA);
                }
            }
            
            return assinaturas;
        } catch (Exception e) {
            System.err.println("Erro ao listar assinaturas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lista apenas as assinaturas ativas de um usuário
     * @param usuarioId ID do usuário
     * @return Lista de assinaturas ativas
     */
    public List<Assinatura> listarAssinaturasAtivas(String usuarioId) {
        List<Assinatura> todasAssinaturas = listarAssinaturasUsuario(usuarioId);
        List<Assinatura> assinaturasAtivas = new ArrayList<>();
        
        for (Assinatura assinatura : todasAssinaturas) {
            if (assinatura.getStatus() == Assinatura.StatusAssinatura.ATIVA) {
                assinaturasAtivas.add(assinatura);
            }
        }
        
        return assinaturasAtivas;
    }

    /**
     * Altera o status de uma assinatura (ativa/inativa)
     * @param assinaturaId ID da assinatura
     * @param novoStatus Novo status
     * @return Resultado da operação
     */
    public ResultadoAlteracaoStatus alterarStatusAssinatura(String assinaturaId, 
                                                          Assinatura.StatusAssinatura novoStatus) {
        if (!sistemaAtivo) {
            return new ResultadoAlteracaoStatus(false, "Sistema temporariamente indisponível", null, null);
        }

        if (assinaturaId == null || assinaturaId.trim().isEmpty()) {
            return new ResultadoAlteracaoStatus(false, "ID da assinatura é obrigatório", null, null);
        }

        if (novoStatus == null) {
            return new ResultadoAlteracaoStatus(false, "Novo status é obrigatório", null, null);
        }

        try {
            // Buscar assinatura atual
            Assinatura assinatura = assinaturaDAO.buscarAssinaturaPorId(assinaturaId);
            
            if (assinatura == null) {
                return new ResultadoAlteracaoStatus(false, "Assinatura não encontrada", null, null);
            }

            Assinatura.StatusAssinatura statusAnterior = assinatura.getStatus();

            // Validar transição de status
            if (!isTransicaoStatusValida(statusAnterior, novoStatus)) {
                return new ResultadoAlteracaoStatus(false, 
                    "Transição de status inválida: " + statusAnterior + " -> " + novoStatus, 
                    statusAnterior, novoStatus);
            }

            // Atualizar status
            boolean sucesso = assinaturaDAO.atualizarStatusAssinatura(assinaturaId, novoStatus);
            
            if (sucesso) {
                return new ResultadoAlteracaoStatus(true, "Status alterado com sucesso", 
                    statusAnterior, novoStatus);
            } else {
                return new ResultadoAlteracaoStatus(false, "Erro ao atualizar status no banco", 
                    statusAnterior, novoStatus);
            }

        } catch (Exception e) {
            return new ResultadoAlteracaoStatus(false, "Erro interno: " + e.getMessage(), null, null);
        }
    }

    /**
     * Verifica se a transição de status é válida
     */
    private boolean isTransicaoStatusValida(Assinatura.StatusAssinatura statusAtual, 
                                          Assinatura.StatusAssinatura novoStatus) {
        // Regras de negócio para transições de status
        switch (statusAtual) {
            case ATIVA:
                return novoStatus == Assinatura.StatusAssinatura.INATIVA || 
                       novoStatus == Assinatura.StatusAssinatura.CANCELADA;
            case INATIVA:
                return novoStatus == Assinatura.StatusAssinatura.ATIVA || 
                       novoStatus == Assinatura.StatusAssinatura.CANCELADA;
            case CANCELADA:
                return novoStatus == Assinatura.StatusAssinatura.ATIVA; // Reativação
            case VENCIDA:
                return novoStatus == Assinatura.StatusAssinatura.ATIVA || 
                       novoStatus == Assinatura.StatusAssinatura.CANCELADA;
            default:
                return false;
        }
    }

    /**
     * Busca uma assinatura específica por ID
     * @param assinaturaId ID da assinatura
     * @return Assinatura encontrada ou null
     */
    public Assinatura buscarAssinaturaPorId(String assinaturaId) {
        if (!sistemaAtivo || assinaturaId == null || assinaturaId.trim().isEmpty()) {
            return null;
        }

        try {
            return assinaturaDAO.buscarAssinaturaPorId(assinaturaId);
        } catch (Exception e) {
            System.err.println("Erro ao buscar assinatura: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ativa uma assinatura
     * @param assinaturaId ID da assinatura
     * @return Resultado da operação
     */
    public ResultadoAlteracaoStatus ativarAssinatura(String assinaturaId) {
        return alterarStatusAssinatura(assinaturaId, Assinatura.StatusAssinatura.ATIVA);
    }

    /**
     * Desativa uma assinatura
     * @param assinaturaId ID da assinatura
     * @return Resultado da operação
     */
    public ResultadoAlteracaoStatus desativarAssinatura(String assinaturaId) {
        return alterarStatusAssinatura(assinaturaId, Assinatura.StatusAssinatura.INATIVA);
    }

    /**
     * Conta quantas assinaturas ativas um usuário possui
     * @param usuarioId ID do usuário
     * @return Número de assinaturas ativas
     */
    public int contarAssinaturasAtivas(String usuarioId) {
        return listarAssinaturasAtivas(usuarioId).size();
    }

    /**
     * Verifica se o usuário possui assinaturas
     * @param usuarioId ID do usuário
     * @return true se possui assinaturas, false caso contrário
     */
    public boolean usuarioPossuiAssinaturas(String usuarioId) {
        return !listarAssinaturasUsuario(usuarioId).isEmpty();
    }

    // ===== MÉTODOS PARA CONFIGURAÇÃO DE TESTES =====

    /**
     * Configura dados de teste
     * @param usuarioId ID do usuário
     * @param assinaturas Lista de assinaturas para teste
     */
    public void configurarDadosTeste(String usuarioId, List<Assinatura> assinaturas) {
        assinaturaDAO.configurarDadosTeste(usuarioId, assinaturas);
    }

    /**
     * Limpa todos os dados (útil para testes)
     */
    public void limparDados() {
        assinaturaDAO.limparDados();
    }

    /**
     * Define se o sistema está ativo
     */
    public void setSistemaAtivo(boolean ativo) {
        this.sistemaAtivo = ativo;
    }

    public boolean isSistemaAtivo() {
        return sistemaAtivo;
    }

    /**
     * Simula login de usuário
     */
    public void simularLogin(String usuarioId) {
        this.usuarioLogado = usuarioId;
    }

    /**
     * Simula logout
     */
    public void simularLogout() {
        this.usuarioLogado = null;
    }

    public String getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Define o DAO a ser usado (útil para testes)
     */
    public void setAssinaturaDAO(AssinaturaDAO dao) {
        this.assinaturaDAO = dao;
    }

    // ===== CLASSE INTERNA PARA RESULTADO =====

    public static class ResultadoAlteracaoStatus {
        private final boolean sucesso;
        private final String mensagem;
        private final Assinatura.StatusAssinatura statusAnterior;
        private final Assinatura.StatusAssinatura statusNovo;

        public ResultadoAlteracaoStatus(boolean sucesso, String mensagem, 
                                      Assinatura.StatusAssinatura statusAnterior, 
                                      Assinatura.StatusAssinatura statusNovo) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.statusAnterior = statusAnterior;
            this.statusNovo = statusNovo;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public Assinatura.StatusAssinatura getStatusAnterior() {
            return statusAnterior;
        }

        public Assinatura.StatusAssinatura getStatusNovo() {
            return statusNovo;
        }
    }
}

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Serviço para gerenciar histórico de navegação dos usuários
 * RF026 - O sistema deve permitir visualizar histórico de navegação do usuário
 */
public class HistoricoNavegacaoService {
    
    private Map<String, HistoricoNavegacao> historicosUsuarios;
    private boolean sistemaAtivo;

    public HistoricoNavegacaoService() {
        this.historicosUsuarios = new HashMap<>();
        this.sistemaAtivo = true;
    }

    /**
     * Registra a visita a um item pelo usuário
     * @param usuarioId ID do usuário
     * @param item Item visitado
     * @return Resultado da operação
     */
    public ResultadoRegistroVisita registrarVisitaItem(String usuarioId, ItemNavegacao item) {
        if (!sistemaAtivo) {
            return new ResultadoRegistroVisita(false, "Sistema temporariamente indisponível");
        }

        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            return new ResultadoRegistroVisita(false, "ID do usuário é obrigatório");
        }

        if (item == null) {
            return new ResultadoRegistroVisita(false, "Item é obrigatório");
        }

        if (!usuarioId.equals(item.getUsuarioId())) {
            return new ResultadoRegistroVisita(false, "Item não pertence ao usuário informado");
        }

        // Obter ou criar histórico para o usuário
        HistoricoNavegacao historico = historicosUsuarios.computeIfAbsent(usuarioId, 
            userId -> new HistoricoNavegacao(userId));

        // Registrar a visita
        historico.adicionarItem(item);

        return new ResultadoRegistroVisita(true, "Visita registrada com sucesso");
    }

    /**
     * Obtém o histórico de navegação de um usuário
     * @param usuarioId ID do usuário
     * @return Lista de itens visitados
     */
    public List<ItemNavegacao> obterHistoricoNavegacao(String usuarioId) {
        if (!sistemaAtivo) {
            return new ArrayList<>();
        }

        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        HistoricoNavegacao historico = historicosUsuarios.get(usuarioId);
        
        if (historico == null) {
            // Criar histórico vazio para novo usuário
            historico = new HistoricoNavegacao(usuarioId);
            historicosUsuarios.put(usuarioId, historico);
        }

        return historico.getItensVisitados();
    }

    /**
     * Verifica se um usuário possui histórico de navegação
     * @param usuarioId ID do usuário
     * @return true se possui histórico, false caso contrário
     */
    public boolean usuarioPossuiHistorico(String usuarioId) {
        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            return false;
        }

        HistoricoNavegacao historico = historicosUsuarios.get(usuarioId);
        return historico != null && !historico.isVazio();
    }

    /**
     * Limpa o histórico de um usuário
     * @param usuarioId ID do usuário
     * @return Resultado da operação
     */
    public ResultadoLimpezaHistorico limparHistoricoUsuario(String usuarioId) {
        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            return new ResultadoLimpezaHistorico(false, "ID do usuário é obrigatório");
        }

        HistoricoNavegacao historico = historicosUsuarios.get(usuarioId);
        
        if (historico == null) {
            return new ResultadoLimpezaHistorico(true, "Usuário não possui histórico");
        }

        historico.limparHistorico();
        return new ResultadoLimpezaHistorico(true, "Histórico limpo com sucesso");
    }

    /**
     * Simula a configuração do ambiente de teste com histórico pré-existente
     * @param usuarioId ID do usuário
     * @param itens Lista de itens para adicionar ao histórico
     */
    public void configurarHistoricoTeste(String usuarioId, List<ItemNavegacao> itens) {
        HistoricoNavegacao historico = new HistoricoNavegacao(usuarioId);
        
        if (itens != null) {
            for (ItemNavegacao item : itens) {
                if (item.getUsuarioId().equals(usuarioId)) {
                    historico.adicionarItem(item);
                }
            }
        }
        
        historicosUsuarios.put(usuarioId, historico);
    }

    /**
     * Remove todos os históricos (útil para testes)
     */
    public void limparTodosHistoricos() {
        historicosUsuarios.clear();
    }

    /**
     * Ativa/desativa o sistema (útil para testes de indisponibilidade)
     */
    public void setSistemaAtivo(boolean ativo) {
        this.sistemaAtivo = ativo;
    }

    public boolean isSistemaAtivo() {
        return sistemaAtivo;
    }

    // Classes internas para resultados
    public static class ResultadoRegistroVisita {
        private final boolean sucesso;
        private final String mensagem;

        public ResultadoRegistroVisita(boolean sucesso, String mensagem) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }
    }

    public static class ResultadoLimpezaHistorico {
        private final boolean sucesso;
        private final String mensagem;

        public ResultadoLimpezaHistorico(boolean sucesso, String mensagem) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }
    }
}

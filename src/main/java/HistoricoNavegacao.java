import java.util.List;
import java.util.ArrayList;

/**
 * Representa o histórico de navegação de um usuário
 */
public class HistoricoNavegacao {
    private String usuarioId;
    private List<ItemNavegacao> itensVisitados;
    private int limitePaginas;

    public HistoricoNavegacao(String usuarioId) {
        this.usuarioId = usuarioId;
        this.itensVisitados = new ArrayList<>();
        this.limitePaginas = 100; // Limite padrão de itens no histórico
    }

    public HistoricoNavegacao(String usuarioId, int limitePaginas) {
        this.usuarioId = usuarioId;
        this.itensVisitados = new ArrayList<>();
        this.limitePaginas = limitePaginas;
    }

    /**
     * Adiciona um item ao histórico de navegação
     * @param item Item a ser adicionado
     */
    public void adicionarItem(ItemNavegacao item) {
        if (item != null && item.getUsuarioId().equals(this.usuarioId)) {
            // Remove item se já existe (para mover para o topo)
            itensVisitados.removeIf(i -> i.getId().equals(item.getId()));
            
            // Adiciona no início da lista (mais recente)
            itensVisitados.add(0, item);
            
            // Mantém o limite de itens
            if (itensVisitados.size() > limitePaginas) {
                itensVisitados.remove(itensVisitados.size() - 1);
            }
        }
    }

    /**
     * Remove um item específico do histórico
     * @param itemId ID do item a ser removido
     */
    public void removerItem(String itemId) {
        itensVisitados.removeIf(item -> item.getId().equals(itemId));
    }

    /**
     * Limpa todo o histórico
     */
    public void limparHistorico() {
        itensVisitados.clear();
    }

    /**
     * Verifica se o histórico está vazio
     * @return true se vazio, false caso contrário
     */
    public boolean isVazio() {
        return itensVisitados.isEmpty();
    }

    /**
     * Obtém o número de itens no histórico
     * @return número de itens
     */
    public int getTamanho() {
        return itensVisitados.size();
    }

    // Getters e Setters
    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<ItemNavegacao> getItensVisitados() {
        return new ArrayList<>(itensVisitados); // Retorna cópia para evitar modificações externas
    }

    public void setItensVisitados(List<ItemNavegacao> itensVisitados) {
        this.itensVisitados = itensVisitados != null ? itensVisitados : new ArrayList<>();
    }

    public int getLimitePaginas() {
        return limitePaginas;
    }

    public void setLimitePaginas(int limitePaginas) {
        this.limitePaginas = limitePaginas;
    }
}

import java.time.LocalDateTime;

/**
 * Representa um item visitado no histórico de navegação
 */
public class ItemNavegacao {
    private String id;
    private String nome;
    private String categoria;
    private double preco;
    private LocalDateTime dataVisita;
    private String usuarioId;

    public ItemNavegacao() {
    }

    public ItemNavegacao(String id, String nome, String categoria, double preco, String usuarioId) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.usuarioId = usuarioId;
        this.dataVisita = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public LocalDateTime getDataVisita() {
        return dataVisita;
    }

    public void setDataVisita(LocalDateTime dataVisita) {
        this.dataVisita = dataVisita;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "ItemNavegacao{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", preco=" + preco +
                ", dataVisita=" + dataVisita +
                ", usuarioId='" + usuarioId + '\'' +
                '}';
    }
}

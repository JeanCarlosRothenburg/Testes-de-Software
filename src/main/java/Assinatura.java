import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Representa uma inscrição/assinatura do usuário
 */
public class Assinatura {
    private String id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private TipoAssinatura tipo;
    private StatusAssinatura status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String usuarioId;
    private boolean autoRenovacao;

    public Assinatura() {
    }

    public Assinatura(String id, String nome, String descricao, BigDecimal preco, 
                     TipoAssinatura tipo, String usuarioId) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.status = StatusAssinatura.ATIVA;
        this.dataInicio = LocalDateTime.now();
        this.dataFim = calcularDataFim();
        this.autoRenovacao = false;
    }

    private LocalDateTime calcularDataFim() {
        if (dataInicio == null) {
            dataInicio = LocalDateTime.now();
        }
        
        switch (tipo) {
            case MENSAL:
                return dataInicio.plusMonths(1);
            case TRIMESTRAL:
                return dataInicio.plusMonths(3);
            case SEMESTRAL:
                return dataInicio.plusMonths(6);
            case ANUAL:
                return dataInicio.plusYears(1);
            default:
                return dataInicio.plusMonths(1);
        }
    }

    /**
     * Verifica se a assinatura está vencida
     */
    public boolean isVencida() {
        return dataFim != null && LocalDateTime.now().isAfter(dataFim);
    }

    /**
     * Ativa a assinatura
     */
    public void ativar() {
        if (this.status == StatusAssinatura.CANCELADA) {
            // Reativar assinatura cancelada
            this.dataInicio = LocalDateTime.now();
            this.dataFim = calcularDataFim();
        }
        this.status = StatusAssinatura.ATIVA;
    }

    /**
     * Desativa/suspende a assinatura
     */
    public void desativar() {
        this.status = StatusAssinatura.INATIVA;
    }

    /**
     * Cancela a assinatura permanentemente
     */
    public void cancelar() {
        this.status = StatusAssinatura.CANCELADA;
        this.autoRenovacao = false;
    }

    // Enums
    public enum TipoAssinatura {
        MENSAL("Mensal"),
        TRIMESTRAL("Trimestral"),
        SEMESTRAL("Semestral"),
        ANUAL("Anual");

        private final String descricao;

        TipoAssinatura(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusAssinatura {
        ATIVA("Ativa"),
        INATIVA("Inativa"),
        CANCELADA("Cancelada"),
        VENCIDA("Vencida");

        private final String descricao;

        StatusAssinatura(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public TipoAssinatura getTipo() {
        return tipo;
    }

    public void setTipo(TipoAssinatura tipo) {
        this.tipo = tipo;
        if (dataInicio != null) {
            this.dataFim = calcularDataFim();
        }
    }

    public StatusAssinatura getStatus() {
        return status;
    }

    public void setStatus(StatusAssinatura status) {
        this.status = status;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
        this.dataFim = calcularDataFim();
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public boolean isAutoRenovacao() {
        return autoRenovacao;
    }

    public void setAutoRenovacao(boolean autoRenovacao) {
        this.autoRenovacao = autoRenovacao;
    }

    @Override
    public String toString() {
        return "Assinatura{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", status=" + status +
                ", tipo=" + tipo +
                ", preco=" + preco +
                ", usuarioId='" + usuarioId + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                '}';
    }
}

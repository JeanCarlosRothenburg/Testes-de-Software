public class MetodoPagamento {
    private String numeroCartao;
    private String validade;
    private String cvv;
    private String nomePortador;
    private TipoCartao tipoCartao;

    public enum TipoCartao {
        CREDITO, DEBITO
    }

    public MetodoPagamento(String numeroCartao, String validade, String cvv, String nomePortador, TipoCartao tipoCartao) {
        this.numeroCartao = numeroCartao;
        this.validade = validade;
        this.cvv = cvv;
        this.nomePortador = nomePortador;
        this.tipoCartao = tipoCartao;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getValidade() {
        return validade;
    }

    public String getCvv() {
        return cvv;
    }

    public String getNomePortador() {
        return nomePortador;
    }

    public TipoCartao getTipoCartao() {
        return tipoCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setNomePortador(String nomePortador) {
        this.nomePortador = nomePortador;
    }

    public void setTipoCartao(TipoCartao tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    // Método para obter os 4 últimos dígitos do cartão (para exibição segura)
    public String getUltimosDigitos() {
        if (numeroCartao != null && numeroCartao.length() >= 4) {
            return numeroCartao.substring(numeroCartao.length() - 4);
        }
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MetodoPagamento that = (MetodoPagamento) obj;
        return numeroCartao != null ? numeroCartao.equals(that.numeroCartao) : that.numeroCartao == null;
    }

    @Override
    public int hashCode() {
        return numeroCartao != null ? numeroCartao.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MetodoPagamento{" +
                "numeroCartao='****" + getUltimosDigitos() + '\'' +
                ", validade='" + validade + '\'' +
                ", nomePortador='" + nomePortador + '\'' +
                ", tipoCartao=" + tipoCartao +
                '}';
    }
}

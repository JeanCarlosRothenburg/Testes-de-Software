public class ValePresente {
    private String codigo;
    private double valor;
    private boolean ativo;
    private boolean valido;

    public ValePresente(String codigo, double valor, boolean ativo, boolean valido) {
        this.codigo = codigo;
        this.valor = valor;
        this.ativo = ativo;
        this.valido = valido;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getValor() {
        return valor;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

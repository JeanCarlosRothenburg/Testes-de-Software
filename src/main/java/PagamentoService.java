import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PagamentoService {
    
    private List<MetodoPagamento> metodosPagamento;
    private boolean usuarioLogado;
    private String usuarioAtual;

    public PagamentoService() {
        this.metodosPagamento = new ArrayList<>();
        this.usuarioLogado = false;
        this.usuarioAtual = null;
    }

    /**
     * Método para adicionar método de pagamento
     * @param numeroCartao Número do cartão
     * @param validade Data de validade do cartão
     * @param cvv Código de segurança
     * @param nomePortador Nome do portador do cartão
     * @param tipoCartao Tipo do cartão (CREDITO ou DEBITO)
     * @return Resultado da operação
     */
    public ResultadoValidacaoPagamento adicionarMetodoPgto(String numeroCartao, String validade, String cvv, String nomePortador, MetodoPagamento.TipoCartao tipoCartao) {
        // Verificar pré-condições
        if (!usuarioLogado) {
            return new ResultadoValidacaoPagamento(false, "Usuário deve estar logado", null, false);
        }

        // Validar entradas
        if (numeroCartao == null || numeroCartao.trim().isEmpty()) {
            return new ResultadoValidacaoPagamento(false, "Número do cartão é obrigatório", null, false);
        }

        if (validade == null || validade.trim().isEmpty()) {
            return new ResultadoValidacaoPagamento(false, "Validade é obrigatória", null, false);
        }

        if (cvv == null || cvv.trim().isEmpty()) {
            return new ResultadoValidacaoPagamento(false, "CVV é obrigatório", null, false);
        }

        if (nomePortador == null || nomePortador.trim().isEmpty()) {
            return new ResultadoValidacaoPagamento(false, "Nome do portador é obrigatório", null, false);
        }

        // Remover espaços do número do cartão para validação
        String numeroLimpo = numeroCartao.replaceAll("\\s+", "");

        // Validar formato do número do cartão
        if (!validarFormatoNumeroCartao(numeroLimpo)) {
            return new ResultadoValidacaoPagamento(false, "Formato do número do cartão inválido", null, false);
        }

        // Validar formato da validade (MM/DD/YYYY)
        if (!validarFormatoValidade(validade)) {
            return new ResultadoValidacaoPagamento(false, "Formato da validade inválido", null, false);
        }

        // Validar formato do CVV
        if (!validarFormatoCVV(cvv)) {
            return new ResultadoValidacaoPagamento(false, "Formato do CVV inválido", null, false);
        }

        // Simular validação com operadora
        boolean validacaoOperadora = simularValidacaoOperadora(numeroLimpo);

        // Criar método de pagamento
        MetodoPagamento novoMetodo = new MetodoPagamento(numeroLimpo, validade, cvv, nomePortador, tipoCartao);

        // Se a validação da operadora passou, salvar o método
        if (validacaoOperadora) {
            metodosPagamento.add(novoMetodo);
            return new ResultadoValidacaoPagamento(true, "Método de pagamento adicionado com sucesso", novoMetodo, true);
        } else {
            return new ResultadoValidacaoPagamento(false, "Cartão não aprovado pela operadora", novoMetodo, false);
        }
    }

    /**
     * Simula validação com a operadora do cartão
     * Cartões com número específico são considerados válidos para teste
     */
    private boolean simularValidacaoOperadora(String numeroCartao) {
        // Para os testes, o cartão 5103517924944745 é considerado válido
        // O cartão 1111222233334444 é considerado inválido
        return "5103517924944745".equals(numeroCartao);
    }

    /**
     * Valida formato do número do cartão (deve ter entre 13 e 19 dígitos)
     */
    private boolean validarFormatoNumeroCartao(String numeroCartao) {
        if (numeroCartao == null) return false;
        return Pattern.matches("\\d{13,19}", numeroCartao);
    }

    /**
     * Valida formato da validade (MM/DD/YYYY)
     */
    private boolean validarFormatoValidade(String validade) {
        if (validade == null) return false;
        return Pattern.matches("\\d{2}/\\d{2}/\\d{4}", validade);
    }

    /**
     * Valida formato do CVV (3 ou 4 dígitos)
     */
    private boolean validarFormatoCVV(String cvv) {
        if (cvv == null) return false;
        return Pattern.matches("\\d{3,4}", cvv);
    }

    /**
     * Simula login do usuário
     */
    public void simularLogin(String usuario) {
        this.usuarioLogado = true;
        this.usuarioAtual = usuario;
    }

    /**
     * Simula logout do usuário
     */
    public void simularLogout() {
        this.usuarioLogado = false;
        this.usuarioAtual = null;
    }

    /**
     * Retorna a lista de métodos de pagamento
     */
    public List<MetodoPagamento> getMetodosPagamento() {
        return new ArrayList<>(metodosPagamento);
    }

    /**
     * Limpa todos os métodos de pagamento (para testes)
     */
    public void limparMetodosPagamento() {
        this.metodosPagamento.clear();
    }

    /**
     * Verifica se um método de pagamento existe
     */
    public boolean existeMetodoPagamento(String numeroCartao) {
        String numeroLimpo = numeroCartao.replaceAll("\\s+", "");
        return metodosPagamento.stream().anyMatch(metodo -> metodo.getNumeroCartao().equals(numeroLimpo));
    }

    public String getUsuarioAtual() {
        return usuarioAtual;
    }

    public boolean isUsuarioLogado() {
        return usuarioLogado;
    }

    // Classe interna para retornar resultado da validação
    public static class ResultadoValidacaoPagamento {
        private boolean sucesso;
        private String mensagem;
        private MetodoPagamento metodoPagamento;
        private boolean validadoOperadora;

        public ResultadoValidacaoPagamento(boolean sucesso, String mensagem, MetodoPagamento metodoPagamento, boolean validadoOperadora) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.metodoPagamento = metodoPagamento;
            this.validadoOperadora = validadoOperadora;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public MetodoPagamento getMetodoPagamento() {
            return metodoPagamento;
        }

        public boolean isValidadoOperadora() {
            return validadoOperadora;
        }
    }
}

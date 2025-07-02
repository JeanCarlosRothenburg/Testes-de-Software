public class CarrinhoService {
    private double saldoCarteira = 0.0;

    public String adicionarProduto(String nome, int estoque) {
        if (estoque > 0) {
            return "Produto adicionado ao carrinho";
        } else {
            return "Produto sem estoque";
        }
    }

    public double calcularValorCarrinho(double[] precos) {
        double total = 0;
        for (double preco : precos) {
            total += preco;
        }
        return total;
    }

    public ResultadoValePresente adicionarSaldoValePresente(String codigoCupom, double valorVale, double totalCarrinho) {
        // Simula verificação do vale presente
        ValePresente vale = buscarValePresentePorCodigo(codigoCupom);

        if (vale == null || !vale.isAtivo() || !vale.isValido()) {
            return new ResultadoValePresente(false, "Vale presente inválido ou inativo", totalCarrinho, 0.0);
        }

        if (valorVale <= 0) {
            return new ResultadoValePresente(false, "Valor do vale deve ser positivo", totalCarrinho, 0.0);
        }

        double valorFinalPagamento;
        double valorSalvoCarteira = 0.0;

        if (valorVale >= totalCarrinho) {
            // Vale é maior ou igual ao carrinho - zera o pagamento e salva o resto na carteira
            valorFinalPagamento = 0.0;
            valorSalvoCarteira = valorVale - totalCarrinho;
            this.saldoCarteira += valorSalvoCarteira;
        } else {
            // Vale é menor que o carrinho - aplica desconto
            valorFinalPagamento = totalCarrinho - valorVale;
        }

        return new ResultadoValePresente(true, "Desconto aplicado com sucesso", valorFinalPagamento, valorSalvoCarteira);
    }

    private ValePresente buscarValePresentePorCodigo(String codigo) {
        // Simula a busca do vale presente no sistema
        // Para os testes, vamos considerar VALE123 como um vale válido
        if ("VALE123".equals(codigo)) {
            return new ValePresente(codigo, 100.0, true, true);
        }
        return null;
    }

    public double getSaldoCarteira() {
        return saldoCarteira;
    }

    public void limparSaldoCarteira() {
        this.saldoCarteira = 0.0;
    }

    // Classe interna para retornar resultado da aplicação do vale
    public static class ResultadoValePresente {
        private boolean sucesso;
        private String mensagem;
        private double valorFinalPagamento;
        private double valorSalvoCarteira;

        public ResultadoValePresente(boolean sucesso, String mensagem, double valorFinalPagamento, double valorSalvoCarteira) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.valorFinalPagamento = valorFinalPagamento;
            this.valorSalvoCarteira = valorSalvoCarteira;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public double getValorFinalPagamento() {
            return valorFinalPagamento;
        }

        public double getValorSalvoCarteira() {
            return valorSalvoCarteira;
        }
    }
}

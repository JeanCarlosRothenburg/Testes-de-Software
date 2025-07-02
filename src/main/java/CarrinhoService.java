public class CarrinhoService {
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
}

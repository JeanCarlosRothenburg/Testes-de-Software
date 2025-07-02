public class CheckoutService {
    public boolean verificarDados(String endereco, String pagamento) {
        return endereco != null && !endereco.isEmpty() && pagamento != null && !pagamento.isEmpty();
    }
}


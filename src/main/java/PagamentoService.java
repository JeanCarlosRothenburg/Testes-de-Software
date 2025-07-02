public class PagamentoService {
    public String selecionarFormaDePagamento(String forma) {
        if (forma == null || forma.isEmpty()) {
            return "Forma de pagamento inválida";
        }
        return "Pagamento efetuado com " + forma;
    }
}


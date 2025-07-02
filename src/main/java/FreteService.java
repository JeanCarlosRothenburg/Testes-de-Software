public class FreteService {
    public String calcularFrete(String cep) {
        if ("11001-000".equals(cep)) {
            return "Frete: R$15,00";
        } else {
            return "Nenhuma opção de envio encontrada";
        }
    }
}


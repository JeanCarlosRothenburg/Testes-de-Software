public class FreteService {
    public String calcularFrete(String cep) {
        if (cep == null || !cep.matches("\\d{5}-\\d{3}")) { //Valida se o CEP é correto
            return "Nenhuma opção de envio encontrada";
        }
        else{
            return "Frete: R$15,00";
        }


    }
}
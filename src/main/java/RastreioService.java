public class RastreioService {
    public String rastrearEncomenda(String codigo) {
        if (codigo == null || !codigo.matches("[A-Z]{2}\\d{7}[A-Z]{2}")) { //Valida se o formato do código é correto
            return "Código de rastreio inválido";
        }
        else{
            return "Encomenda em trânsito - São Paulo/SP";
        }
    }
}
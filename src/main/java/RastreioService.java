public class RastreioService {
    public String rastrearEncomenda(String codigo) {
        if ("QM7638493BR".equals(codigo)) {
            return "Encomenda em trânsito - São Paulo/SP";
        } else {
            return "Código de rastreio inválido";
        }
    }
}


public class CompraService {

    public String comprarDireto(String produto, boolean estaLogado) {
        if (estaLogado) {
            return "Compra realizada com sucesso";
        } else {
            return "Usuário precisa estar logado";
        }
    }
}

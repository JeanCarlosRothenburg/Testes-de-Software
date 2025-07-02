public class AvaliacaoService {

    public String avaliarCompra(String comentario) {
        if (comentario.length() >= 20 && comentario.length() <= 500) {
            return "Avaliação registrada com sucesso";
        } else {
            return "Comentário deve ter entre 20 e 500 caracteres";
        }
    }
}

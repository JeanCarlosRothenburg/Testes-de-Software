import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoService {

    private List<String> produtos = Arrays.asList("alexa", "notebook", "fone bluetooth");

    public List<String> buscarProduto(String termo) {
        return produtos.stream()
                .filter(p -> p.contains(termo.toLowerCase()))
                .collect(Collectors.toList());
    }
}

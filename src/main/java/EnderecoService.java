import java.util.HashSet;
import java.util.Set;

public class EnderecoService {
    private Set<String> enderecosCadastrados = new HashSet<>();

    public String adicionarEndereco(String endereco) {
        if (endereco == null || !endereco.toLowerCase().contains("rua")) {
            return "Endereço inválido";
        }
        if (enderecosCadastrados.contains(endereco)) {
            return "Endereço já cadastrado";
        }
        enderecosCadastrados.add(endereco);
        return "Endereço adicionado com sucesso";
    }
}

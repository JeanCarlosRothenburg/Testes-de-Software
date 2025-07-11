import java.util.HashSet;
import java.util.Set;

public class EnderecoService {
    private Set<String> enderecosCadastrados = new HashSet<>(); //Armazena os endereços cadastrados e evita que haja endereços duplicados

    public String adicionarEndereco(String endereco) {
        if (endereco == null || !endereco.toLowerCase().contains("rua")) {
            return "Endereço inválido";
        }
        if (enderecosCadastrados.contains(endereco)) { //Verifica se o endereço já foi cadastrado
            return "Endereço já cadastrado";
        }
        enderecosCadastrados.add(endereco);
        return "Endereço adicionado com sucesso";
    }
}
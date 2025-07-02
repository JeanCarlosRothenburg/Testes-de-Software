import java.util.ArrayList;
import java.util.List;

public class LoginService {

    List<Usuario> usuariosCadastrados = new ArrayList<>();

    public LoginService() {

        //"Mockando" um DB
        usuariosCadastrados.add(new Usuario("lucasteste@email.com", "123456"));
    }

    public boolean realizarLogin(String email, String senha) {
        for (Usuario u : usuariosCadastrados) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return true; // Login bem-sucedido
            }
        }
        return false; // Falha no login
    }
}


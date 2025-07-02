import java.util.ArrayList;
import java.util.List;

public class AmazonFamilyService {
    
    private List<UsuarioFamily> membrosFamily;
    private boolean usuarioLogado;
    private boolean ehAdministrador;

    public AmazonFamilyService() {
        this.membrosFamily = new ArrayList<>();
        this.usuarioLogado = false;
        this.ehAdministrador = false;
    }

    /**
     * Método para adicionar usuário ao Amazon Family
     * @param id ID do usuário
     * @param nome Nome do usuário
     * @param ehAdulto Se o usuário é adulto ou não
     * @return Resultado da operação
     */
    public ResultadoAdicaoFamily adicionarUsuarioFamily(String id, String nome, boolean ehAdulto) {
        // Verificar pré-condições
        if (!usuarioLogado) {
            return new ResultadoAdicaoFamily(false, "Usuário deve estar logado", null);
        }

        if (!ehAdministrador) {
            return new ResultadoAdicaoFamily(false, "Usuário deve ser administrador da conta", null);
        }

        // Validar entrada
        if (id == null || id.trim().isEmpty()) {
            return new ResultadoAdicaoFamily(false, "ID do usuário é obrigatório", null);
        }

        if (nome == null || nome.trim().isEmpty()) {
            return new ResultadoAdicaoFamily(false, "Nome do usuário é obrigatório", null);
        }

        // Validar limite de caracteres do nome (máximo 50)
        if (nome.length() > 50) {
            return new ResultadoAdicaoFamily(false, "Nome deve ter no máximo 50 caracteres", null);
        }

        // Verificar se já existe usuário com mesmo ID
        for (UsuarioFamily membro : membrosFamily) {
            if (membro.getId().equals(id)) {
                return new ResultadoAdicaoFamily(false, "Usuário com este ID já existe", null);
            }
        }

        // Criar e adicionar o usuário
        UsuarioFamily novoMembro = new UsuarioFamily(id, nome, ehAdulto);
        membrosFamily.add(novoMembro);

        return new ResultadoAdicaoFamily(true, "Usuário salvo com sucesso", novoMembro);
    }

    /**
     * Simula login do usuário administrador
     */
    public void simularLoginAdministrador() {
        this.usuarioLogado = true;
        this.ehAdministrador = true;
    }

    /**
     * Simula logout do usuário
     */
    public void simularLogout() {
        this.usuarioLogado = false;
        this.ehAdministrador = false;
    }

    /**
     * Retorna a lista de membros do Amazon Family
     */
    public List<UsuarioFamily> getMembrosFamily() {
        return new ArrayList<>(membrosFamily);
    }

    /**
     * Limpa a lista de membros (para testes)
     */
    public void limparMembros() {
        this.membrosFamily.clear();
    }

    /**
     * Verifica se um usuário existe na família
     */
    public boolean existeUsuario(String id) {
        return membrosFamily.stream().anyMatch(membro -> membro.getId().equals(id));
    }

    /**
     * Busca um usuário pelo ID
     */
    public UsuarioFamily buscarUsuarioPorId(String id) {
        return membrosFamily.stream()
                .filter(membro -> membro.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Classe interna para retornar resultado da adição
    public static class ResultadoAdicaoFamily {
        private boolean sucesso;
        private String mensagem;
        private UsuarioFamily usuario;

        public ResultadoAdicaoFamily(boolean sucesso, String mensagem, UsuarioFamily usuario) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.usuario = usuario;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public UsuarioFamily getUsuario() {
            return usuario;
        }
    }
}

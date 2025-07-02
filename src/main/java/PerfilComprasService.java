import java.util.ArrayList;
import java.util.List;

public class PerfilComprasService {
    
    private List<PerfilCompras> perfisCompras;
    private boolean usuarioLogado;
    private String usuarioAtual;

    public PerfilComprasService() {
        this.perfisCompras = new ArrayList<>();
        this.usuarioLogado = false;
        this.usuarioAtual = null;
    }

    /**
     * Método para adicionar perfil de compras
     * @param nomePerfil Nome do perfil de compras
     * @return Resultado da operação
     */
    public ResultadoCriacaoPerfil adicionarPerfilCompras(String nomePerfil) {
        // Verificar pré-condições
        if (!usuarioLogado) {
            return new ResultadoCriacaoPerfil(false, "Usuário deve estar logado", null);
        }

        if (usuarioAtual == null || usuarioAtual.trim().isEmpty()) {
            return new ResultadoCriacaoPerfil(false, "Usuário atual não identificado", null);
        }

        // Validar entrada
        if (nomePerfil == null || nomePerfil.trim().isEmpty()) {
            return new ResultadoCriacaoPerfil(false, "Nome do perfil é obrigatório", null);
        }

        // Validar limite de caracteres do nome (máximo 50)
        if (nomePerfil.length() > 50) {
            return new ResultadoCriacaoPerfil(false, "Nome deve ter no máximo 50 caracteres", null);
        }

        // Verificar se já existe perfil com mesmo nome para este usuário
        for (PerfilCompras perfil : perfisCompras) {
            if (perfil.getNome().equals(nomePerfil) && perfil.getUsuario().equals(usuarioAtual)) {
                return new ResultadoCriacaoPerfil(false, "Perfil com este nome já existe", null);
            }
        }

        // Criar e adicionar o perfil
        PerfilCompras novoPerfil = new PerfilCompras(nomePerfil, usuarioAtual);
        perfisCompras.add(novoPerfil);

        return new ResultadoCriacaoPerfil(true, "Novo perfil criado com sucesso", novoPerfil);
    }

    /**
     * Simula login do usuário
     */
    public void simularLogin(String usuario) {
        this.usuarioLogado = true;
        this.usuarioAtual = usuario;
    }

    /**
     * Simula logout do usuário
     */
    public void simularLogout() {
        this.usuarioLogado = false;
        this.usuarioAtual = null;
    }

    /**
     * Retorna a lista de perfis de compras do usuário atual
     */
    public List<PerfilCompras> getPerfisComprasUsuarioAtual() {
        if (!usuarioLogado || usuarioAtual == null) {
            return new ArrayList<>();
        }
        
        List<PerfilCompras> perfisDoUsuario = new ArrayList<>();
        for (PerfilCompras perfil : perfisCompras) {
            if (perfil.getUsuario().equals(usuarioAtual)) {
                perfisDoUsuario.add(perfil);
            }
        }
        return perfisDoUsuario;
    }

    /**
     * Retorna todos os perfis (para testes)
     */
    public List<PerfilCompras> getTodosPerfis() {
        return new ArrayList<>(perfisCompras);
    }

    /**
     * Limpa todos os perfis (para testes)
     */
    public void limparPerfis() {
        this.perfisCompras.clear();
    }

    /**
     * Verifica se um perfil existe para o usuário atual
     */
    public boolean existePerfilParaUsuarioAtual(String nomePerfil) {
        if (!usuarioLogado || usuarioAtual == null) {
            return false;
        }
        
        return perfisCompras.stream().anyMatch(perfil -> 
            perfil.getNome().equals(nomePerfil) && perfil.getUsuario().equals(usuarioAtual));
    }

    /**
     * Busca um perfil pelo nome para o usuário atual
     */
    public PerfilCompras buscarPerfilPorNome(String nomePerfil) {
        if (!usuarioLogado || usuarioAtual == null) {
            return null;
        }
        
        return perfisCompras.stream()
                .filter(perfil -> perfil.getNome().equals(nomePerfil) && perfil.getUsuario().equals(usuarioAtual))
                .findFirst()
                .orElse(null);
    }

    public String getUsuarioAtual() {
        return usuarioAtual;
    }

    public boolean isUsuarioLogado() {
        return usuarioLogado;
    }

    // Classe interna para retornar resultado da criação
    public static class ResultadoCriacaoPerfil {
        private boolean sucesso;
        private String mensagem;
        private PerfilCompras perfil;

        public ResultadoCriacaoPerfil(boolean sucesso, String mensagem, PerfilCompras perfil) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
            this.perfil = perfil;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }

        public PerfilCompras getPerfil() {
            return perfil;
        }
    }
}

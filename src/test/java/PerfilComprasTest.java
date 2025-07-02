import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários para funcionalidade de Perfil de Compras - CT066 e CT067
 * RF033 - Gerenciamento de Perfil de Compras
 * RT037 - Teste de criação de perfis de compras
 */
public class PerfilComprasTest {

    private PerfilComprasService perfilComprasService;
    private final String USUARIO_TESTE = "usuario.teste@email.com";

    @Before
    public void setUp() {
        perfilComprasService = new PerfilComprasService();
        perfilComprasService.limparPerfis(); // Garante que a lista está vazia
        perfilComprasService.simularLogin(USUARIO_TESTE); // Simula usuário logado
    }

    /**
     * CT066 - Verificar barramento da criação de novos perfis com nome com mais de 50 caracteres
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * 
     * Entradas:
     * - Nome do novo perfil = "Testeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" (mais de 50 caracteres)
     * 
     * Resultado Esperado:
     * - Novo perfil não criado
     * 
     * Pós-condições:
     * - O perfil não aparece na lista de perfis associados à conta
     */
    @Test
    public void ct066_criarPerfilComNomeMuitoLongo() {
        // Arrange
        // Nome com 51 caracteres (mais que o limite de 50)
        String nomePerfilLongo = "Testeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";

        // Verificar que o nome tem mais de 50 caracteres
        Assert.assertTrue("Nome deve ter mais de 50 caracteres para o teste", nomePerfilLongo.length() > 50);

        // Act
        PerfilComprasService.ResultadoCriacaoPerfil resultado = 
            perfilComprasService.adicionarPerfilCompras(nomePerfilLongo);

        // Assert
        Assert.assertFalse("Perfil com nome muito longo não deve ser criado", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Nome deve ter no máximo 50 caracteres", resultado.getMensagem());
        Assert.assertNull("Perfil não deve ser retornado", resultado.getPerfil());
        
        // Verificar pós-condições
        Assert.assertFalse("Perfil não deve existir na lista", perfilComprasService.existePerfilParaUsuarioAtual(nomePerfilLongo));
        Assert.assertEquals("Lista de perfis deve estar vazia", 0, perfilComprasService.getPerfisComprasUsuarioAtual().size());
    }

    /**
     * CT067 - Verificar se a criação de novos perfis funciona corretamente
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * 
     * Entradas:
     * - Nome do novo perfil = "PerfilTeste"
     * 
     * Resultado Esperado:
     * - Novo perfil criado com sucesso
     * 
     * Pós-condições:
     * - O novo perfil aparece na lista de perfis associados à conta
     */
    @Test
    public void ct067_criarPerfilComSucesso() {
        // Arrange
        String nomePerfilValido = "PerfilTeste";

        // Act
        PerfilComprasService.ResultadoCriacaoPerfil resultado = 
            perfilComprasService.adicionarPerfilCompras(nomePerfilValido);

        // Assert
        Assert.assertTrue("Perfil deve ser criado com sucesso", resultado.isSucesso());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Novo perfil criado com sucesso", resultado.getMensagem());
        Assert.assertNotNull("Perfil deve ser retornado", resultado.getPerfil());
        Assert.assertEquals("Nome do perfil deve estar correto", nomePerfilValido, resultado.getPerfil().getNome());
        Assert.assertEquals("Usuário do perfil deve estar correto", USUARIO_TESTE, resultado.getPerfil().getUsuario());
        
        // Verificar pós-condições
        Assert.assertTrue("Perfil deve existir na lista", perfilComprasService.existePerfilParaUsuarioAtual(nomePerfilValido));
        Assert.assertEquals("Lista deve conter 1 perfil", 1, perfilComprasService.getPerfisComprasUsuarioAtual().size());
        
        // Verificar se o perfil pode ser encontrado
        PerfilCompras perfilEncontrado = perfilComprasService.buscarPerfilPorNome(nomePerfilValido);
        Assert.assertNotNull("Perfil deve ser encontrado", perfilEncontrado);
        Assert.assertEquals("Nome do perfil encontrado deve estar correto", nomePerfilValido, perfilEncontrado.getNome());
    }

    /**
     * Teste adicional - Usuário sem login
     */
    @Test
    public void testCriarPerfilSemLogin() {
        // Arrange
        perfilComprasService.simularLogout(); // Simula logout
        String nomePerfil = "PerfilSemLogin";

        // Act
        PerfilComprasService.ResultadoCriacaoPerfil resultado = 
            perfilComprasService.adicionarPerfilCompras(nomePerfil);

        // Assert
        Assert.assertFalse("Usuário sem login não deve conseguir criar perfil", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Usuário deve estar logado", resultado.getMensagem());
    }

    /**
     * Teste adicional - Nome de perfil duplicado
     */
    @Test
    public void testCriarPerfilComNomeDuplicado() {
        // Arrange
        String nomePerfil = "PerfilDuplicado";

        // Criar primeiro perfil
        perfilComprasService.adicionarPerfilCompras(nomePerfil);

        // Act - Tentar criar segundo perfil com mesmo nome
        PerfilComprasService.ResultadoCriacaoPerfil resultado = 
            perfilComprasService.adicionarPerfilCompras(nomePerfil);

        // Assert
        Assert.assertFalse("Não deve permitir nome duplicado", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Perfil com este nome já existe", resultado.getMensagem());
        Assert.assertEquals("Deve haver apenas 1 perfil na lista", 1, perfilComprasService.getPerfisComprasUsuarioAtual().size());
    }

    /**
     * Teste adicional - Nome de perfil vazio
     */
    @Test
    public void testCriarPerfilComNomeVazio() {
        // Arrange
        String nomePerfilVazio = "";

        // Act
        PerfilComprasService.ResultadoCriacaoPerfil resultado = 
            perfilComprasService.adicionarPerfilCompras(nomePerfilVazio);

        // Assert
        Assert.assertFalse("Perfil com nome vazio não deve ser criado", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Nome do perfil é obrigatório", resultado.getMensagem());
    }

    /**
     * Teste adicional - Nome com exatamente 50 caracteres (limite válido)
     */
    @Test
    public void testCriarPerfilComNomeExatamente50Caracteres() {
        // Arrange
        // Nome com exatamente 50 caracteres
        String nomePerfilLimite = "01234567890123456789012345678901234567890123456789"; // 50 chars

        // Verificar que o nome tem exatamente 50 caracteres
        Assert.assertEquals("Nome deve ter exatamente 50 caracteres", 50, nomePerfilLimite.length());

        // Act
        PerfilComprasService.ResultadoCriacaoPerfil resultado = 
            perfilComprasService.adicionarPerfilCompras(nomePerfilLimite);

        // Assert
        Assert.assertTrue("Perfil com nome de 50 caracteres deve ser criado", resultado.isSucesso());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Novo perfil criado com sucesso", resultado.getMensagem());
        Assert.assertTrue("Perfil deve existir na lista", perfilComprasService.existePerfilParaUsuarioAtual(nomePerfilLimite));
    }

    /**
     * Teste adicional - Múltiplos perfis para o mesmo usuário
     */
    @Test
    public void testCriarMultiplosPerfis() {
        // Arrange
        String perfil1 = "Perfil1";
        String perfil2 = "Perfil2";
        String perfil3 = "Perfil3";

        // Act
        PerfilComprasService.ResultadoCriacaoPerfil resultado1 = perfilComprasService.adicionarPerfilCompras(perfil1);
        PerfilComprasService.ResultadoCriacaoPerfil resultado2 = perfilComprasService.adicionarPerfilCompras(perfil2);
        PerfilComprasService.ResultadoCriacaoPerfil resultado3 = perfilComprasService.adicionarPerfilCompras(perfil3);

        // Assert
        Assert.assertTrue("Primeiro perfil deve ser criado", resultado1.isSucesso());
        Assert.assertTrue("Segundo perfil deve ser criado", resultado2.isSucesso());
        Assert.assertTrue("Terceiro perfil deve ser criado", resultado3.isSucesso());
        Assert.assertEquals("Deve haver 3 perfis na lista", 3, perfilComprasService.getPerfisComprasUsuarioAtual().size());
    }
}

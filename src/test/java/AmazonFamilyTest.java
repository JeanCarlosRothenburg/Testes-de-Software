import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários para funcionalidade de Amazon Family - CT063, CT064 e CT065
 * RF030 - Gerenciamento de Membros Amazon Family
 * RT036 - Teste de criação de perfil na Amazon Family
 */
public class AmazonFamilyTest {

    private AmazonFamilyService amazonFamilyService;

    @Before
    public void setUp() {
        amazonFamilyService = new AmazonFamilyService();
        amazonFamilyService.limparMembros(); // Garante que a lista está vazia
        amazonFamilyService.simularLoginAdministrador(); // Simula usuário logado como administrador
    }

    /**
     * CT063 - Garantir a criação de um usuário adulto
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * - Usuário é o administrador da conta Amazon Family
     * 
     * Entradas:
     * - Nome do novo membro, ID = "userAdultoFamily123"
     * - Perfil Adulto = true
     * 
     * Resultado Esperado:
     * - Usuário salvo com sucesso
     * 
     * Pós-condições:
     * - A lista de membros do Amazon Family está atualizada com novo usuário adulto
     */
    @Test
    public void ct063_criarUsuarioAdulto() {
        // Arrange
        String idUsuario = "userAdultoFamily123";
        String nomeUsuario = "João Silva";
        boolean ehAdulto = true;

        // Act
        AmazonFamilyService.ResultadoAdicaoFamily resultado = 
            amazonFamilyService.adicionarUsuarioFamily(idUsuario, nomeUsuario, ehAdulto);

        // Assert
        Assert.assertTrue("Usuário adulto deve ser criado com sucesso", resultado.isSucesso());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Usuário salvo com sucesso", resultado.getMensagem());
        Assert.assertNotNull("Usuário deve ser retornado", resultado.getUsuario());
        Assert.assertEquals("ID do usuário deve estar correto", idUsuario, resultado.getUsuario().getId());
        Assert.assertEquals("Nome do usuário deve estar correto", nomeUsuario, resultado.getUsuario().getNome());
        Assert.assertTrue("Usuário deve ser marcado como adulto", resultado.getUsuario().isEhAdulto());
        
        // Verificar se foi adicionado à lista
        Assert.assertTrue("Usuário deve existir na lista de membros", amazonFamilyService.existeUsuario(idUsuario));
        Assert.assertEquals("Lista deve conter 1 membro", 1, amazonFamilyService.getMembrosFamily().size());
    }

    /**
     * CT064 - Garantir a criação de um usuário infantil
     * 
     * Entradas:
     * - Nome do novo membro, ID = "userInfantilFamily456"
     * - Perfil Adulto = false
     * 
     * Resultado Esperado:
     * - Usuário salvo com sucesso
     * 
     * Pós-condições:
     * - A lista de membros do Amazon Family está atualizada com novo usuário infantil
     */
    @Test
    public void ct064_criarUsuarioInfantil() {
        // Arrange
        String idUsuario = "userInfantilFamily456";
        String nomeUsuario = "Maria Silva";
        boolean ehAdulto = false;

        // Act
        AmazonFamilyService.ResultadoAdicaoFamily resultado = 
            amazonFamilyService.adicionarUsuarioFamily(idUsuario, nomeUsuario, ehAdulto);

        // Assert
        Assert.assertTrue("Usuário infantil deve ser criado com sucesso", resultado.isSucesso());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Usuário salvo com sucesso", resultado.getMensagem());
        Assert.assertNotNull("Usuário deve ser retornado", resultado.getUsuario());
        Assert.assertEquals("ID do usuário deve estar correto", idUsuario, resultado.getUsuario().getId());
        Assert.assertEquals("Nome do usuário deve estar correto", nomeUsuario, resultado.getUsuario().getNome());
        Assert.assertFalse("Usuário deve ser marcado como infantil", resultado.getUsuario().isEhAdulto());
        
        // Verificar se foi adicionado à lista
        Assert.assertTrue("Usuário deve existir na lista de membros", amazonFamilyService.existeUsuario(idUsuario));
        Assert.assertEquals("Lista deve conter 1 membro", 1, amazonFamilyService.getMembrosFamily().size());
    }

    /**
     * CT065 - Verificar a criação de usuário com nome com mais de 50 caracteres
     * 
     * Entradas:
     * - Nome do novo membro, ID = "Testeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" (mais de 50 caracteres)
     * - Perfil Adulto = true
     * 
     * Resultado Esperado:
     * - Usuário não salvo
     * 
     * Pós-condições:
     * - A lista de membros do Amazon Family está atualizada sem usuário
     */
    @Test
    public void ct065_criarUsuarioComNomeMuitoLongo() {
        // Arrange
        String idUsuario = "userLongoNome789";
        // Nome com 51 caracteres (mais que o limite de 50)
        String nomeUsuario = "Testeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
        boolean ehAdulto = true;

        // Verificar que o nome tem mais de 50 caracteres
        Assert.assertTrue("Nome deve ter mais de 50 caracteres para o teste", nomeUsuario.length() > 50);

        // Act
        AmazonFamilyService.ResultadoAdicaoFamily resultado = 
            amazonFamilyService.adicionarUsuarioFamily(idUsuario, nomeUsuario, ehAdulto);

        // Assert
        Assert.assertFalse("Usuário com nome muito longo não deve ser criado", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Nome deve ter no máximo 50 caracteres", resultado.getMensagem());
        Assert.assertNull("Usuário não deve ser retornado", resultado.getUsuario());
        
        // Verificar se NÃO foi adicionado à lista
        Assert.assertFalse("Usuário não deve existir na lista de membros", amazonFamilyService.existeUsuario(idUsuario));
        Assert.assertEquals("Lista deve estar vazia", 0, amazonFamilyService.getMembrosFamily().size());
    }

    /**
     * Teste adicional - Usuário sem login
     */
    @Test
    public void testCriarUsuarioSemLogin() {
        // Arrange
        amazonFamilyService.simularLogout(); // Simula logout
        String idUsuario = "userSemLogin";
        String nomeUsuario = "Teste Sem Login";

        // Act
        AmazonFamilyService.ResultadoAdicaoFamily resultado = 
            amazonFamilyService.adicionarUsuarioFamily(idUsuario, nomeUsuario, true);

        // Assert
        Assert.assertFalse("Usuário sem login não deve conseguir criar membro", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Usuário deve estar logado", resultado.getMensagem());
    }

    /**
     * Teste adicional - ID duplicado
     */
    @Test
    public void testCriarUsuarioComIdDuplicado() {
        // Arrange
        String idUsuario = "userDuplicado";
        String nomeUsuario1 = "Primeiro Usuário";
        String nomeUsuario2 = "Segundo Usuário";

        // Criar primeiro usuário
        amazonFamilyService.adicionarUsuarioFamily(idUsuario, nomeUsuario1, true);

        // Act - Tentar criar segundo usuário com mesmo ID
        AmazonFamilyService.ResultadoAdicaoFamily resultado = 
            amazonFamilyService.adicionarUsuarioFamily(idUsuario, nomeUsuario2, false);

        // Assert
        Assert.assertFalse("Não deve permitir ID duplicado", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Usuário com este ID já existe", resultado.getMensagem());
        Assert.assertEquals("Deve haver apenas 1 usuário na lista", 1, amazonFamilyService.getMembrosFamily().size());
        
        // Verificar que o primeiro usuário permanece
        UsuarioFamily usuarioExistente = amazonFamilyService.buscarUsuarioPorId(idUsuario);
        Assert.assertEquals("Nome do usuário original deve permanecer", nomeUsuario1, usuarioExistente.getNome());
    }

    /**
     * Teste adicional - Nome com exatamente 50 caracteres (limite válido)
     */
    @Test
    public void testCriarUsuarioComNomeExatamente50Caracteres() {
        // Arrange
        String idUsuario = "userLimite50";
        // Nome com exatamente 50 caracteres
        String nomeUsuario = "01234567890123456789012345678901234567890123456789"; // 50 chars
        boolean ehAdulto = true;

        // Verificar que o nome tem exatamente 50 caracteres
        Assert.assertEquals("Nome deve ter exatamente 50 caracteres", 50, nomeUsuario.length());

        // Act
        AmazonFamilyService.ResultadoAdicaoFamily resultado = 
            amazonFamilyService.adicionarUsuarioFamily(idUsuario, nomeUsuario, ehAdulto);

        // Assert
        Assert.assertTrue("Usuário com nome de 50 caracteres deve ser criado", resultado.isSucesso());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Usuário salvo com sucesso", resultado.getMensagem());
        Assert.assertTrue("Usuário deve existir na lista", amazonFamilyService.existeUsuario(idUsuario));
    }
}

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

/**
 * Testes de integração completos - conectando Login, Perfil de Compras e Histórico de Navegação
 * Demonstra como fazer testes de integração que envolvem múltiplos serviços
 */
public class SistemaComprasIntegrationTest {

    private LoginService loginService;
    private PerfilComprasService perfilComprasService;
    private HistoricoNavegacaoService historicoService;
    private CompraService compraService;
    
    private final String EMAIL_USUARIO = "usuario.integracao@teste.com";
    private final String SENHA_USUARIO = "123456";

    @Before
    public void setUp() {
        // Configuração dos serviços
        loginService = new LoginService();
        perfilComprasService = new PerfilComprasService();
        historicoService = new HistoricoNavegacaoService();
        compraService = new CompraService();
        
        // Limpar estados anteriores
        perfilComprasService.limparPerfis();
        historicoService.limparTodosHistoricos();
        
        // Adicionar usuário de teste ao sistema de login
        Usuario usuarioTeste = new Usuario(EMAIL_USUARIO, SENHA_USUARIO);
        loginService.usuariosCadastrados.add(usuarioTeste);
    }

    /**
     * Teste de integração completo: Login → Criar Perfil → Navegar → Comprar
     * Simula um fluxo real de usuário no sistema
     */
    @Test
    public void testFluxoCompletoUsuario() {
        // 1. FASE LOGIN
        boolean loginRealizado = loginService.realizarLogin(EMAIL_USUARIO, SENHA_USUARIO);
        Assert.assertTrue("Login deve ser bem-sucedido", loginRealizado);
        
        // Simular login no serviço de perfil
        perfilComprasService.simularLogin(EMAIL_USUARIO);
        
        // 2. FASE CRIAÇÃO DE PERFIL
        String nomePerfil = "Perfil Eletrônicos";
        PerfilComprasService.ResultadoCriacaoPerfil resultadoPerfil = 
            perfilComprasService.adicionarPerfilCompras(nomePerfil);
        
        Assert.assertTrue("Perfil deve ser criado com sucesso", resultadoPerfil.isSucesso());
        Assert.assertNotNull("Perfil deve ser retornado", resultadoPerfil.getPerfil());
        
        // 3. FASE NAVEGAÇÃO
        String usuarioId = EMAIL_USUARIO;
        
        // Simular navegação por produtos
        ItemNavegacao item1 = new ItemNavegacao("smartphone001", "iPhone 15", "Eletrônicos", 4999.99, usuarioId);
        ItemNavegacao item2 = new ItemNavegacao("notebook001", "MacBook Pro", "Informática", 8999.99, usuarioId);
        ItemNavegacao item3 = new ItemNavegacao("watch001", "Apple Watch", "Wearables", 1999.99, usuarioId);
        
        HistoricoNavegacaoService.ResultadoRegistroVisita registro1 = 
            historicoService.registrarVisitaItem(usuarioId, item1);
        HistoricoNavegacaoService.ResultadoRegistroVisita registro2 = 
            historicoService.registrarVisitaItem(usuarioId, item2);
        HistoricoNavegacaoService.ResultadoRegistroVisita registro3 = 
            historicoService.registrarVisitaItem(usuarioId, item3);
        
        Assert.assertTrue("Todas as navegações devem ser registradas", 
            registro1.isSucesso() && registro2.isSucesso() && registro3.isSucesso());
        
        // Verificar histórico
        List<ItemNavegacao> historico = historicoService.obterHistoricoNavegacao(usuarioId);
        Assert.assertEquals("Histórico deve conter 3 itens", 3, historico.size());
        
        // 4. FASE COMPRA
        String resultadoCompra = compraService.comprarDireto("Apple Watch", true);
        Assert.assertEquals("Compra deve ser realizada com sucesso", 
            "Compra realizada com sucesso", resultadoCompra);
        
        // 5. VERIFICAÇÕES FINAIS DE INTEGRAÇÃO
        // Verificar que o perfil ainda existe
        Assert.assertTrue("Perfil deve ainda existir", 
            perfilComprasService.existePerfilParaUsuarioAtual(nomePerfil));
        
        // Verificar que o histórico se mantém
        List<ItemNavegacao> historicoFinal = historicoService.obterHistoricoNavegacao(usuarioId);
        Assert.assertEquals("Histórico deve ser preservado", 3, historicoFinal.size());
        Assert.assertEquals("Item mais recente deve ser Apple Watch", 
            "watch001", historicoFinal.get(0).getId());
    }

    /**
     * Teste de integração: Usuário sem login não pode usar serviços integrados
     */
    @Test
    public void testUsuarioSemLoginNaoAcessaServicos() {
        String usuarioId = "usuario.nao.logado@teste.com";
        
        // 1. Tentar criar perfil sem login
        PerfilComprasService.ResultadoCriacaoPerfil resultadoPerfil = 
            perfilComprasService.adicionarPerfilCompras("Perfil Teste");
        
        Assert.assertFalse("Criação de perfil deve falhar sem login", resultadoPerfil.isSucesso());
        
        // 2. Tentar comprar sem login
        String resultadoCompra = compraService.comprarDireto("Produto Teste", false);
        Assert.assertEquals("Compra deve falhar sem login", 
            "Usuário precisa estar logado", resultadoCompra);
        
        // 3. Histórico ainda pode ser acessado (mas estará vazio)
        List<ItemNavegacao> historico = historicoService.obterHistoricoNavegacao(usuarioId);
        Assert.assertTrue("Histórico deve estar vazio para usuário não logado", 
            historico.isEmpty());
    }

    /**
     * Teste de integração: Múltiplos usuários com perfis e históricos isolados
     */
    @Test
    public void testIsolamentoEntreUsuarios() {
        // Configurar dois usuários
        String usuario1 = "user1@teste.com";
        String usuario2 = "user2@teste.com";
        
        // Adicionar usuários ao sistema de login
        loginService.usuariosCadastrados.add(new Usuario(usuario1, "senha1"));
        loginService.usuariosCadastrados.add(new Usuario(usuario2, "senha2"));
        
        // Usuario 1: Login e criação de perfil
        Assert.assertTrue("Login usuário 1 deve funcionar", 
            loginService.realizarLogin(usuario1, "senha1"));
        perfilComprasService.simularLogin(usuario1);
        
        PerfilComprasService.ResultadoCriacaoPerfil perfilUser1 = 
            perfilComprasService.adicionarPerfilCompras("Perfil User 1");
        Assert.assertTrue("Perfil usuário 1 deve ser criado", perfilUser1.isSucesso());
        
        // Usuario 1: Navegação
        ItemNavegacao itemUser1 = new ItemNavegacao("prod001", "Produto User 1", "Cat1", 100.0, usuario1);
        historicoService.registrarVisitaItem(usuario1, itemUser1);
        
        // Usuario 2: Login e criação de perfil
        Assert.assertTrue("Login usuário 2 deve funcionar", 
            loginService.realizarLogin(usuario2, "senha2"));
        perfilComprasService.simularLogin(usuario2);
        
        PerfilComprasService.ResultadoCriacaoPerfil perfilUser2 = 
            perfilComprasService.adicionarPerfilCompras("Perfil User 2");
        Assert.assertTrue("Perfil usuário 2 deve ser criado", perfilUser2.isSucesso());
        
        // Usuario 2: Navegação
        ItemNavegacao itemUser2 = new ItemNavegacao("prod002", "Produto User 2", "Cat2", 200.0, usuario2);
        historicoService.registrarVisitaItem(usuario2, itemUser2);
        
        // Verificar isolamento
        List<ItemNavegacao> historicoUser1 = historicoService.obterHistoricoNavegacao(usuario1);
        List<ItemNavegacao> historicoUser2 = historicoService.obterHistoricoNavegacao(usuario2);
        
        Assert.assertEquals("Usuário 1 deve ter 1 item no histórico", 1, historicoUser1.size());
        Assert.assertEquals("Usuário 2 deve ter 1 item no histórico", 1, historicoUser2.size());
        
        Assert.assertEquals("Histórico usuário 1 deve conter seu item", 
            "prod001", historicoUser1.get(0).getId());
        Assert.assertEquals("Histórico usuário 2 deve conter seu item", 
            "prod002", historicoUser2.get(0).getId());
        
        // Verificar isolamento de perfis
        perfilComprasService.simularLogin(usuario1);
        Assert.assertEquals("Usuário 1 deve ter 1 perfil", 
            1, perfilComprasService.getPerfisComprasUsuarioAtual().size());
        
        perfilComprasService.simularLogin(usuario2);
        Assert.assertEquals("Usuário 2 deve ter 1 perfil", 
            1, perfilComprasService.getPerfisComprasUsuarioAtual().size());
    }

    /**
     * Teste de integração: Falha em cascata - se um serviço falha, outros são afetados
     */
    @Test
    public void testFalhaEmCascata() {
        // Simular sistema de histórico indisponível
        historicoService.setSistemaAtivo(false);
        
        // Fazer login normalmente
        Assert.assertTrue("Login deve funcionar mesmo com histórico indisponível", 
            loginService.realizarLogin(EMAIL_USUARIO, SENHA_USUARIO));
        
        perfilComprasService.simularLogin(EMAIL_USUARIO);
        
        // Criar perfil deve funcionar
        PerfilComprasService.ResultadoCriacaoPerfil resultadoPerfil = 
            perfilComprasService.adicionarPerfilCompras("Perfil Teste");
        Assert.assertTrue("Criação de perfil deve funcionar independente do histórico", 
            resultadoPerfil.isSucesso());
        
        // Mas histórico não deve funcionar
        ItemNavegacao item = new ItemNavegacao("prod001", "Produto", "Cat", 10.0, EMAIL_USUARIO);
        HistoricoNavegacaoService.ResultadoRegistroVisita registroHistorico = 
            historicoService.registrarVisitaItem(EMAIL_USUARIO, item);
        
        Assert.assertFalse("Registro no histórico deve falhar com sistema indisponível", 
            registroHistorico.isSucesso());
        
        // Compra ainda deve funcionar
        String resultadoCompra = compraService.comprarDireto("Produto", true);
        Assert.assertEquals("Compra deve funcionar independente do histórico", 
            "Compra realizada com sucesso", resultadoCompra);
    }
}

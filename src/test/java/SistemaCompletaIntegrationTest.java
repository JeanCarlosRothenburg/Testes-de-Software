import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * Testes de integração híbridos - Demonstra integração entre múltiplos serviços
 * Combina componentes reais (LoginService, PerfilComprasService) com stubs (AssinaturaService)
 * 
 * Esta abordagem permite testar integrações reais mantendo controle sobre dados de teste
 */
public class SistemaCompletaIntegrationTest {

    private LoginService loginService;
    private PerfilComprasService perfilComprasService;
    private AssinaturaService assinaturaService;
    private HistoricoNavegacaoService historicoService;
    
    private final String EMAIL_USUARIO = "integration.user@teste.com";
    private final String SENHA_USUARIO = "123456";

    @Before
    public void setUp() {
        // Inicializar serviços reais
        loginService = new LoginService();
        perfilComprasService = new PerfilComprasService();
        historicoService = new HistoricoNavegacaoService();
        
        // Inicializar serviço de assinaturas com stub
        AssinaturaDAO assinaturaDAO = new AssinaturaDAO(false); // Modo stub
        assinaturaService = new AssinaturaService(assinaturaDAO);
        
        // Limpar estados
        perfilComprasService.limparPerfis();
        historicoService.limparTodosHistoricos();
        assinaturaService.limparDados();
        
        // Adicionar usuário de teste ao sistema real de login
        Usuario usuarioTeste = new Usuario(EMAIL_USUARIO, SENHA_USUARIO);
        loginService.usuariosCadastrados.add(usuarioTeste);
    }

    /**
     * Teste de integração híbrido - Fluxo completo do usuário
     * Combina componentes reais e stubs para testar cenário completo
     */
    @Test
    public void testFluxoCompletoHibrido() {
        // ===== FASE 1: LOGIN (COMPONENTE REAL) =====
        boolean loginSucesso = loginService.realizarLogin(EMAIL_USUARIO, SENHA_USUARIO);
        Assert.assertTrue("Login com componente real deve funcionar", loginSucesso);
        
        // Simular login nos outros serviços
        perfilComprasService.simularLogin(EMAIL_USUARIO);
        assinaturaService.simularLogin(EMAIL_USUARIO);
        
        // ===== FASE 2: PERFIL DE COMPRAS (COMPONENTE REAL) =====
        String nomePerfil = "Perfil Eletrônicos Premium";
        PerfilComprasService.ResultadoCriacaoPerfil resultadoPerfil = 
            perfilComprasService.adicionarPerfilCompras(nomePerfil);
        
        Assert.assertTrue("Criação de perfil com componente real deve funcionar", 
            resultadoPerfil.isSucesso());
        Assert.assertEquals("Perfil deve ter nome correto", nomePerfil, 
            resultadoPerfil.getPerfil().getNome());
        
        // ===== FASE 3: HISTÓRICO DE NAVEGAÇÃO (COMPONENTE REAL) =====
        ItemNavegacao item1 = new ItemNavegacao("prod001", "iPhone 15 Pro", "Eletrônicos", 
            6999.99, EMAIL_USUARIO);
        ItemNavegacao item2 = new ItemNavegacao("prod002", "MacBook Pro M3", "Informática", 
            12999.99, EMAIL_USUARIO);
        
        HistoricoNavegacaoService.ResultadoRegistroVisita registro1 = 
            historicoService.registrarVisitaItem(EMAIL_USUARIO, item1);
        HistoricoNavegacaoService.ResultadoRegistroVisita registro2 = 
            historicoService.registrarVisitaItem(EMAIL_USUARIO, item2);
        
        Assert.assertTrue("Registro no histórico deve funcionar", 
            registro1.isSucesso() && registro2.isSucesso());
        
        List<ItemNavegacao> historico = historicoService.obterHistoricoNavegacao(EMAIL_USUARIO);
        Assert.assertEquals("Histórico deve conter 2 itens", 2, historico.size());
        
        // ===== FASE 4: ASSINATURAS (STUB CONTROLADO) =====
        // Configurar assinaturas de teste via stub
        List<Assinatura> assinaturasIniciais = criarAssinaturasParaFluxo(EMAIL_USUARIO);
        assinaturaService.configurarDadosTeste(EMAIL_USUARIO, assinaturasIniciais);
        
        List<Assinatura> assinaturas = assinaturaService.listarAssinaturasUsuario(EMAIL_USUARIO);
        Assert.assertEquals("Deve ter assinaturas configuradas via stub", 
            assinaturasIniciais.size(), assinaturas.size());
        
        // Testar operação com stub
        AssinaturaService.ResultadoAlteracaoStatus alteracao = 
            assinaturaService.desativarAssinatura("prime001");
        Assert.assertTrue("Operação com stub deve funcionar", alteracao.isSucesso());
        
        // ===== VERIFICAÇÃO DE INTEGRAÇÃO ENTRE COMPONENTES =====
        
        // 1. Verificar que perfil ainda existe
        Assert.assertTrue("Perfil deve existir após operações", 
            perfilComprasService.existePerfilParaUsuarioAtual(nomePerfil));
        
        // 2. Verificar que histórico se mantém
        List<ItemNavegacao> historicoFinal = historicoService.obterHistoricoNavegacao(EMAIL_USUARIO);
        Assert.assertEquals("Histórico deve se manter após outras operações", 
            2, historicoFinal.size());
        
        // 3. Verificar que assinaturas foram afetadas pela operação
        int assinaturasAtivasDepois = assinaturaService.contarAssinaturasAtivas(EMAIL_USUARIO);
        Assert.assertTrue("Deve ter menos assinaturas ativas após desativação", 
            assinaturasAtivasDepois < assinaturasIniciais.size());
        
        // 4. Verificar isolamento: dados são do usuário correto
        for (ItemNavegacao item : historicoFinal) {
            Assert.assertEquals("Itens do histórico devem ser do usuário correto", 
                EMAIL_USUARIO, item.getUsuarioId());
        }
        
        List<Assinatura> assinaturasFinais = assinaturaService.listarAssinaturasUsuario(EMAIL_USUARIO);
        for (Assinatura assinatura : assinaturasFinais) {
            Assert.assertEquals("Assinaturas devem ser do usuário correto", 
                EMAIL_USUARIO, assinatura.getUsuarioId());
        }
    }

    /**
     * Teste de integração híbrido - Falha em componente real afeta outros
     */
    @Test
    public void testFalhaComponenteRealAfetaOutros() {
        // Simular falha no login (componente real)
        boolean loginFalha = loginService.realizarLogin(EMAIL_USUARIO, "senhaErrada");
        Assert.assertFalse("Login com senha incorreta deve falhar", loginFalha);
        
        // Tentar operações sem login bem-sucedido
        PerfilComprasService.ResultadoCriacaoPerfil resultadoPerfil = 
            perfilComprasService.adicionarPerfilCompras("Perfil Teste");
        Assert.assertFalse("Criação de perfil deve falhar sem login", 
            resultadoPerfil.isSucesso());
        
        // Histórico ainda pode funcionar (não depende de login formal)
        ItemNavegacao item = new ItemNavegacao("prod001", "Produto", "Cat", 100.0, EMAIL_USUARIO);
        HistoricoNavegacaoService.ResultadoRegistroVisita registro = 
            historicoService.registrarVisitaItem(EMAIL_USUARIO, item);
        Assert.assertTrue("Histórico deve funcionar independente do login", 
            registro.isSucesso());
        
        // Assinaturas com stub funcionam independentemente
        List<Assinatura> assinaturas = assinaturaService.listarAssinaturasUsuario(EMAIL_USUARIO);
        Assert.assertNotNull("Stub deve funcionar independente de outros componentes", 
            assinaturas);
    }

    /**
     * Teste de integração híbrido - Performance com componentes mistos
     */
    @Test
    public void testPerformanceComponentesMistos() {
        // Setup
        loginService.realizarLogin(EMAIL_USUARIO, SENHA_USUARIO);
        perfilComprasService.simularLogin(EMAIL_USUARIO);
        
        // Configurar dados de teste para múltiplas operações
        List<Assinatura> muitasAssinaturas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Assinatura assinatura = new Assinatura(
                "sub" + i,
                "Assinatura " + i,
                "Descrição " + i,
                new BigDecimal("10.00"),
                Assinatura.TipoAssinatura.MENSAL,
                EMAIL_USUARIO
            );
            muitasAssinaturas.add(assinatura);
        }
        assinaturaService.configurarDadosTeste(EMAIL_USUARIO, muitasAssinaturas);
        
        // Medir performance de operações mistas
        long tempoInicio = System.currentTimeMillis();
        
        // Operação com componente real
        perfilComprasService.adicionarPerfilCompras("Perfil Performance");
        
        // Operação com stub
        List<Assinatura> assinaturas = assinaturaService.listarAssinaturasUsuario(EMAIL_USUARIO);
        
        // Operação com componente real
        ItemNavegacao item = new ItemNavegacao("perf001", "Item Performance", "Cat", 50.0, EMAIL_USUARIO);
        historicoService.registrarVisitaItem(EMAIL_USUARIO, item);
        
        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;
        
        // Verificações
        Assert.assertEquals("Deve carregar todas as assinaturas", 100, assinaturas.size());
        Assert.assertTrue("Operações mistas devem ser rápidas (< 2000ms)", tempoTotal < 2000);
        Assert.assertTrue("Perfil deve ser criado", 
            perfilComprasService.existePerfilParaUsuarioAtual("Perfil Performance"));
        Assert.assertEquals("Histórico deve ter 1 item", 1, 
            historicoService.obterHistoricoNavegacao(EMAIL_USUARIO).size());
    }

    /**
     * Teste de integração híbrido - Consistência de dados entre componentes
     */
    @Test
    public void testConsistenciaDadosEntreComponentes() {
        // Setup inicial
        loginService.realizarLogin(EMAIL_USUARIO, SENHA_USUARIO);
        perfilComprasService.simularLogin(EMAIL_USUARIO);
        
        // Criar dados em cada componente
        // 1. Perfil
        perfilComprasService.adicionarPerfilCompras("Perfil Consistência");
        
        // 2. Histórico
        ItemNavegacao item = new ItemNavegacao("cons001", "Item Consistência", "Cat", 25.0, EMAIL_USUARIO);
        historicoService.registrarVisitaItem(EMAIL_USUARIO, item);
        
        // 3. Assinaturas
        Assinatura assinatura = new Assinatura("cons001", "Assinatura Consistência", "Desc", 
            new BigDecimal("15.00"), Assinatura.TipoAssinatura.MENSAL, EMAIL_USUARIO);
        List<Assinatura> assinaturas = new ArrayList<>();
        assinaturas.add(assinatura);
        assinaturaService.configurarDadosTeste(EMAIL_USUARIO, assinaturas);
        
        // Verificar consistência - todos os dados devem referenciar o mesmo usuário
        List<PerfilCompras> perfis = perfilComprasService.getPerfisComprasUsuarioAtual();
        List<ItemNavegacao> historico = historicoService.obterHistoricoNavegacao(EMAIL_USUARIO);
        List<Assinatura> assinaturasUsuario = assinaturaService.listarAssinaturasUsuario(EMAIL_USUARIO);
        
        Assert.assertEquals("Deve ter 1 perfil", 1, perfis.size());
        Assert.assertEquals("Deve ter 1 item no histórico", 1, historico.size());
        Assert.assertEquals("Deve ter 1 assinatura", 1, assinaturasUsuario.size());
        
        // Verificar que todos referenciam o usuário correto
        Assert.assertEquals("Perfil deve ser do usuário correto", 
            EMAIL_USUARIO, perfis.get(0).getUsuario());
        Assert.assertEquals("Item do histórico deve ser do usuário correto", 
            EMAIL_USUARIO, historico.get(0).getUsuarioId());
        Assert.assertEquals("Assinatura deve ser do usuário correto", 
            EMAIL_USUARIO, assinaturasUsuario.get(0).getUsuarioId());
    }

    /**
     * Método auxiliar para criar assinaturas para fluxo de teste
     */
    private List<Assinatura> criarAssinaturasParaFluxo(String usuarioId) {
        List<Assinatura> assinaturas = new ArrayList<>();
        
        Assinatura prime = new Assinatura("prime001", "Amazon Prime", "Benefícios premium", 
            new BigDecimal("14.90"), Assinatura.TipoAssinatura.MENSAL, usuarioId);
        
        Assinatura music = new Assinatura("music001", "Amazon Music", "Streaming musical", 
            new BigDecimal("9.90"), Assinatura.TipoAssinatura.MENSAL, usuarioId);
        
        assinaturas.add(prime);
        assinaturas.add(music);
        
        return assinaturas;
    }
}

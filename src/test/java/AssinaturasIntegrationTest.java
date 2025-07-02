import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * Testes de integração para funcionalidade de Inscrições e Assinaturas - CT073 e CT074
 * RF032 - O sistema deve permitir gerenciar inscrições e assinaturas
 * RT040 - Teste para ativar e desativar inscrições e assinaturas
 * 
 * Nível de teste: Integração
 * Técnica de teste: Caixa Preta
 * Critério de seleção de entradas: Casos de Uso
 */
public class AssinaturasIntegrationTest {

    private AssinaturaService assinaturaService;
    private AssinaturaDAO assinaturaDAO;
    
    // Usuários de teste conforme especificação
    private final String USUARIO_COM_ASSINATURAS = "userActiveSubs";
    private final String ID_ASSINATURA_TESTE = "subscription123";

    @Before
    public void setUp() {
        // Configuração do ambiente de teste
        assinaturaDAO = new AssinaturaDAO(false); // Usar stub para testes
        assinaturaService = new AssinaturaService(assinaturaDAO);
        
        // Limpar dados anteriores
        assinaturaService.limparDados();
        assinaturaService.setSistemaAtivo(true);
    }

    /**
     * CT073 - Validar a exibição correta de todas as inscrições e seu estado
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * - Usuário possui assinaturas vinculadas à conta
     * 
     * Entradas:
     * - Usuário logado com ID = "userActiveSubs"
     * 
     * Resultado Esperado:
     * - Todas as inscrições ativas são exibidas corretamente sem atrasos ou erros
     * 
     * Pós-condições:
     * - A lista de inscrições ativas permanece consistente e sem erros de exibição
     */
    @Test
    public void ct073_validarExibicaoCorretaInscricoesEstado() {
        // Arrange - Configurar ambiente de teste com usuário que possui assinaturas vinculadas
        String usuarioId = USUARIO_COM_ASSINATURAS;
        
        // Passo 1: Configurar o ambiente de teste com assinaturas vinculadas
        List<Assinatura> assinaturasIniciais = criarAssinaturasParaTeste(usuarioId);
        assinaturaService.configurarDadosTeste(usuarioId, assinaturasIniciais);
        
        // Simular usuário logado
        assinaturaService.simularLogin(usuarioId);
        
        // Act - Passo 2: Verificar se o método lista as assinaturas disponíveis e seu estado
        List<Assinatura> todasAssinaturas = assinaturaService.listarAssinaturasUsuario(usuarioId);
        List<Assinatura> assinaturasAtivas = assinaturaService.listarAssinaturasAtivas(usuarioId);
        
        // Assert - Validar exibição correta
        Assert.assertNotNull("Lista de assinaturas não deve ser nula", todasAssinaturas);
        Assert.assertFalse("Lista de assinaturas não deve estar vazia", todasAssinaturas.isEmpty());
        
        // Verificar que todas as assinaturas são exibidas corretamente
        Assert.assertEquals("Deve exibir todas as assinaturas configuradas", 
            assinaturasIniciais.size(), todasAssinaturas.size());
        
        // Verificar que assinaturas ativas são filtradas corretamente
        long assinaturasAtivasEsperadas = assinaturasIniciais.stream()
            .filter(a -> a.getStatus() == Assinatura.StatusAssinatura.ATIVA)
            .count();
        Assert.assertEquals("Deve exibir apenas assinaturas ativas", 
            assinaturasAtivasEsperadas, assinaturasAtivas.size());
        
        // Verificar estados específicos das assinaturas
        for (Assinatura assinatura : todasAssinaturas) {
            Assert.assertNotNull("Status da assinatura não deve ser nulo", assinatura.getStatus());
            Assert.assertEquals("Assinatura deve pertencer ao usuário correto", 
                usuarioId, assinatura.getUsuarioId());
            Assert.assertNotNull("Nome da assinatura não deve ser nulo", assinatura.getNome());
            Assert.assertNotNull("ID da assinatura não deve ser nulo", assinatura.getId());
        }
        
        // Verificar que não há atrasos ou erros (performance básica)
        long tempoInicio = System.currentTimeMillis();
        List<Assinatura> assinaturasSegundaConsulta = assinaturaService.listarAssinaturasUsuario(usuarioId);
        long tempoFim = System.currentTimeMillis();
        long tempoExecucao = tempoFim - tempoInicio;
        
        Assert.assertTrue("Consulta deve ser rápida (< 1000ms)", tempoExecucao < 1000);
        Assert.assertEquals("Segunda consulta deve retornar mesmo resultado", 
            todasAssinaturas.size(), assinaturasSegundaConsulta.size());
        
        // Pós-condições: Lista permanece consistente
        Assert.assertTrue("Lista deve permanecer consistente", 
            assinaturaService.usuarioPossuiAssinaturas(usuarioId));
        Assert.assertTrue("Contador de assinaturas ativas deve estar correto", 
            assinaturaService.contarAssinaturasAtivas(usuarioId) > 0);
    }

    /**
     * CT074 - Alterar estado de uma inscrição
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * - Assinatura existe e está ativa
     * 
     * Entradas:
     * - Usuário logado com ID = "userActiveSubs"
     * - ID da inscrição a ser alterada: "subscription123"
     * - Estado inicial: Ativo
     * 
     * Resultado Esperado:
     * - Estado inicial: Inativo
     * 
     * Pós-condições:
     * - O sistema deve atualizar e exibir corretamente o novo estado da inscrição
     */
    @Test
    public void ct074_alterarEstadoInscricao() {
        // Arrange - Configurar ambiente com assinatura ativa
        String usuarioId = USUARIO_COM_ASSINATURAS;
        String assinaturaId = ID_ASSINATURA_TESTE;
        
        // Criar assinatura com estado inicial ATIVO
        Assinatura assinaturaInicial = new Assinatura(
            assinaturaId,
            "Amazon Prime",
            "Assinatura premium com benefícios exclusivos",
            new BigDecimal("14.90"),
            Assinatura.TipoAssinatura.MENSAL,
            usuarioId
        );
        assinaturaInicial.setStatus(Assinatura.StatusAssinatura.ATIVA); // Estado inicial: Ativo
        
        List<Assinatura> assinaturasIniciais = new ArrayList<>();
        assinaturasIniciais.add(assinaturaInicial);
        
        assinaturaService.configurarDadosTeste(usuarioId, assinaturasIniciais);
        assinaturaService.simularLogin(usuarioId);
        
        // Verificar estado inicial
        Assinatura assinaturaAntes = assinaturaService.buscarAssinaturaPorId(assinaturaId);
        Assert.assertNotNull("Assinatura deve existir", assinaturaAntes);
        Assert.assertEquals("Estado inicial deve ser ATIVA", 
            Assinatura.StatusAssinatura.ATIVA, assinaturaAntes.getStatus());
        
        // Act - Passo 3: Solicitar alteração de status da assinatura (ativa -> inativa)
        AssinaturaService.ResultadoAlteracaoStatus resultado = 
            assinaturaService.alterarStatusAssinatura(assinaturaId, Assinatura.StatusAssinatura.INATIVA);
        
        // Assert - Verificar resultado da alteração
        Assert.assertTrue("Alteração de status deve ser bem-sucedida", resultado.isSucesso());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", 
            "Status alterado com sucesso", resultado.getMensagem());
        Assert.assertEquals("Status anterior deve ser ATIVA", 
            Assinatura.StatusAssinatura.ATIVA, resultado.getStatusAnterior());
        Assert.assertEquals("Novo status deve ser INATIVA", 
            Assinatura.StatusAssinatura.INATIVA, resultado.getStatusNovo());
        
        // Passo 4: Verificar se o método lista as assinaturas com o estado correto
        Assinatura assinaturaDepois = assinaturaService.buscarAssinaturaPorId(assinaturaId);
        Assert.assertNotNull("Assinatura deve ainda existir", assinaturaDepois);
        Assert.assertEquals("Estado final deve ser INATIVA", 
            Assinatura.StatusAssinatura.INATIVA, assinaturaDepois.getStatus());
        
        // Verificar que mudança se reflete nas listagens
        List<Assinatura> todasAssinaturas = assinaturaService.listarAssinaturasUsuario(usuarioId);
        List<Assinatura> assinaturasAtivas = assinaturaService.listarAssinaturasAtivas(usuarioId);
        
        Assert.assertEquals("Deve haver 1 assinatura total", 1, todasAssinaturas.size());
        Assert.assertEquals("Não deve haver assinaturas ativas", 0, assinaturasAtivas.size());
        
        // Verificar que a assinatura na lista tem o status correto
        Assinatura assinaturaNaLista = todasAssinaturas.get(0);
        Assert.assertEquals("Assinatura na lista deve ter status INATIVA", 
            Assinatura.StatusAssinatura.INATIVA, assinaturaNaLista.getStatus());
        
        // Pós-condições: Sistema atualiza e exibe corretamente o novo estado
        Assert.assertFalse("Usuário não deve ter assinaturas ativas", 
            assinaturaService.contarAssinaturasAtivas(usuarioId) > 0);
        Assert.assertTrue("Usuário ainda deve possuir assinaturas (inativas)", 
            assinaturaService.usuarioPossuiAssinaturas(usuarioId));
    }

    /**
     * Teste de integração adicional - Fluxo completo de ativação/desativação
     */
    @Test
    public void testFluxoCompletoAtivacaoDesativacao() {
        // Arrange
        String usuarioId = "userFluxoCompleto";
        String assinaturaId = "sub001";
        
        Assinatura assinatura = new Assinatura(
            assinaturaId,
            "Netflix Premium",
            "Streaming de vídeos em 4K",
            new BigDecimal("45.90"),
            Assinatura.TipoAssinatura.MENSAL,
            usuarioId
        );
        
        List<Assinatura> assinaturas = new ArrayList<>();
        assinaturas.add(assinatura);
        assinaturaService.configurarDadosTeste(usuarioId, assinaturas);
        
        // Act & Assert - Ciclo completo
        // 1. Verificar estado inicial (ATIVA)
        Assert.assertEquals("Deve iniciar como ATIVA", 
            Assinatura.StatusAssinatura.ATIVA, 
            assinaturaService.buscarAssinaturaPorId(assinaturaId).getStatus());
        Assert.assertEquals("Deve ter 1 assinatura ativa", 1, 
            assinaturaService.contarAssinaturasAtivas(usuarioId));
        
        // 2. Desativar
        AssinaturaService.ResultadoAlteracaoStatus resultadoDesativar = 
            assinaturaService.desativarAssinatura(assinaturaId);
        Assert.assertTrue("Desativação deve ser bem-sucedida", resultadoDesativar.isSucesso());
        Assert.assertEquals("Deve ter 0 assinaturas ativas", 0, 
            assinaturaService.contarAssinaturasAtivas(usuarioId));
        
        // 3. Reativar
        AssinaturaService.ResultadoAlteracaoStatus resultadoAtivar = 
            assinaturaService.ativarAssinatura(assinaturaId);
        Assert.assertTrue("Reativação deve ser bem-sucedida", resultadoAtivar.isSucesso());
        Assert.assertEquals("Deve ter 1 assinatura ativa novamente", 1, 
            assinaturaService.contarAssinaturasAtivas(usuarioId));
    }

    /**
     * Teste de integração adicional - Múltiplas assinaturas com estados diferentes
     */
    @Test
    public void testMultiplasAssinaturasEstadosDiferentes() {
        // Arrange
        String usuarioId = "userMultiplasAssinaturas";
        
        List<Assinatura> assinaturas = new ArrayList<>();
        
        // Assinatura ativa
        Assinatura ativa = new Assinatura("sub1", "Spotify", "Música", 
            new BigDecimal("19.90"), Assinatura.TipoAssinatura.MENSAL, usuarioId);
        ativa.setStatus(Assinatura.StatusAssinatura.ATIVA);
        
        // Assinatura inativa
        Assinatura inativa = new Assinatura("sub2", "YouTube Premium", "Vídeos", 
            new BigDecimal("20.90"), Assinatura.TipoAssinatura.MENSAL, usuarioId);
        inativa.setStatus(Assinatura.StatusAssinatura.INATIVA);
        
        // Assinatura cancelada
        Assinatura cancelada = new Assinatura("sub3", "Adobe Creative", "Design", 
            new BigDecimal("89.90"), Assinatura.TipoAssinatura.MENSAL, usuarioId);
        cancelada.setStatus(Assinatura.StatusAssinatura.CANCELADA);
        
        assinaturas.add(ativa);
        assinaturas.add(inativa);
        assinaturas.add(cancelada);
        
        assinaturaService.configurarDadosTeste(usuarioId, assinaturas);
        
        // Act
        List<Assinatura> todasAssinaturas = assinaturaService.listarAssinaturasUsuario(usuarioId);
        List<Assinatura> assinaturasAtivas = assinaturaService.listarAssinaturasAtivas(usuarioId);
        
        // Assert
        Assert.assertEquals("Deve ter 3 assinaturas total", 3, todasAssinaturas.size());
        Assert.assertEquals("Deve ter 1 assinatura ativa", 1, assinaturasAtivas.size());
        Assert.assertEquals("Assinatura ativa deve ser o Spotify", 
            "Spotify", assinaturasAtivas.get(0).getNome());
    }

    /**
     * Teste de integração adicional - Sistema indisponível
     */
    @Test
    public void testSistemaIndisponivelIntegracao() {
        // Arrange
        String usuarioId = "userSistemaOff";
        String assinaturaId = "subSistemaOff";
        
        // Configurar dados
        Assinatura assinatura = new Assinatura(assinaturaId, "Teste", "Teste", 
            new BigDecimal("10.00"), Assinatura.TipoAssinatura.MENSAL, usuarioId);
        List<Assinatura> assinaturas = new ArrayList<>();
        assinaturas.add(assinatura);
        assinaturaService.configurarDadosTeste(usuarioId, assinaturas);
        
        // Act - Desativar sistema
        assinaturaService.setSistemaAtivo(false);
        
        List<Assinatura> assinaturasResult = assinaturaService.listarAssinaturasUsuario(usuarioId);
        AssinaturaService.ResultadoAlteracaoStatus resultado = 
            assinaturaService.alterarStatusAssinatura(assinaturaId, Assinatura.StatusAssinatura.INATIVA);
        
        // Assert
        Assert.assertTrue("Lista deve estar vazia com sistema indisponível", 
            assinaturasResult.isEmpty());
        Assert.assertFalse("Alteração deve falhar com sistema indisponível", 
            resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser adequada", 
            "Sistema temporariamente indisponível", resultado.getMensagem());
    }

    /**
     * Teste de integração adicional - Validação de transições de status
     */
    @Test
    public void testValidacaoTransicoesStatus() {
        // Arrange
        String usuarioId = "userTransicoes";
        String assinaturaId = "subTransicoes";
        
        Assinatura assinatura = new Assinatura(assinaturaId, "Teste Transições", "Teste", 
            new BigDecimal("10.00"), Assinatura.TipoAssinatura.MENSAL, usuarioId);
        assinatura.setStatus(Assinatura.StatusAssinatura.CANCELADA);
        
        List<Assinatura> assinaturas = new ArrayList<>();
        assinaturas.add(assinatura);
        assinaturaService.configurarDadosTeste(usuarioId, assinaturas);
        
        // Act & Assert - Testar transição válida: CANCELADA -> ATIVA
        AssinaturaService.ResultadoAlteracaoStatus resultadoValido = 
            assinaturaService.alterarStatusAssinatura(assinaturaId, Assinatura.StatusAssinatura.ATIVA);
        Assert.assertTrue("Transição CANCELADA -> ATIVA deve ser válida", 
            resultadoValido.isSucesso());
        
        // Testar transição inválida: ATIVA -> VENCIDA (não permitida diretamente)
        AssinaturaService.ResultadoAlteracaoStatus resultadoInvalido = 
            assinaturaService.alterarStatusAssinatura(assinaturaId, Assinatura.StatusAssinatura.VENCIDA);
        Assert.assertFalse("Transição ATIVA -> VENCIDA deve ser inválida", 
            resultadoInvalido.isSucesso());
        Assert.assertTrue("Mensagem deve indicar transição inválida", 
            resultadoInvalido.getMensagem().contains("Transição de status inválida"));
    }

    /**
     * Método auxiliar para criar assinaturas de teste
     */
    private List<Assinatura> criarAssinaturasParaTeste(String usuarioId) {
        List<Assinatura> assinaturas = new ArrayList<>();
        
        // Assinatura ativa
        Assinatura prime = new Assinatura(
            ID_ASSINATURA_TESTE,
            "Amazon Prime",
            "Entrega rápida e streaming",
            new BigDecimal("14.90"),
            Assinatura.TipoAssinatura.MENSAL,
            usuarioId
        );
        prime.setStatus(Assinatura.StatusAssinatura.ATIVA);
        
        // Assinatura ativa
        Assinatura music = new Assinatura(
            "music456",
            "Amazon Music",
            "Streaming de música",
            new BigDecimal("9.90"),
            Assinatura.TipoAssinatura.MENSAL,
            usuarioId
        );
        music.setStatus(Assinatura.StatusAssinatura.ATIVA);
        
        // Assinatura inativa
        Assinatura kindle = new Assinatura(
            "kindle789",
            "Kindle Unlimited",
            "Livros digitais",
            new BigDecimal("19.90"),
            Assinatura.TipoAssinatura.MENSAL,
            usuarioId
        );
        kindle.setStatus(Assinatura.StatusAssinatura.INATIVA);
        
        assinaturas.add(prime);
        assinaturas.add(music);
        assinaturas.add(kindle);
        
        return assinaturas;
    }
}

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

/**
 * Testes de integração para funcionalidade de Histórico de Navegação - CT071 e CT072
 * RF026 - O sistema deve permitir visualizar histórico de navegação do usuário
 * RT039 - Teste para exibir histórico de navegação em itens
 * 
 * Nível de teste: Integração
 * Técnica de teste: Caixa Preta
 * Critério de seleção de entradas: Baseado em caso de uso
 */
public class HistoricoNavegacaoIntegrationTest {

    private HistoricoNavegacaoService historicoService;
    
    // Usuários de teste conforme especificação
    private final String USUARIO_COM_HISTORICO = "user456";
    private final String USUARIO_NOVO = "user789";

    @Before
    public void setUp() {
        // Configuração do ambiente de teste
        historicoService = new HistoricoNavegacaoService();
        
        // Limpar todos os históricos antes de cada teste
        historicoService.limparTodosHistoricos();
        historicoService.setSistemaAtivo(true);
    }

    /**
     * CT071 - Usuário possui histórico de navegação e é exibido corretamente
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * - Usuário possui navegação por um ou mais itens
     * 
     * Entradas:
     * - Usuário logado com ID = "user456"
     * 
     * Resultado Esperado:
     * - Lista com histórico de navegação
     * 
     * Pós-condições:
     * - Usuário vê o histórico de navegação corretamente preenchido
     */
    @Test
    public void ct071_usuarioComHistoricoExibidoCorretamente() {
        // Arrange - Configurar ambiente de teste com usuário que possui navegação
        String usuarioId = USUARIO_COM_HISTORICO;
        
        // Simular itens visitados anteriormente pelo usuário
        List<ItemNavegacao> itensPreExistentes = criarItensHistoricoTeste(usuarioId);
        historicoService.configurarHistoricoTeste(usuarioId, itensPreExistentes);
        
        // Simular acesso a mais um item (passo 2 do roteiro)
        ItemNavegacao novoItem = new ItemNavegacao(
            "item004", 
            "Smartphone Galaxy", 
            "Eletrônicos", 
            1299.99, 
            usuarioId
        );
        
        // Act - Registrar visita ao novo item e obter histórico
        HistoricoNavegacaoService.ResultadoRegistroVisita resultadoRegistro = 
            historicoService.registrarVisitaItem(usuarioId, novoItem);
        
        // Verificar se método lista os itens visitados (passo 3)
        List<ItemNavegacao> historicoObtido = historicoService.obterHistoricoNavegacao(usuarioId);
        
        // Assert - Validar o histórico exibido (passo 4)
        Assert.assertTrue("Registro da visita deve ser bem-sucedido", resultadoRegistro.isSucesso());
        Assert.assertNotNull("Lista de histórico não deve ser nula", historicoObtido);
        Assert.assertFalse("Lista de histórico não deve estar vazia", historicoObtido.isEmpty());
        
        // Verificar que o histórico contém os itens esperados (3 pré-existentes + 1 novo = 4 total)
        Assert.assertEquals("Histórico deve conter 4 itens", 4, historicoObtido.size());
        
        // Verificar que o item mais recente está no topo da lista
        ItemNavegacao itemMaisRecente = historicoObtido.get(0);
        Assert.assertEquals("Item mais recente deve estar no topo", "item004", itemMaisRecente.getId());
        Assert.assertEquals("Nome do item mais recente deve estar correto", "Smartphone Galaxy", itemMaisRecente.getNome());
        
        // Verificar que todos os itens pertencem ao usuário correto
        for (ItemNavegacao item : historicoObtido) {
            Assert.assertEquals("Todos os itens devem pertencer ao usuário", usuarioId, item.getUsuarioId());
        }
        
        // Verificar que o usuário possui histórico
        Assert.assertTrue("Usuário deve possuir histórico", historicoService.usuarioPossuiHistorico(usuarioId));
        
        // Pós-condições: Usuário vê o histórico de navegação corretamente preenchido
        Assert.assertTrue("Histórico deve estar corretamente preenchido", 
            historicoObtido.size() > 0 && 
            historicoObtido.stream().allMatch(item -> item.getUsuarioId().equals(usuarioId)));
    }

    /**
     * CT072 - Novo usuário sem histórico de navegação, sendo o histórico exibido corretamente
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * - Usuário é novo (sem histórico anterior)
     * 
     * Entradas:
     * - Usuário logado com ID = "user456" (mas sem histórico)
     * 
     * Resultado Esperado:
     * - Lista vazia
     * 
     * Pós-condições:
     * - Usuário vê o histórico vazio
     */
    @Test
    public void ct072_novoUsuarioSemHistoricoExibidoCorretamente() {
        // Arrange - Usuário novo sem histórico anterior
        String usuarioId = USUARIO_NOVO;
        
        // Garantir que o usuário não possui histórico anterior
        Assert.assertFalse("Usuário novo não deve possuir histórico", 
            historicoService.usuarioPossuiHistorico(usuarioId));
        
        // Act - Obter histórico de navegação do usuário novo
        List<ItemNavegacao> historicoObtido = historicoService.obterHistoricoNavegacao(usuarioId);
        
        // Assert - Validar que o histórico está vazio
        Assert.assertNotNull("Lista de histórico não deve ser nula", historicoObtido);
        Assert.assertTrue("Lista de histórico deve estar vazia para usuário novo", historicoObtido.isEmpty());
        Assert.assertEquals("Tamanho da lista deve ser zero", 0, historicoObtido.size());
        
        // Verificar que o usuário realmente não possui histórico
        Assert.assertFalse("Usuário não deve possuir histórico", historicoService.usuarioPossuiHistorico(usuarioId));
        
        // Pós-condições: Usuário vê o histórico vazio
        Assert.assertTrue("Histórico deve estar vazio", historicoObtido.isEmpty());
    }

    /**
     * Teste de integração adicional - Fluxo completo de navegação
     * Testa a integração entre registro de visitas e exibição do histórico
     */
    @Test
    public void testFluxoCompletoNavegacao() {
        // Arrange
        String usuarioId = "userFluxoCompleto";
        
        // Act - Simular sequência de navegação
        ItemNavegacao item1 = new ItemNavegacao("prod001", "Livro Java", "Livros", 49.90, usuarioId);
        ItemNavegacao item2 = new ItemNavegacao("prod002", "Mouse Gamer", "Periféricos", 89.90, usuarioId);
        ItemNavegacao item3 = new ItemNavegacao("prod003", "Teclado Mecânico", "Periféricos", 299.90, usuarioId);
        
        // Registrar visitas em sequência
        HistoricoNavegacaoService.ResultadoRegistroVisita resultado1 = 
            historicoService.registrarVisitaItem(usuarioId, item1);
        HistoricoNavegacaoService.ResultadoRegistroVisita resultado2 = 
            historicoService.registrarVisitaItem(usuarioId, item2);
        HistoricoNavegacaoService.ResultadoRegistroVisita resultado3 = 
            historicoService.registrarVisitaItem(usuarioId, item3);
        
        // Obter histórico final
        List<ItemNavegacao> historicoFinal = historicoService.obterHistoricoNavegacao(usuarioId);
        
        // Assert - Verificar integração completa
        Assert.assertTrue("Todas as visitas devem ser registradas com sucesso", 
            resultado1.isSucesso() && resultado2.isSucesso() && resultado3.isSucesso());
        
        Assert.assertEquals("Histórico deve conter 3 itens", 3, historicoFinal.size());
        
        // Verificar ordem cronológica reversa (mais recente primeiro)
        Assert.assertEquals("Item mais recente deve ser o teclado", "prod003", historicoFinal.get(0).getId());
        Assert.assertEquals("Segundo item deve ser o mouse", "prod002", historicoFinal.get(1).getId());
        Assert.assertEquals("Terceiro item deve ser o livro", "prod001", historicoFinal.get(2).getId());
    }

    /**
     * Teste de integração adicional - Revisita de item (deve mover para o topo)
     */
    @Test
    public void testRevisitaItemMoveParaTopo() {
        // Arrange
        String usuarioId = "userRevisita";
        
        ItemNavegacao item1 = new ItemNavegacao("prod001", "Produto A", "Categoria", 10.0, usuarioId);
        ItemNavegacao item2 = new ItemNavegacao("prod002", "Produto B", "Categoria", 20.0, usuarioId);
        ItemNavegacao item3 = new ItemNavegacao("prod003", "Produto C", "Categoria", 30.0, usuarioId);
        
        // Act - Registrar visitas e depois revisitar o primeiro item
        historicoService.registrarVisitaItem(usuarioId, item1);
        historicoService.registrarVisitaItem(usuarioId, item2);
        historicoService.registrarVisitaItem(usuarioId, item3);
        
        // Revisitar item1
        historicoService.registrarVisitaItem(usuarioId, item1);
        
        List<ItemNavegacao> historico = historicoService.obterHistoricoNavegacao(usuarioId);
        
        // Assert
        Assert.assertEquals("Deve ter 3 itens únicos", 3, historico.size());
        Assert.assertEquals("Item revisitado deve estar no topo", "prod001", historico.get(0).getId());
    }

    /**
     * Teste de integração adicional - Sistema indisponível
     */
    @Test
    public void testSistemaIndisponivelIntegracao() {
        // Arrange
        String usuarioId = "userSistemaOff";
        ItemNavegacao item = new ItemNavegacao("prod001", "Produto Teste", "Categoria", 10.0, usuarioId);
        
        // Act - Desativar sistema e tentar operações
        historicoService.setSistemaAtivo(false);
        
        HistoricoNavegacaoService.ResultadoRegistroVisita resultadoRegistro = 
            historicoService.registrarVisitaItem(usuarioId, item);
        List<ItemNavegacao> historico = historicoService.obterHistoricoNavegacao(usuarioId);
        
        // Assert
        Assert.assertFalse("Registro deve falhar com sistema indisponível", resultadoRegistro.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser adequada", 
            "Sistema temporariamente indisponível", resultadoRegistro.getMensagem());
        Assert.assertTrue("Histórico deve estar vazio com sistema indisponível", historico.isEmpty());
    }

    /**
     * Método auxiliar para criar itens de teste para o histórico
     */
    private List<ItemNavegacao> criarItensHistoricoTeste(String usuarioId) {
        List<ItemNavegacao> itens = new ArrayList<>();
        
        itens.add(new ItemNavegacao("item001", "Notebook Dell", "Informática", 2499.99, usuarioId));
        itens.add(new ItemNavegacao("item002", "Headset Gamer", "Games", 199.99, usuarioId));
        itens.add(new ItemNavegacao("item003", "Cafeteira Elétrica", "Casa", 89.90, usuarioId));
        
        return itens;
    }
}

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários para funcionalidade de Pagamento - CT068, CT069 e CT070
 * RF034 - Gerenciar formas de pagamento
 * RT038 - Teste para criar formas de pagamento para uso futuro
 */
public class PagamentoTest {

    private PagamentoService pagamentoService;
    private final String USUARIO_TESTE = "usuario.teste@email.com";

    @Before
    public void setUp() {
        pagamentoService = new PagamentoService();
        pagamentoService.limparMetodosPagamento(); // Garante que a lista está vazia
        pagamentoService.simularLogin(USUARIO_TESTE); // Simula usuário logado
    }

    /**
     * CT068 - Garantir que os dados do cartão de crédito sejam encaminhados para validação pela operadora sem erros
     * 
     * Pré-condições:
     * - Usuário deve estar logado
     * 
     * Entradas:
     * - Número do cartão: 5103 5179 2494 4745
     * - Validade: 04/11/2026
     * - CVV: 956
     * - Nome: Anderson Fozina Kruger
     * 
     * Resultado Esperado:
     * - Retorno true da validação do método
     * 
     * Pós-condições:
     * - Os dados do cartão foram encaminhados para validação pela operadora do cartão
     */
    @Test
    public void ct068_validarCartaoCreditoValido() {
        // Arrange
        String numeroCartao = "5103 5179 2494 4745";
        String validade = "04/11/2026";
        String cvv = "956";
        String nomePortador = "Anderson Fozina Kruger";
        MetodoPagamento.TipoCartao tipoCartao = MetodoPagamento.TipoCartao.CREDITO;

        // Act
        PagamentoService.ResultadoValidacaoPagamento resultado = 
            pagamentoService.adicionarMetodoPgto(numeroCartao, validade, cvv, nomePortador, tipoCartao);

        // Assert
        Assert.assertTrue("Cartão de crédito válido deve ser aceito", resultado.isSucesso());
        Assert.assertTrue("Validação da operadora deve retornar true", resultado.isValidadoOperadora());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Método de pagamento adicionado com sucesso", resultado.getMensagem());
        Assert.assertNotNull("Método de pagamento deve ser retornado", resultado.getMetodoPagamento());
        
        // Verificar dados do cartão
        MetodoPagamento metodo = resultado.getMetodoPagamento();
        Assert.assertEquals("Número do cartão deve estar correto (sem espaços)", "5103517924944745", metodo.getNumeroCartao());
        Assert.assertEquals("Validade deve estar correta", validade, metodo.getValidade());
        Assert.assertEquals("CVV deve estar correto", cvv, metodo.getCvv());
        Assert.assertEquals("Nome do portador deve estar correto", nomePortador, metodo.getNomePortador());
        Assert.assertEquals("Tipo do cartão deve ser CREDITO", MetodoPagamento.TipoCartao.CREDITO, metodo.getTipoCartao());
        
        // Verificar pós-condições
        Assert.assertTrue("Método de pagamento deve existir na lista", pagamentoService.existeMetodoPagamento(numeroCartao));
        Assert.assertEquals("Lista deve conter 1 método de pagamento", 1, pagamentoService.getMetodosPagamento().size());
    }

    /**
     * CT069 - Garantir que os dados do cartão de débito sejam encaminhados para validação pela operadora sem erros
     * 
     * Entradas:
     * - Número do cartão: 5103 5179 2494 4745
     * - Validade: 04/11/2026
     * - CVV: 956
     * - Nome: Anderson Fozina Kruger
     * 
     * Resultado Esperado:
     * - Retorno true da validação do método
     * 
     * Pós-condições:
     * - Os dados do cartão foram encaminhados para validação pela operadora do cartão
     */
    @Test
    public void ct069_validarCartaoDebitoValido() {
        // Arrange
        String numeroCartao = "5103 5179 2494 4745";
        String validade = "04/11/2026";
        String cvv = "956";
        String nomePortador = "Anderson Fozina Kruger";
        MetodoPagamento.TipoCartao tipoCartao = MetodoPagamento.TipoCartao.DEBITO;

        // Act
        PagamentoService.ResultadoValidacaoPagamento resultado = 
            pagamentoService.adicionarMetodoPgto(numeroCartao, validade, cvv, nomePortador, tipoCartao);

        // Assert
        Assert.assertTrue("Cartão de débito válido deve ser aceito", resultado.isSucesso());
        Assert.assertTrue("Validação da operadora deve retornar true", resultado.isValidadoOperadora());
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Método de pagamento adicionado com sucesso", resultado.getMensagem());
        Assert.assertNotNull("Método de pagamento deve ser retornado", resultado.getMetodoPagamento());
        
        // Verificar dados do cartão
        MetodoPagamento metodo = resultado.getMetodoPagamento();
        Assert.assertEquals("Número do cartão deve estar correto (sem espaços)", "5103517924944745", metodo.getNumeroCartao());
        Assert.assertEquals("Validade deve estar correta", validade, metodo.getValidade());
        Assert.assertEquals("CVV deve estar correto", cvv, metodo.getCvv());
        Assert.assertEquals("Nome do portador deve estar correto", nomePortador, metodo.getNomePortador());
        Assert.assertEquals("Tipo do cartão deve ser DEBITO", MetodoPagamento.TipoCartao.DEBITO, metodo.getTipoCartao());
        
        // Verificar pós-condições
        Assert.assertTrue("Método de pagamento deve existir na lista", pagamentoService.existeMetodoPagamento(numeroCartao));
        Assert.assertEquals("Lista deve conter 1 método de pagamento", 1, pagamentoService.getMetodosPagamento().size());
    }

    /**
     * CT070 - Garantir que os dados do cartão de crédito sejam rejeitados pela operadora
     * 
     * Entradas:
     * - Número do cartão: 1111 2222 3333 4444
     * - Validade: 04/11/2026
     * - CVV: 956
     * - Nome: Anderson Fozina Kruger
     * 
     * Resultado Esperado:
     * - Retorno false da validação do método
     * 
     * Pós-condições:
     * - Os dados do cartão não foram encaminhados para validação pela operadora do cartão
     */
    @Test
    public void ct070_validarCartaoCreditoInvalido() {
        // Arrange
        String numeroCartao = "1111 2222 3333 4444";
        String validade = "04/11/2026";
        String cvv = "956";
        String nomePortador = "Anderson Fozina Kruger";
        MetodoPagamento.TipoCartao tipoCartao = MetodoPagamento.TipoCartao.CREDITO;

        // Act
        PagamentoService.ResultadoValidacaoPagamento resultado = 
            pagamentoService.adicionarMetodoPgto(numeroCartao, validade, cvv, nomePortador, tipoCartao);

        // Assert
        Assert.assertFalse("Cartão inválido deve ser rejeitado", resultado.isSucesso());
        Assert.assertFalse("Validação da operadora deve retornar false", resultado.isValidadoOperadora());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Cartão não aprovado pela operadora", resultado.getMensagem());
        Assert.assertNotNull("Método de pagamento deve ser retornado mesmo sendo rejeitado", resultado.getMetodoPagamento());
        
        // Verificar que os dados foram processados mas rejeitados
        MetodoPagamento metodo = resultado.getMetodoPagamento();
        Assert.assertEquals("Número do cartão deve estar correto (sem espaços)", "1111222233334444", metodo.getNumeroCartao());
        Assert.assertEquals("Validade deve estar correta", validade, metodo.getValidade());
        Assert.assertEquals("CVV deve estar correto", cvv, metodo.getCvv());
        Assert.assertEquals("Nome do portador deve estar correto", nomePortador, metodo.getNomePortador());
        Assert.assertEquals("Tipo do cartão deve ser CREDITO", MetodoPagamento.TipoCartao.CREDITO, metodo.getTipoCartao());
        
        // Verificar pós-condições
        Assert.assertFalse("Método de pagamento não deve existir na lista", pagamentoService.existeMetodoPagamento(numeroCartao));
        Assert.assertEquals("Lista deve estar vazia", 0, pagamentoService.getMetodosPagamento().size());
    }

    /**
     * Teste adicional - Usuário sem login
     */
    @Test
    public void testAdicionarMetodoPagamentoSemLogin() {
        // Arrange
        pagamentoService.simularLogout(); // Simula logout
        String numeroCartao = "5103 5179 2494 4745";
        String validade = "04/11/2026";
        String cvv = "956";
        String nomePortador = "Anderson Fozina Kruger";

        // Act
        PagamentoService.ResultadoValidacaoPagamento resultado = 
            pagamentoService.adicionarMetodoPgto(numeroCartao, validade, cvv, nomePortador, MetodoPagamento.TipoCartao.CREDITO);

        // Assert
        Assert.assertFalse("Usuário sem login não deve conseguir adicionar método de pagamento", resultado.isSucesso());
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Usuário deve estar logado", resultado.getMensagem());
    }

    /**
     * Teste adicional - Validação de formato de entrada
     */
    @Test
    public void testValidacaoFormatoEntradas() {
        // Teste com número de cartão inválido
        PagamentoService.ResultadoValidacaoPagamento resultado1 = 
            pagamentoService.adicionarMetodoPgto("123", "04/11/2026", "956", "Anderson Fozina Kruger", MetodoPagamento.TipoCartao.CREDITO);
        Assert.assertFalse("Número de cartão muito curto deve ser rejeitado", resultado1.isSucesso());

        // Teste com validade inválida
        PagamentoService.ResultadoValidacaoPagamento resultado2 = 
            pagamentoService.adicionarMetodoPgto("5103517924944745", "04/2026", "956", "Anderson Fozina Kruger", MetodoPagamento.TipoCartao.CREDITO);
        Assert.assertFalse("Formato de validade inválido deve ser rejeitado", resultado2.isSucesso());

        // Teste com CVV inválido
        PagamentoService.ResultadoValidacaoPagamento resultado3 = 
            pagamentoService.adicionarMetodoPgto("5103517924944745", "04/11/2026", "12", "Anderson Fozina Kruger", MetodoPagamento.TipoCartao.CREDITO);
        Assert.assertFalse("CVV muito curto deve ser rejeitado", resultado3.isSucesso());

        // Teste com nome vazio
        PagamentoService.ResultadoValidacaoPagamento resultado4 = 
            pagamentoService.adicionarMetodoPgto("5103517924944745", "04/11/2026", "956", "", MetodoPagamento.TipoCartao.CREDITO);
        Assert.assertFalse("Nome vazio deve ser rejeitado", resultado4.isSucesso());
    }

    /**
     * Teste adicional - Múltiplos métodos de pagamento
     */
    @Test
    public void testMultiplosMetodosPagamento() {
        // Arrange
        String numeroCartao1 = "5103 5179 2494 4745"; // Válido
        String numeroCartao2 = "1111 2222 3333 4444"; // Inválido

        // Act
        PagamentoService.ResultadoValidacaoPagamento resultado1 = 
            pagamentoService.adicionarMetodoPgto(numeroCartao1, "04/11/2026", "956", "Anderson Fozina Kruger", MetodoPagamento.TipoCartao.CREDITO);
        
        PagamentoService.ResultadoValidacaoPagamento resultado2 = 
            pagamentoService.adicionarMetodoPgto(numeroCartao2, "04/11/2026", "956", "Anderson Fozina Kruger", MetodoPagamento.TipoCartao.DEBITO);

        // Assert
        Assert.assertTrue("Primeiro cartão deve ser aceito", resultado1.isSucesso());
        Assert.assertFalse("Segundo cartão deve ser rejeitado", resultado2.isSucesso());
        Assert.assertEquals("Deve haver apenas 1 método de pagamento na lista", 1, pagamentoService.getMetodosPagamento().size());
    }
}

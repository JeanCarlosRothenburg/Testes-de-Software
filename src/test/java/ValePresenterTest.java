import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Testes unitários para funcionalidade de vale presente - CT061 e CT062
 * RF025 - Uso de cupom de descontos em compras
 * RT035 - Teste de limites de aplicação de vale presente em compras
 */
public class ValePresenterTest {

    private CarrinhoService carrinhoService;

    @Before
    public void setUp() {
        carrinhoService = new CarrinhoService();
        carrinhoService.limparSaldoCarteira(); // Garante que a carteira está zerada
    }

    /**
     * CT061 - Validar a aplicação correta de um cupom de vale-presente durante a compra
     * 
     * Pré-condições:
     * - Usuário deve estar logado no sistema
     * - Vale presente deve estar ativo e válido
     * 
     * Entradas:
     * - códigoCupom = "VALE123"
     * - valor vale = 100.00
     * - totalCarrinho = 150
     * 
     * Resultado Esperado:
     * - Desconto aplicado corretamente no valor total da compra
     * - Valor final a pagar = 50.00 (150 - 100)
     */
    @Test
    public void ct061_aplicarValePresenterComCarrinhoMaiorQueVale() {
        // Arrange
        String codigoCupom = "VALE123";
        double valorVale = 100.00;
        double totalCarrinho = 150.00;

        // Act
        CarrinhoService.ResultadoValePresente resultado = 
            carrinhoService.adicionarSaldoValePresente(codigoCupom, valorVale, totalCarrinho);

        // Assert
        Assert.assertTrue("Vale presente deve ser aplicado com sucesso", resultado.isSucesso());
        Assert.assertEquals("Valor final do pagamento deve ser 50.00", 50.00, resultado.getValorFinalPagamento(), 0.01);
        Assert.assertEquals("Não deve haver valor salvo na carteira", 0.00, resultado.getValorSalvoCarteira(), 0.01);
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Desconto aplicado com sucesso", resultado.getMensagem());
    }

    /**
     * CT062 - Validar a aplicação correta de um cupom de vale-presente durante a compra 
     * com carrinho com valor total inferior ao do vale presente
     * 
     * Entradas:
     * - códigoCupom = "VALE123"
     * - valor vale = 100.00
     * - totalCarrinho = 90
     * 
     * Resultado Esperado:
     * - Desconto aplicado até o limite zero do saldo a pagar
     * - Valor final a pagar = 0.00
     * - Valor salvo em carteira = 10.00 (100 - 90)
     */
    @Test
    public void ct062_aplicarValePresenterComCarrinhoMenorQueVale() {
        // Arrange
        String codigoCupom = "VALE123";
        double valorVale = 100.00;
        double totalCarrinho = 90.00;

        // Act
        CarrinhoService.ResultadoValePresente resultado = 
            carrinhoService.adicionarSaldoValePresente(codigoCupom, valorVale, totalCarrinho);

        // Assert
        Assert.assertTrue("Vale presente deve ser aplicado com sucesso", resultado.isSucesso());
        Assert.assertEquals("Valor final do pagamento deve ser 0.00", 0.00, resultado.getValorFinalPagamento(), 0.01);
        Assert.assertEquals("Valor salvo na carteira deve ser 10.00", 10.00, resultado.getValorSalvoCarteira(), 0.01);
        Assert.assertEquals("Saldo da carteira deve ser atualizado", 10.00, carrinhoService.getSaldoCarteira(), 0.01);
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Desconto aplicado com sucesso", resultado.getMensagem());
    }

    /**
     * Teste adicional - Vale presente com código inválido
     */
    @Test
    public void testValePresenterComCodigoInvalido() {
        // Arrange
        String codigoCupom = "INVALIDO";
        double valorVale = 100.00;
        double totalCarrinho = 150.00;

        // Act
        CarrinhoService.ResultadoValePresente resultado = 
            carrinhoService.adicionarSaldoValePresente(codigoCupom, valorVale, totalCarrinho);

        // Assert
        Assert.assertFalse("Vale presente inválido deve retornar falha", resultado.isSucesso());
        Assert.assertEquals("Valor do carrinho deve permanecer inalterado", totalCarrinho, resultado.getValorFinalPagamento(), 0.01);
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Vale presente inválido ou inativo", resultado.getMensagem());
    }

    /**
     * Teste adicional - Vale presente com valor zero
     */
    @Test
    public void testValePresenterComValorZero() {
        // Arrange
        String codigoCupom = "VALE123";
        double valorVale = 0.00;
        double totalCarrinho = 150.00;

        // Act
        CarrinhoService.ResultadoValePresente resultado = 
            carrinhoService.adicionarSaldoValePresente(codigoCupom, valorVale, totalCarrinho);

        // Assert
        Assert.assertFalse("Vale presente com valor zero deve retornar falha", resultado.isSucesso());
        Assert.assertEquals("Valor do carrinho deve permanecer inalterado", totalCarrinho, resultado.getValorFinalPagamento(), 0.01);
        Assert.assertEquals("Mensagem de erro deve ser exibida", "Valor do vale deve ser positivo", resultado.getMensagem());
    }

    /**
     * Teste adicional - Vale presente com valor exatamente igual ao carrinho
     */
    @Test
    public void testValePresenterComValorIgualCarrinho() {
        // Arrange
        String codigoCupom = "VALE123";
        double valorVale = 100.00;
        double totalCarrinho = 100.00;

        // Act
        CarrinhoService.ResultadoValePresente resultado = 
            carrinhoService.adicionarSaldoValePresente(codigoCupom, valorVale, totalCarrinho);

        // Assert
        Assert.assertTrue("Vale presente deve ser aplicado com sucesso", resultado.isSucesso());
        Assert.assertEquals("Valor final do pagamento deve ser 0.00", 0.00, resultado.getValorFinalPagamento(), 0.01);
        Assert.assertEquals("Não deve haver valor salvo na carteira", 0.00, resultado.getValorSalvoCarteira(), 0.01);
        Assert.assertEquals("Mensagem de sucesso deve ser exibida", "Desconto aplicado com sucesso", resultado.getMensagem());
    }
}

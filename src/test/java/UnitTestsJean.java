import Model.*;
import Model.Bo.ModelBoCarrinho;
import Model.Bo.ModelBoDesconto;
import Model.Bo.ModelBoPerfil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests
 * @author Jean Carlos Rothenburg
 */
public class UnitTestsJean {

    // region Test Case 5.2.1

    @Test
    public void validaElegibilidadeFreteGratisPedido() {
        // Arrange
        ModelCarrinho carrinho = new ModelCarrinho();
        carrinho.setNomeComerciante("Amazon.com.br");
        carrinho.setValorTotal(100);

        // Act
        boolean isElegivelFreteGratis = ModelBoCarrinho.isElegivelFreteGratis(carrinho);

        // Assert
        Assert.assertTrue("Elegibilidade do frete não identificada para pedido válido!", isElegivelFreteGratis);
    }

    @Test
    public void validaInelegibilidadeFreteGratisPedidoByValorTotal() {
        // Arrange
        ModelCarrinho carrinho = new ModelCarrinho();
        carrinho.setNomeComerciante("Amazon.com.br");
        carrinho.setValorTotal(99.99);

        // Act
        boolean isElegivelFreteGratis = ModelBoCarrinho.isElegivelFreteGratis(carrinho);

        // Assert
        Assert.assertFalse("Definida elegibilidade de frete grátis para pedido com valor mínimo não atingido", isElegivelFreteGratis);
    }

    @Test
    public void validaInelegibilidadeFreteGratisPedidoByComerciante() {
        // Arrange
        ModelCarrinho carrinho = new ModelCarrinho();
        carrinho.setNomeComerciante("Loja Lorem Ipsum");
        carrinho.setValorTotal(100);

        // Act
        boolean isElegivelFreteGratis = ModelBoCarrinho.isElegivelFreteGratis(carrinho);

        // Assert
        Assert.assertFalse("Definida Elegibilidade de frete grátis para pedido não comercializado pela Amazon!", isElegivelFreteGratis);
    }

    // endregion
    // region Test Case 5.2.2

    @Test
    public void validaAplicacaoDescontoPercentualInferiorAZero() {
        // Arrange
        ModelProduto produto = new ModelProduto();
        produto.setValor(100);
        double percentualDesconto = -0.01;

        // Act
        try {
            ModelBoDesconto.calcularDesconto(produto, percentualDesconto);
        } catch (PercentualException e) {
            // Assert
            Assert.assertEquals("Percentual de desconto inválido!", e.getMessage());
        }
    }

    @Test
    public void validaAplicacaoDescontoPercentualMinimoValido() throws PercentualException {
        // Arrange
        ModelProduto produto = new ModelProduto();
        produto.setValor(100);
        double percentualDesconto = 0.01;

        // Act
        double valorWithDesconto = ModelBoDesconto.calcularDesconto(produto, percentualDesconto);

        // Assert
        Assert.assertEquals("Valor incorreto para o produto com o percentual de desconto aplicado!", 99.99, valorWithDesconto, 0.001);
    }

    @Test
    public void validaAplicacaoDescontoPercentualMaximoValido() throws PercentualException {
        // Arrange
        ModelProduto produto = new ModelProduto();
        produto.setValor(100);
        double percentualDesconto = 100;

        // Act
        double valorWithDesconto = ModelBoDesconto.calcularDesconto(produto, percentualDesconto);

        // Assert
        Assert.assertEquals("Valor incorreto para o produto com o valor percentual de desconto aplicado!", 0, valorWithDesconto, 0.001);
    }

    @Test
    public void validaAplicacaoDescontoPercentualSuperiorACem() {
        // Arrange
        ModelProduto produto = new ModelProduto();
        produto.setValor(100);
        double percentualDesconto = 100.01;

        // Act
        try {
            ModelBoDesconto.calcularDesconto(produto, percentualDesconto);
        } catch (PercentualException e) {
            // Assert
            Assert.assertEquals("Percentual de desconto inválido!", e.getMessage());
        }
    }
    // endregion
    // region Teste Case 5.2.3

    @Test
    public void validaAlturaPerfilContaInferiorAoMinimo() {
        // Arrange
        ModelPerfil perfil = new ModelPerfil();
        perfil.setAltura(89);
        ModelConta conta = new ModelConta();
        conta.setPerfil(perfil);

        // Act
        boolean isAlturaValida = ModelBoPerfil.isAlturaValida(conta);

        // Assert
        Assert.assertFalse("A altura do perfil é inferior a altura mínima!", isAlturaValida);
    }

    @Test
    public void validaAlturaPerfilContaDentroIntervaloValido() {
        // Arrange
        ModelPerfil perfil = new ModelPerfil();
        perfil.setAltura(165);
        ModelConta conta = new ModelConta();
        conta.setPerfil(perfil);

        // Act
        boolean isAlturaValida = ModelBoPerfil.isAlturaValida(conta);

        // Assert
        Assert.assertTrue("A altura do perfil está dentro do intervalo válido!", isAlturaValida);
    }

    @Test
    public void validaAlturaPerfilContaSuperiorAoMaximo() {
        // Arrange
        ModelPerfil perfil = new ModelPerfil();
        perfil.setAltura(241);
        ModelConta conta = new ModelConta();
        conta.setPerfil(perfil);

        // Act
        boolean isAlturaValida = ModelBoPerfil.isAlturaValida(conta);

        // Assert
        Assert.assertFalse("A altura do perfil é superior a altura máxima!", isAlturaValida);
    }

    // endregion
}

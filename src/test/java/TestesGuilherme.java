import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestesGuilherme {

    //Esta classe abrange os casos de teste do CT041 ao CT054 (Guilherme Wolff


    //Testes Unitários --------------------------------------------------------------------

    @Test
    public void adicionarEnderecoValidoNaoCadastrado() { //CT041

        //Arrange
        EnderecoService service = new EnderecoService();

        //Act
        String retorno = service.adicionarEndereco("Rua Ibirama,n° 100, SC");

        //Assert
        Assert.assertEquals("Endereço adicionado com sucesso", retorno);
    }

    @Test
    public void adicionarEnderecoValidoJaCadastrado() { //CT042

        //Arrange
        EnderecoService service = new EnderecoService();

        //Act
        service.adicionarEndereco("Rua Ibirama,n° 100, SC"); //Adiciona o endereço pele primeira vez
        String retorno = service.adicionarEndereco("Rua Ibirama,n° 100, SC"); //Força a duplicação do endereço

        //Assert
        Assert.assertEquals("Endereço já cadastrado", retorno);
    }

    @Test
    public void adicionarEnderecoInvalido() { //CT043

        //Arrange
        EnderecoService service = new EnderecoService();

        //Act
        String retorno = service.adicionarEndereco("n° 100, SC");

        //Assert
        Assert.assertEquals("Endereço inválido", retorno);
    }

    @Test
    public void pagamentoCartaoCredito() { //CT044

        //Arrange
        PagamentoService service = new PagamentoService();

        //Act
        String retorno = service.selecionarFormaDePagamento("Cartão de crédito");

        //Assert
        Assert.assertEquals("Pagamento efetuado com Cartão de crédito", retorno);
    }

    @Test
    public void pagamentoInvalido() { //CT045

        //Arrange
        PagamentoService service = new PagamentoService();

        //Act
        String retorno = service.selecionarFormaDePagamento(null);

        //Assert
        Assert.assertEquals("Forma de pagamento inválida", retorno);
    }

    @Test
    public void validarDadosPreenchidosCorretamente() { //CT046

        //Arrange
        CheckoutService service = new CheckoutService();

        //Act
        boolean valido = service.verificarDados("Rua ibirama,n° 100,SC", "Cartão de crédito");

        //Assert
        Assert.assertTrue(valido);
    }

    @Test
    public void validarPedidoComDadosAusentes() { //CT047

        //Arrange
        CheckoutService service = new CheckoutService();

        //Act
        boolean valido = service.verificarDados("Rua ibirama,n° 100,SC", null);

        //Assert
        Assert.assertFalse(valido);
    }
    @Test
    public void calculoFreteCepValido() { //CT048

        //Arrange
        FreteService service = new FreteService();

        //Act
        String retorno = service.calcularFrete("11001-000");

        //Assert
        Assert.assertEquals("Frete: R$15,00", retorno);
    }

    @Test
    public void calculoFreteCepInvalido() { //CT049

        //Arrange
        FreteService service = new FreteService();

        //Act
        String retorno = service.calcularFrete("88888888");

        //Assert
        Assert.assertEquals("Nenhuma opção de envio encontrada", retorno);
    }

    @Test
    public void rastreioEncomendaValida() { //CT050

        //Arrange
        RastreioService service = new RastreioService();

        //Act
        String resultado = service.rastrearEncomenda("QM7638493BR");

        //Assert
        Assert.assertEquals("Encomenda em trânsito - São Paulo/SP", resultado);
    }

    //Testes de Integração  --------------------------------------------------------------------

    private ProdutoDAO daoPro;

    @Before
    public void setUpProduto() { daoPro = new ProdutoDAO();
    }

    @Test
    public void filtrarPorCategoriaEPreco() { //CT051
        //Act
        List<String> resultado = daoPro.aplicarFiltro("Eletrônicos", 100, 200, null);

        //Assert
        Assert.assertFalse(resultado.isEmpty());
    }

    @Test
    public void filtrarPorCategoriaPrecoENota() { //CT052
        //Act
        List<String> resultado = daoPro.aplicarFiltro("Eletrônicos", 100, 200, 5);

        //Assert
        Assert.assertFalse(resultado.isEmpty());
    }

    private CompraDAO daoCom;

    @Before
    public void setUpCompra() { daoCom = new CompraDAO();
    }

    @Test
    public void calcularTotalComFrete() { //CT053
        //Arrange
        double carrinho = 200.00;
        String cep = "11001-000";

        //Act
        double total = daoCom.calcularTotalComFrete(carrinho, cep);

        //Assert
        Assert.assertEquals(215.00, total, 0.01);
    }

    @Test
    public void finalizarCompraPIX() { //CT054
        //Arrange
        String email = "guilherme@email.com";
        String produto = "Fone de ouvido";
        double total = 199.90;

        //Act
        String resultado = daoCom.finalizarCompraPIX(email, produto, total);

        //Assert
        Assert.assertEquals("Compra realizada com sucesso via PIX", resultado);
    }

}
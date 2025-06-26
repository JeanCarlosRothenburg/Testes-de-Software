import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

public class TestesLucas {

    //Esta classe abrange os casos de teste do CT001 ao CT014 (Lucas


    //Testes Unitários --------------------------------------------------------------------

    @Test
    public void loginComUsuarioValido() { //CT001

        //Arrange
        LoginService service = new LoginService();

        //Act
        boolean resultado = service.realizarLogin("lucasteste@email.com", "123456");

        //Assert
        Assert.assertEquals("Sucesso no Login", true, resultado);
    }

    @Test
    public void loginComUsuarioInvalido() { //CT002

        //Arrange
        LoginService service = new LoginService();

        //Act
        boolean resultado = service.realizarLogin("lucasteste@universal.com", "1234");

        //Assert
        Assert.assertEquals("Falha no Login", false, resultado);
    }

    @Test
    public void buscarProdutoComTermoValido() { //CT003
        ProdutoService service = new ProdutoService();
        List<String> resultado = service.buscarProduto("alexa");

        Assert.assertFalse("Sucesso na busca", resultado.isEmpty());
    }

    @Test
    public void buscarProdutoComTermoInvalido() { //CT004
        ProdutoService service = new ProdutoService();
        List<String> resultado = service.buscarProduto("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        Assert.assertTrue("Nenhum produto encontrado", resultado.isEmpty());
    }

    @Test
    public void adicionarProdutoComEstoqueDisponivel() { //CT005
        CarrinhoService service = new CarrinhoService();
        String resultado = service.adicionarProduto("alexa", 5);

        Assert.assertEquals("Produto adicionado ao carrinho", resultado);
    }

    @Test
    public void calcularValorTotalCarrinho() { //CT006
        CarrinhoService service = new CarrinhoService();
        double[] precos = {100.0, 150.0, 50.0};
        double total = service.calcularValorCarrinho(precos);

        Assert.assertEquals(300.0, total, 0.01);
    }

    @Test
    public void compraSemLogin() { //CT007
        CompraService service = new CompraService();
        String resultado = service.comprarDireto("alexa", false);

        Assert.assertEquals("Usuário precisa estar logado", resultado);
    }

    @Test
    public void compraComLogin() { //CT008
        CompraService service = new CompraService();
        String resultado = service.comprarDireto("alexa", true);

        Assert.assertEquals("Compra realizada com sucesso", resultado);
    }

    @Test
    public void avaliacaoValida() { //CT009
        AvaliacaoService service = new AvaliacaoService();
        String resultado = service.avaliarCompra("Excelente produto, muito confortável e bonito.");

        Assert.assertEquals("Avaliação registrada com sucesso", resultado);
    }

    @Test
    public void avaliacaoInvalida() { //CT010
        AvaliacaoService service = new AvaliacaoService();
        String resultado = service.avaliarCompra("Muito bom");

        Assert.assertEquals("Comentário deve ter entre 20 e 500 caracteres", resultado);
    }

    //Testes de Integração  --------------------------------------------------------------------

    private UsuarioDAO daoUser;

    @Before
    public void setUpUsuario() {
        daoUser = new UsuarioDAO();
    }

    @Test
    public void criarContaComEmailNovo() { //CT011
        String email = "lucas@gmail.com";
        daoUser.excluirContaPorEmail(email); // garante que o teste será limpo

        boolean resultado = daoUser.criarConta(email, "Lucas Ratzmann", "Lucas123");
        Assert.assertTrue("Conta criada com sucesso", resultado);
    }

    @Test
    public void criarContaComEmailExistente() { //CT012
        String email = "lucas@gmail.com";
        daoUser.criarConta(email, "Lucas Ratzmann", "Lucas123"); // já cria

        boolean resultado = daoUser.criarConta(email, "Lucas Ratzmann", "Lucas123");
        Assert.assertFalse("Falha esperada ao tentar criar com e-mail já existente", resultado);
    }

    @Test
    public void loginComContaValida() { //CT013
        String email = "lucas@gmail.com";
        String senha = "Lucas123";
        daoUser.criarConta(email, "Lucas Ratzmann", senha); // garante que existe

        boolean resultado = daoUser.fazerLogin(email, senha);
        Assert.assertTrue("Login válido realizado com sucesso", resultado);
    }

    private CompraDAO daoCompra;

    @Before
    public void setUpCompra() {
        daoCompra = new CompraDAO();
    }

    @Test
    public void ct014_refazerCompraRapida() { //CT014
        int idCompra = daoCompra.criarCompraDeTeste("lucas@gmail.com", "Echo Pop");
        Assert.assertTrue("ID de compra de teste deve ser válido", idCompra > 0);

        String resultado = daoCompra.comprarNovamente(idCompra);
        Assert.assertEquals("Compra refeita com sucesso", resultado);
    }
}

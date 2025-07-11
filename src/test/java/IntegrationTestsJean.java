import DAO.DAOConta;
import Model.Bo.ModelBoProcessConta;
import Model.Conta;
import Model.UpdateException;
import org.junit.Before;
import org.junit.Test;
import Enum.EnumUpdate;
import org.junit.Assert;

/**
 * Integration Tests
 * @author Jean Carlos Rothenburg
 */
public class IntegrationTestsJean {

    private Conta conta;

    @Before
    public void setup() {
        this.conta = new Conta();
        conta.setEmail("teste@gmail.com");
        conta.setSenha("123@teste");
        this.conta = (new DAOConta()).insert(conta);
    }

    @Test
    public void atualizaContaWithMethodUpdate() {
        // Arrange
        this.conta.setSenha("teste@123");
        EnumUpdate metodo = EnumUpdate.METHOD_UPDATE;

        // Act
        boolean sucess = ModelBoProcessConta.update(this.conta, metodo);

        // Assert
        Assert.assertTrue("Falha ao alterar conta utilizando o método de atualização UPDATE!", sucess);
    }

    @Test
    public void atualizaContaWithMethodDrop() {
        // Arrange
        this.conta.setSenha("teste@123");
        EnumUpdate metodo = EnumUpdate.METHOD_DROP;

        // Act
        boolean sucess = ModelBoProcessConta.update(this.conta, metodo);

        // Assert
        Assert.assertTrue("Falha ao alterar conta utilizando o método de atualização DROP!", sucess);
    }

    @Test
    public void atualizaContaWithMethodUpdateWithoutChanges() {
        // Arrange
        EnumUpdate metodo = EnumUpdate.METHOD_UPDATE;

        // Act
        try {
            ModelBoProcessConta.update(this.conta, metodo);
        } catch (UpdateException e) {
            // Assert
            Assert.assertEquals("Não houve alteração dos dados da conta!", e.getMessage());
        }

    }
}

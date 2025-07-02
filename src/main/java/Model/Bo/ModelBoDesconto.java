package Model.Bo;

import Model.ModelProduto;
import Model.PercentualException;

public class ModelBoDesconto {

    public static double calcularDesconto(ModelProduto produto, double percentualDesconto) throws PercentualException {
        if ((percentualDesconto < 0) || (percentualDesconto > 100)) {
            throw new PercentualException("Percentual de desconto inválido!");
        }

        return produto.getValor() - (produto.getValor() * (percentualDesconto / 100));
    }
}

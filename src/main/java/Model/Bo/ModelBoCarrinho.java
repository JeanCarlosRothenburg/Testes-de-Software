package Model.Bo;

import Model.ModelCarrinho;

public class ModelBoCarrinho {

    public static boolean isElegivelFreteGratis(ModelCarrinho carrinho) {
        return ((carrinho.getNomeComerciante().equals("Amazon.com.br")) && (carrinho.getValorTotal() >= 100));
    }

}

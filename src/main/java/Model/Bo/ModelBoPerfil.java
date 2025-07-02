package Model.Bo;

import Model.ModelConta;

public class ModelBoPerfil {

    public static boolean isAlturaValida(ModelConta conta) {
        double alturaPerfil = conta.getPerfil().getAltura();

        return ((alturaPerfil > 89) && (alturaPerfil < 240));
    }
}

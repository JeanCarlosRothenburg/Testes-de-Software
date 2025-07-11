package Model.Bo;

import DAO.DAOConta;
import DAO.InterfaceDAO;
import Model.Conta;
import Enum.EnumUpdate;

public class ModelBoProcessConta {

    private InterfaceDAO daoConta;

    ModelBoProcessConta(InterfaceDAO daoConta) {
        this.daoConta = daoConta;
    }

    public static boolean update (Conta conta, EnumUpdate metodo) {
        DAOConta daoConta = new DAOConta();
        switch (metodo) {
            case METHOD_UPDATE:
                return daoConta.update(conta);
            case METHOD_DROP:
                return daoConta.updateWithDrop(conta);
            default:
                return false;
        }
    }

}
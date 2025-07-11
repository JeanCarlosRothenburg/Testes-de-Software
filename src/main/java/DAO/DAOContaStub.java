package DAO;

import Model.Conta;

public class DAOContaStub implements InterfaceDAO {

    public boolean update(Conta conta) {
        return true;
    }

    public boolean updateWithDrop(Conta conta) {
        return true;
    }

}
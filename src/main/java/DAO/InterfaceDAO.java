package DAO;

import Model.Conta;

public interface InterfaceDAO {

    public abstract boolean update(Conta conta);

    public abstract boolean updateWithDrop(Conta conta);

}
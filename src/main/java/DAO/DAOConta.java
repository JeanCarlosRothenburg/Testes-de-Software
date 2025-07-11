package DAO;

import Model.Conta;
import Model.UpdateException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DAOConta implements InterfaceDAO {
    private Connection con;

    public DAOConta() {
        this.con = DBConnection.getConnection();
    }

    public Conta insert(Conta conta) {
        String sql = "INSERT INTO conta (email, senha) VALUES (?,?)";

        try {
            PreparedStatement stm = this.con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setString(1, conta.getEmail());
            stm.setString(2, conta.getSenha());
            if (stm.executeUpdate() > 0) {
                ResultSet rs = stm.getGeneratedKeys();
                if (rs.next()) {
                    conta.setCodigo(rs.getInt(1));
                    return conta;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean update(Conta conta) {
        Conta c = get(conta);

        if (c == null) {
            return false;
        }

        StringBuilder sql = new StringBuilder("UPDATE conta SET ");
        List<Object> values = new ArrayList<Object>();
        if (!Objects.equals(conta.getEmail(), conta.getEmail())) {
            sql.append("email = ? ");
            values.add(conta.getEmail());
        }
        if (!Objects.equals(conta.getSenha(), c.getSenha())) {
            sql.append("senha = ? ");
            values.add(c.getSenha());
        }

        if (values.isEmpty()) {
            throw new UpdateException("Não houve alteração dos dados da conta!");
        }

        sql.setLength(sql.length() - 1);
        sql.append(" WHERE codigo = ?");
        values.add(c.getCodigo());

        try {
            PreparedStatement stm = con.prepareStatement(sql.toString());

            for (int i = 0; i < values.size(); i++) {
                stm.setObject(i + 1, values.get(i));
            }

            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateWithDrop(Conta conta) {
        Conta c = get(conta);

        if (c == null || !delete(conta)) {
            return false;
        }

        try {
            return insert(conta) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Conta get(Conta conta) {
        String sql = "SELECT * FROM conta WHERE codigo = ?";

        try {
            PreparedStatement stm = this.con.prepareStatement(sql);
            stm.setInt(1, conta.getCodigo());
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                Conta c = new Conta();
                c.setCodigo(rs.getInt("codigo"));
                c.setEmail(rs.getString("email"));
                c.setSenha(rs.getString("senha"));

                return c;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean delete(Conta conta) {
        String sql = "DELETE FROM conta WHERE codigo = ?";

        try {
            PreparedStatement stm = this.con.prepareStatement(sql);
            stm.setInt(1, conta.getCodigo());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

}

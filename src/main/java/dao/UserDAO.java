package dao;

import database.MySqlConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrique on 13/06/2016.
 */
public class UserDAO {

    public User getUser(String username) {
        User user = new User();

        try (Connection con = MySqlConnection.getConnection()) {
            String sql = "SELECT DISTINCT username, nome FROM ppgcc.users "
                         + "WHERE username = ?";

            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);

            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                user.setName(rs.getString("username"));
                user.setName(rs.getString("nome"));
            }

            user.setRoles(getRoles(username));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean authenticate(String username, String password) {
        boolean ret = false;

        try (Connection con = MySqlConnection.getConnection()) {
            String sql = "SELECT DISTINCT username, nome FROM ppgcc.users "
                    + "WHERE username = ? and senha = ?";

            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            p.setString(2, password);

            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                ret = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public List<String> getRoles(String username) {
        List<String> roles = new ArrayList();

        try (Connection con = MySqlConnection.getConnection()) {
            String sql = "SELECT role FROM ppgcc.roles "
                        + "WHERE username = ?";

            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);

            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roles;
    }
}

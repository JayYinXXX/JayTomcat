package tmall.dao;

import tmall.bean.User;
import tmall.util.DBUtil;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // 增
    public void add(User bean) {
        String sql = "INSERT INTO user VALUES(null, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, bean.getName());
            ps.setString(2, bean.getPassword());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                bean.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 删
    public void delete(int id) {
        String sql = "DELETE FROM user WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 改
    public void update(User bean) {
        String sql = "UPDATE user SET name=?, password=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, bean.getName());
            ps.setString(2, bean.getPassword());
            ps.setInt(3, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // 查
    public User get(int id) {
        String sql = "SELECT * FROM user WHERE id=?";
        User bean = null;
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bean = new User();
                bean.setId(id);
                bean.setName(rs.getString("name"));
                bean.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }
    public User get(String name) {
        String sql = "SELECT * FROM user WHERE name=?";
        User bean = null;
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bean = new User();
                bean.setId(rs.getInt("id"));
                bean.setName(name);
                bean.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }
    public User get(String name, String password) {
        User bean = null;
        String sql = "SELECT * FROM user WHERE name=? AND password=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, name);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bean = new User();
                bean.setId(rs.getInt("id"));
                bean.setName(name);
                bean.setPassword(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }
    // 查询用户总数
    public int getTotal() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM user";
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    // 查询用户是否存在
    public boolean isExist(String name) {
        User bean = get(name);
        return bean != null;
    }
    // 列出用户信息
    public List<User> list() {
        return list(0, Short.MAX_VALUE);
    }
    public List<User> list(int start, int count) {
        List<User> beans = new ArrayList<User>();
        String sql = "SELECT * FROM user ORDER BY id DESC LIMIT ?,?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, start);
            ps.setInt(2, count);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User bean = new User();
                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setPassword(rs.getString("password"));
                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beans;
    }
}

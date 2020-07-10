package tmall.dao;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.DBUtil;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertyDAO {
    // 增
    public void add(Property bean) {
        String sql = "INSERT INTO property VALUES(null, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getCategory().getId());
            ps.setString(2, bean.getName());
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
        String sql = "DELETE FROM property WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 改
    public void update(Property bean) {
        String sql = "UPDATE property SET cid=?, name=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getCategory().getId());
            ps.setString(2, bean.getName());
            ps.setInt(3, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 查
    public Property get(int id) {
        Property bean = null;
        String sql = "SELECT * FROM property WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bean = new Property();
                bean.setId(id);
                int cid = rs.getInt("cid");
                bean.setCategory(new CategoryDAO().get(cid));
                bean.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }

    // 查询指定分类的记录数
    public int getTotal(int cid) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM property WHERE cid=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // 列出指定分类的属性记录
    public List<Property> list(int cid, int start, int count) {
        List<Property> beans = new ArrayList<>();
        String sql = "SELECT * FROM property WHERE cid=? ORDER BY id DESC LIMIT ?,?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, cid);
            ps.setInt(2, start);
            ps.setInt(3, count);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Property bean = new Property();

                bean.setId(rs.getInt("id"));
                Category category = new CategoryDAO().get(rs.getInt("cid"));
                bean.setCategory(category);
                bean.setName(rs.getString("name"));

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beans;
    }
    public List<Property> list(int cid) {
        return list(cid, 0, Short.MAX_VALUE);
    }
}

package tmall.dao;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDAO {
    public static final String type_single = "type_single";  // 外观图
    public static final String type_detail = "type_detail";  // 详情图

    // 增
    public void add(ProductImage bean) {
        String sql = "INSERT INTO productimage VALUES(null,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getProduct().getId());
            ps.setString(2, bean.getType());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                bean.setId(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 删
    public void delete(int id) {
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            String sql = "delete from ProductImage where id = " + id;
            s.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 改

    // 查
    public ProductImage get(int id) {
        ProductImage bean = new ProductImage();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            String sql = "select * from ProductImage where id = " + id;
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                bean.setId(id);
                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);
                String type = rs.getString("type");
                bean.setType(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }

    // 查询图片总数
    public int getTotal() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM productimage";
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

    // 列出图片
    public List<ProductImage> list(Product p, String type) {
        return list(p, type,0, Short.MAX_VALUE);
    }
    public List<ProductImage> list(Product p, String type, int start, int count) {
        List<ProductImage> beans = new ArrayList<ProductImage>();
        String sql = "select * from ProductImage where pid =? and type =? order by id desc limit ?,? ";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, p.getId());
            ps.setString(2, type);
            ps.setInt(3, start);
            ps.setInt(4, count);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProductImage bean = new ProductImage();
                int id = rs.getInt(1);
                bean.setId(id);
                bean.setProduct(p);
                bean.setType(type);
                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beans;
    }

}


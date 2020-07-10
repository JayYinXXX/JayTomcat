package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Property;
import tmall.bean.PropertyValue;
import tmall.util.DBUtil;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyValueDAO {
    // 增
    public void add(PropertyValue bean) {

        String sql = "insert into PropertyValue values(null,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getProduct().getId());
            ps.setInt(2, bean.getProperty().getId());
            ps.setString(3, bean.getValue());
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
        String sql = "delete from PropertyValue where id = " + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            s.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 改
    public void update(PropertyValue bean) {
        String sql = "update PropertyValue set pid= ?, ptid=?, value=?  where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getProduct().getId());
            ps.setInt(2, bean.getProperty().getId());
            ps.setString(3, bean.getValue());
            ps.setInt(4, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // 查
    public PropertyValue get(int id) {
        PropertyValue bean = new PropertyValue();
        String sql = "select * from PropertyValue where id = " + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                bean.setId(id);

                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                int ptid = rs.getInt("ptid");
                Property property = new PropertyDAO().get(ptid);
                bean.setProperty(property);

                String value = rs.getString("value");
                bean.setValue(value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }
    // pid ptid 确定一个属性值
    public PropertyValue get(int ptid, int pid ) {
        PropertyValue bean = null;
        String sql = "select * from PropertyValue where ptid = " + ptid + " and pid = " + pid;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                bean= new PropertyValue();

                int id = rs.getInt("id");
                bean.setId(id);

                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                Property property = new PropertyDAO().get(ptid);
                bean.setProperty(property);

                String value = rs.getString("value");
                bean.setValue(value);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return bean;
    }

    // 查询属性值记录总数
    public int getTotal() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM propertyvalue";
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

    // 列出属性值对象
    public List<PropertyValue> list() {
        return list(0, Short.MAX_VALUE);
    }
    public List<PropertyValue> list(int start, int count) {
        List<PropertyValue> beans = new ArrayList<PropertyValue>();
        String sql = "select * from PropertyValue order by id desc limit ?,? ";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, start);
            ps.setInt(2, count);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PropertyValue bean = new PropertyValue();
                // 字段1
                int id = rs.getInt(1);
                bean.setId(id);
                // 2
                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);
                // 3
                int ptid = rs.getInt("ptid");
                Property property = new PropertyDAO().get(ptid);
                bean.setProperty(property);
                // 4
                String value = rs.getString("value");
                bean.setValue(value);

                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }
    public List<PropertyValue> list(int pid) {
        List<PropertyValue> beans = new ArrayList<PropertyValue>();
        String sql = "select * from PropertyValue where pid = ? order by ptid desc";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, pid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PropertyValue bean = new PropertyValue();

                int id = rs.getInt(1);
                bean.setId(id);

                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                int ptid = rs.getInt("ptid");
                Property property = new PropertyDAO().get(ptid);
                bean.setProperty(property);

                String value = rs.getString("value");
                bean.setValue(value);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beans;
    }

    // 设置
    // 初始化设置产品p的属性值（先占个位，值value暂时为null）
    public void init(Product p) {
        // p对应的品类有这么多种属性
        List<Property> pts = new PropertyDAO().list(p.getCategory().getId());
        for (Property pt : pts) {
            // 每种属性都应该有一个属于p的属性值
            // 如果没有则新建一个放入数据库中（占个位，值value暂时为null）
            PropertyValue pv = get(pt.getId(), p.getId());
            if(null==pv){
                pv = new PropertyValue();
                pv.setProduct(p);
                pv.setProperty(pt);
                this.add(pv);
            }
        }
    }
}


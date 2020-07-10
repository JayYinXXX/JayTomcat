package tmall.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import tmall.bean.Category;
import tmall.bean.Order;
import tmall.bean.OrderItem;
import tmall.bean.Product;
import tmall.bean.User;
import tmall.util.DBUtil;

public class OrderItemDAO {
    // 增
    public void add(OrderItem bean) {
        String sql = "insert into OrderItem values(null, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getProduct().getId());
            // 订单项在创建的时候属于购物车阶段，没有对应的订单对象，oid设置为-1
            if(null==bean.getOrder())
                ps.setInt(2, -1);
            else
                ps.setInt(2, bean.getOrder().getId());
            ps.setInt(3, bean.getUser().getId());
            ps.setInt(4, bean.getNumber());
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
            String sql = "delete from OrderItem where id = " + id;
            s.execute(sql);
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
    // 改
    public void update(OrderItem bean) {
        String sql = "update OrderItem set pid=?, oid=?, uid=?, number=? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, bean.getProduct().getId());
            if(null==bean.getOrder())
                ps.setInt(2, -1);
            else
                ps.setInt(2, bean.getOrder().getId());
            ps.setInt(3, bean.getUser().getId());
            ps.setInt(4, bean.getNumber());
            ps.setInt(5, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // 查
    public OrderItem get(int id) {
        OrderItem bean = new OrderItem();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            String sql = "select * from OrderItem where id = " + id;
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                bean.setId(id);

                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                int oid = rs.getInt("oid");
                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }

                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                bean.setUser(user);

                int number = rs.getInt("number");
                bean.setNumber(number);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return bean;
    }

    // 获取订单项总数
    public int getTotal() {
        int total = 0;
        String sql = "select count(*) from OrderItem";
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // 列出指定用户的订单项（购物车中的，还未指定订单的）
    public List<OrderItem> listByUser(int uid) {
        return listByUser(uid, 0, Short.MAX_VALUE);
    }
    public List<OrderItem> listByUser(int uid, int start, int count) {
        List<OrderItem> beans = new ArrayList<OrderItem>();
        String sql = "select * from OrderItem where uid = ? and oid=-1 order by id desc limit ?,? ";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, uid);
            ps.setInt(2, start);
            ps.setInt(3, count);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem bean = new OrderItem();
                int id = rs.getInt(1);
                bean.setId(id);

                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                int oid = rs.getInt("oid");
                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }

                User user = new UserDAO().get(uid);
                bean.setUser(user);

                int number = rs.getInt("number");
                bean.setNumber(number);

                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }
    // 列出指定订单对应的订单项
    public List<OrderItem> listByOrder(int oid) {
        return listByOrder(oid, 0, Short.MAX_VALUE);
    }
    public List<OrderItem> listByOrder(int oid, int start, int count) {
        List<OrderItem> beans = new ArrayList<OrderItem>();
        String sql = "select * from OrderItem where oid = ? order by id desc limit ?,? ";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, oid);
            ps.setInt(2, start);
            ps.setInt(3, count);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem bean = new OrderItem();

                int id = rs.getInt(1);
                bean.setId(id);

                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }

                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                bean.setUser(user);

                int number = rs.getInt("number");
                bean.setNumber(number);

                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }
    // 列出指定商品对应的订单项
    public List<OrderItem> listByProduct(int pid) {
        return listByProduct(pid, 0, Short.MAX_VALUE);
    }
    public List<OrderItem> listByProduct(int pid, int start, int count) {
        List<OrderItem> beans = new ArrayList<OrderItem>();
        String sql = "select * from OrderItem where pid = ? order by id desc limit ?,?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, pid);
            ps.setInt(2, start);
            ps.setInt(3, count);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem bean = new OrderItem();

                int id = rs.getInt(1);
                bean.setId(id);

                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                int oid = rs.getInt("oid");
                if(-1!=oid){
                    Order order= new OrderDAO().get(oid);
                    bean.setOrder(order);
                }

                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                bean.setUser(user);

                int number = rs.getInt("number");
                bean.setNumber(number);

                beans.add(bean);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return beans;
    }

    // 获取指定商品的销量
    public int getSaleCount(int pid) {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            String sql = "select sum(number) from OrderItem where pid = " + pid;
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return total;
    }

    // 设置
    // 取出数据库中的订单项数据，写入订单对象，并计算总价
    public void fill(Order o) {
        List<OrderItem> ois = listByOrder(o.getId());
        float total = 0;
        int num = 0;
        for (OrderItem oi : ois) {
            num += oi.getNumber();
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
        }
        o.setTotal(total);
        o.setTotalNumber(num);
        o.setOrderItems(ois);
    }
    public void fill(List<Order> os) {
        // 依次处理多个订单
        for (Order o : os) {
            fill(o);
        }
    }
}

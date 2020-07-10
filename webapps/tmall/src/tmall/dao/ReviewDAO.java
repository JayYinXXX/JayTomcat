package tmall.dao;

import tmall.bean.Product;
import tmall.bean.Review;
import tmall.bean.User;
import tmall.util.DBUtil;
import tmall.util.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewDAO {
    // 增
    public void add(Review bean) {
        String sql = "insert into Review values(null,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, bean.getContent());
            ps.setInt(2, bean.getUser().getId());
            ps.setInt(3, bean.getProduct().getId());
            ps.setTimestamp(4, DateUtil.d2t(bean.getCreateDate()));
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
        String sql = "delete from Review where id = " + id;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            s.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 改
    public void update(Review bean) {
        String sql = "update Review set content= ?, uid=?, pid=?, createDate = ? where id = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, bean.getContent());
            ps.setInt(2, bean.getUser().getId());
            ps.setInt(3, bean.getProduct().getId());
            ps.setTimestamp(4, DateUtil.d2t( bean.getCreateDate()) );
            ps.setInt(5, bean.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 查
    public Review get(int id) {
        String sql = "select * from Review where id = " + id;
        Review bean = new Review();
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                bean.setId(id);

                String content = rs.getString("content");
                bean.setContent(content);

                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                bean.setUser(user);

                int pid = rs.getInt("pid");
                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                bean.setCreateDate(createDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bean;
    }

    // 返回评论总数
    public int getTotal() {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            String sql = "select count(*) from Review";
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    // 指定商品的评论数
    public int getCount(int pid) {
        int total = 0;
        try (Connection c = DBUtil.getConnection(); Statement s = c.createStatement();) {
            String sql = "select count(*) from Review where pid = " + pid;
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // 列出评论
    public List<Review> list(int pid) {
        return list(pid, 0, Short.MAX_VALUE);
    }
    public List<Review> list(int pid, int start, int count) {
        List<Review> beans = new ArrayList<Review>();
        String sql = "select * from Review where pid = ? order by id desc limit ?,? ";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setInt(1, pid);
            ps.setInt(2, start);
            ps.setInt(3, count);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Review bean = new Review();

                int id = rs.getInt(1);
                bean.setId(id);

                String content = rs.getString("content");
                bean.setContent(content);

                int uid = rs.getInt("uid");
                User user = new UserDAO().get(uid);
                bean.setUser(user);

                Product product = new ProductDAO().get(pid);
                bean.setProduct(product);

                Date createDate = DateUtil.t2d(rs.getTimestamp("createDate"));
                bean.setCreateDate(createDate);

                beans.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return beans;
    }

    // 判断某商品是否存在某评论
    public boolean isExist(String content, int pid) {
        String sql = "select * from Review where content = ? and pid = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setInt(2, pid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}


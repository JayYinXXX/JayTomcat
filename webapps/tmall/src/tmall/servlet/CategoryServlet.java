package tmall.servlet;

import tmall.bean.Category;
import tmall.util.ImageUtil;
import tmall.util.Page;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理
 */
public class CategoryServlet extends BaseBackServlet{
    // 新增分类
    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        // 获取属性构造bean实体对象，加入数据库
        Map<String, String> params = new HashMap<>();
        InputStream is = super.parseUpload(request, params);
        String name = params.get("name");
        Category c = new Category();
        c.setName(name);
        categoryDAO.add(c);

        // 上传图片保存到服务器本地
        // 新建File对象
        String fileName = c.getId() + ".jpg";
        String fileFolder = request.getSession().getServletContext().getRealPath("img/category");
        File file = new File(fileFolder, fileName);
        file.getParentFile().mkdirs();  // 父文件夹不存在则创建

        // is输入流 ——> fos输出流
        try {
            if (is != null && is.available() != 0) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] b = new byte[1024 * 1024];
                    int length = 0;
                    while ((length = is.read(b)) != -1) {
                        fos.write(b, 0, length);
                    }
                    fos.flush();
                    // 将文件保存为jpg格式
                    BufferedImage img = ImageUtil.change2jpg(file);
                    ImageIO.write(img, "jpg", file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
// test
        System.out.println("0.CategoryServlet add(): add name=" + name);
        // add完之后客户端跳转到list，显示出来
        return "@admin_category_list";
    }

    // 点击删除超链发回一个参数id，根据id删除相应的分类
    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        categoryDAO.delete(id);
        String folderPath = request.getServletContext().getRealPath("img/category");
        File file = new File(folderPath, id + ".jpg");
        file.delete();
// test
        System.out.println("0.CategoryServlet delete(): delete id=" + id);
        return "@admin_category_list";
    }

    // 接收一个id，根据id获取数据，跳转到编辑页面展示相应的信息
    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        Category c = categoryDAO.get(id);
        request.setAttribute("c", c);
// test
        System.out.println("0.CategoryServlet edit(): edit id=" + id);
        return "admin/editCategory.jsp";
    }

    // 更改数据库数据
    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        // 获取数据构造bean实体对象，加入数据库
        Map<String, String> params = new HashMap<>();
        InputStream is = super.parseUpload(request, params);
        int id = Integer.parseInt(params.get("id"));
        String name = params.get("name");
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        categoryDAO.update(c);

        // 上传图片保存到服务器本地
        // 新建File对象
        String fileName = c.getId() + ".jpg";
        String fileFolder = request.getSession().getServletContext().getRealPath("img/category");
        File file = new File(fileFolder, fileName);
        file.getParentFile().mkdirs();  // 父文件夹不存在则创建

        // is输入流 ——> fos输出流
        try {
            if (is != null && is.available() != 0) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] b = new byte[1024 * 1024];
                    int length = 0;
                    while ((length = is.read(b)) != -1) {
                        fos.write(b, 0, length);
                    }
                    fos.flush();
                    // 将文件保存为jpg格式
                    BufferedImage img = ImageUtil.change2jpg(file);
                    ImageIO.write(img, "jpg", file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
// test
        System.out.println("0.CategoryServlet update(): update name=" + name);
        // add完之后客户端跳转到list，显示出来
        return "@admin_category_list";
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {

        List<Category> cs = categoryDAO.list(page.getStart(), page.getCount());
        int total = categoryDAO.getTotal();
        page.setTotal(total);

        request.setAttribute("thecs", cs);
        request.setAttribute("page", page);

// test
        System.out.println("0.CategoryServlet list()");
        return "admin/listCategory.jsp";
    }
}

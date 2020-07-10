package tmall.servlet;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.dao.ProductImageDAO;
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

public class ProductImageServlet extends BaseBackServlet {
    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        Map<String, String> params = new HashMap<>();
        InputStream is = super.parseUpload(request, params);

        int pid = Integer.parseInt(params.get("pid"));
        Product p = productDAO.get(pid);
        String type = params.get("type");

        ProductImage pi = new ProductImage();
        pi.setProduct(p);
        pi.setType(type);
        productImageDAO.add(pi);

        // 根据获取的id创建图片文件
        String fileName = pi.getId() + ".jpg";
        String fileFolder = null;
        if (type.equals(ProductImageDAO.type_single))
            fileFolder = request.getSession().getServletContext().getRealPath("img/productSingle");
        else
            fileFolder = request.getSession().getServletContext().getRealPath("img/productDetail");
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
        System.out.println("0.ProductImageServlet add(): img name: " + fileName);
        return "@admin_productImage_list?pid=" + pid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));

        ProductImage pi = productImageDAO.get(id);
        int pid = pi.getProduct().getId();
        // 删除图片
        String imageFolder = null;
        if (pi.getType().equals(ProductImageDAO.type_single))
            imageFolder = request.getServletContext().getRealPath("img/productSingle");
        else
            imageFolder = request.getServletContext().getRealPath("img/productDetail");
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        // 删除数据库记录
        productImageDAO.delete(id);

        return "@admin_productImage_list?pid=" + pid;
    }

    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int pid = Integer.parseInt(request.getParameter("pid"));

        Product p = productDAO.get(pid);
        List<ProductImage> pisSingle = productImageDAO.list(p, ProductImageDAO.type_single);
        List<ProductImage> pisDetail = productImageDAO.list(p, ProductImageDAO.type_detail);

        request.setAttribute("p", p);
        request.setAttribute("pisSingle", pisSingle);
        request.setAttribute("pisDetail", pisDetail);

// test
        System.out.println("0.ProductImageServlet list() pid=" + pid);
        return "admin/listProductImage.jsp";
    }

    // 不提供编辑功能
    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }
    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        return null;
    }

}

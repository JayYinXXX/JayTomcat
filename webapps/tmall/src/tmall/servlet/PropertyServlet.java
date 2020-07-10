package tmall.servlet;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PropertyServlet extends BaseBackServlet {
    @Override
    public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);
        String name = request.getParameter("name");

        Property p = new Property();
        p.setCategory(c);
        p.setName(name);
        propertyDAO.add(p);

// test
        System.out.println("0.PropertyServlet add(): name=" + name);
        return "@admin_property_list?cid=" + cid;
    }

    @Override
    public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));

        Property p = propertyDAO.get(id);
        int cid = p.getCategory().getId();  // 提前取出cid

        propertyDAO.delete(id);
        return "@admin_property_list?cid=" + cid;
    }

    @Override
    public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));

        Property p = propertyDAO.get(id);
        request.setAttribute("p", p);
        return "admin/editProperty.jsp";
    }

    @Override
    public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        int cid = Integer.parseInt(request.getParameter("cid"));
        Category c = categoryDAO.get(cid);

        Property p = new Property();
        p.setId(id);
        p.setName(name);
        p.setCategory(c);
        propertyDAO.update(p);

        return "@admin_property_list?cid=" + cid;
    }

    // 列出指定分类的属性，需要提供cid
    @Override
    public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
        int cid = Integer.parseInt(request.getParameter("cid"));
        // 获取需要传输给jsp的数据 c ps page
        Category c = categoryDAO.get(cid);
        List<Property> ps = propertyDAO.list(cid, page.getStart(), page.getCount());
        page.setTotal(propertyDAO.getTotal(cid));
        page.setParam("&cid=" + c.getId());

        request.setAttribute("c", c);
        request.setAttribute("ps", ps);
        request.setAttribute("page", page);
// test
        System.out.println("0.PropertyServlet list()");
        return "admin/listProperty.jsp";
    }
}

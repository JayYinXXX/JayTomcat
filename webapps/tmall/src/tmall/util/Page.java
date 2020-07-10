package tmall.util;

/**
 * 分页工具
 * 在需要分页浏览的时候，维护一个page对象，在服务端跳转的时候传递信息
 * 包含当前页面中的记录的起始索引、数量等信息
 */
public class Page {
    int start;  // 记录起始索引（从0开始），从前端获得
    int count;  // 一页显示的记录条数，从前端获得
    int total;  // 记录总数，一般在list方法中，从数据库拿出数据后马上计算
    String param;  // 保存从前端传回的额外参数

    public Page(int start, int count){
        super();  // 调用父类中的构造方法，这里没有意义
        this.start = start;
        this.count = count;
    }
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public String getParam() {
        return param;
    }
    public void setParam(String param) {
        this.param = param;
    }

    // 计算总页数
    public int getTotalPage() {
        int tp = 0;
        if (total % count == 0) {
            tp = total / count;
        }
        else {
            tp = total / count + 1;
        }
        if (tp == 0)   // 至少有1页
            tp = 1;
        return tp;
    }
    // 计算最后一页的起始记录数 01234 56789 10
    public int getLast() {
        int last = 0;
        if (total % count == 0)
            last = total - count;
        else
            last = total - total % count;
        return last<0 ? 0 : last;
    }
    // 判断是否有前页
    public boolean isHasPre() {
        if (start == 0)
            return false;
        return true;
    }
    // 判断是否有后页
    public boolean isHasNext() {
        if (start == getLast())
            return false;
        return true;
    }
}

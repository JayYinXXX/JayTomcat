package tmall.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 日期工具
 * 数据库日期类java.sql.Timestamp与java.util.Date日期类之间的转化
 */
public class DateUtil {
    public static Timestamp d2t(Date d) {
        if(d == null) {
            return null;
        }
        return new Timestamp(d.getTime());
    }

    public static Date t2d(Timestamp t) {
        if(t == null) {
            return null;
        }
        return new Date(t.getTime());
    }
}

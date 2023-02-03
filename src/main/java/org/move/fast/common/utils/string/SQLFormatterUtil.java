package org.move.fast.common.utils.string;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;

/**
 * @description: SQLFormatterUtil sql格式化工具 通过导入 druid jar包实现
 * 支持 mysql/postgresql/odps/oracle/db2/sqlserver
 * @author: YinShiJie
 * @create: 2022-02-21 11:11
 **/
public class SQLFormatterUtil {
    /**
     * @description: mySqlUpperCase 关键词大写
     * @author YinShiJie
     * @Param [sql]
     * @Return java.lang.String
     * @date 2022/2/21 11:22
     */
    public static String mySqlUpperCase(String sql) {
        //默认大写
        return SQLUtils.format(sql, JdbcConstants.MYSQL);
    }

    /**
     * @description: mySqlLowerCase 关键词小写
     * @author YinShiJie
     * @Param [sql]
     * @Return java.lang.String
     * @date 2022/2/21 11:23
     */
    public static String mySqlLowerCase(String sql) {
        return SQLUtils.format(sql, JdbcConstants.MYSQL, SQLUtils.DEFAULT_LCASE_FORMAT_OPTION);
    }

}

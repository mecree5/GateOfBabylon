package org.move.fast.config;

import org.apache.ibatis.logging.Log;
import org.move.fast.common.utils.Log.Grade;
import org.springframework.beans.BeanUtils;

/**
 * MybatisPlusLogImpl，直接使用控制台输出日志
 **/
public class MybatisPlusLogConf implements Log {

    private static final String isTraceEnabled = ReadConf.getConfValue("gateOfBabylon.log.mybatis-plus.result-print");

    /**
     * 初始化信息打印
     */
    public MybatisPlusLogConf(String clazz) {

    }

    /**
     * 是否执行下方debug方法
     */
    public boolean isDebugEnabled() {
        return true;
    }

    /**
     * 是否执行下方trace方法
     */
    public boolean isTraceEnabled() {
        return "true".equals(isTraceEnabled);
    }

    public void error(String s, Throwable e) {
        Exception exception = new Exception();
        BeanUtils.copyProperties(e, exception);
        org.move.fast.common.utils.Log.error(s, this.getClass());
        org.move.fast.common.utils.Log.error(exception);
    }

    public void error(String s) {
        org.move.fast.common.utils.Log.error(s, MybatisPlusLogConf.class);
    }

    /**
     * 执行的sql以及参数信息
     *
     * @Param s
     */
    public void debug(String s) {
        org.move.fast.common.utils.Log.infoNotWrite(s, MybatisPlusLogConf.class);
    }

    /**
     * 返回数据信息
     *
     * @Param s
     */
    public void trace(String s) {
        org.move.fast.common.utils.Log.infoNotWrite(s, MybatisPlusLogConf.class);
    }

    public void warn(String s) {
        org.move.fast.common.utils.Log.infoNotWrite(s, MybatisPlusLogConf.class);
    }

}

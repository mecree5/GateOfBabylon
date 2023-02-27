package org.move.fast.config;

import org.apache.ibatis.logging.Log;
import org.move.fast.common.utils.Log.Grade;

/**
 * MybatisPlusLogImpl，直接使用控制台输出日志
 **/
public class MybatisPlusLogImpl implements Log {

    /**
     * 初始化信息打印
     *
     * @Param clazz
     */
    public MybatisPlusLogImpl(String clazz) {

    }

    /**
     * 是否执行下方debug方法
     *
     * @Return
     */
    public boolean isDebugEnabled() {
        return true;
    }

    /**
     * 是否执行下方trace方法
     *
     * @Return
     */
    public boolean isTraceEnabled() {
        return false;
    }

    public void error(String s, Throwable e) {
        org.move.fast.common.utils.Log.printAndWrite(s, e);
    }

    public void error(String s) {
        org.move.fast.common.utils.Log.printAndWrite(Grade.ERROR, s, MybatisPlusLogImpl.class);
    }

    /**
     * 执行的sql以及参数信息
     *
     * @Param s
     */
    public void debug(String s) {
        org.move.fast.common.utils.Log.info(s, MybatisPlusLogImpl.class);
    }

    /**
     * 返回数据信息
     *
     * @Param s
     */
    public void trace(String s) {
        org.move.fast.common.utils.Log.debug(s, MybatisPlusLogImpl.class);
    }

    public void warn(String s) {
        org.move.fast.common.utils.Log.info(s, MybatisPlusLogImpl.class);
    }

}

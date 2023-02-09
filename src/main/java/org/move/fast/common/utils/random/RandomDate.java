package org.move.fast.common.utils.random;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : YinShiJie
 * @date : 2022/1/25 15:02
 */
public class RandomDate {

    public static Date randomDate(String beginDate, String endDate) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //构造日期
        Date start;
        Date end;

        try {
            start = format.parse(beginDate);
            end = format.parse(endDate);//构造结束日期
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
        if (start.getTime() >= end.getTime()) {
            return null;
        }
        long date = random(start.getTime(), end.getTime());
        return new Date(date);
    }

    public static long random(long begin, long end) {

        long rtn = begin + (long) (Math.random() * (end - begin));
        //如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }

}

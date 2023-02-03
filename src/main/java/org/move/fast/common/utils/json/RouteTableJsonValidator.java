package org.move.fast.common.utils.json;

import com.alibaba.fastjson.JSONException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @description: RouteTableJsonValidator
 * @author: YinShiJie
 * @create: 2022-03-04 15:21
 */
public class RouteTableJsonValidator {

    /**
     * 数组指针
     */
    private static int index;
    /**
     * 字符串
     */
    private static String value;
    /**
     * 指针当前字符
     */
    private static char curchar;

    /**
     * 工具类非公有构造函数
     */
    private RouteTableJsonValidator() {
    }

    /**
     * @param rawValue 字符串参数
     * @return boolean 是否是JSON
     */
    public static boolean isJSON(String rawValue) {
        index = 0;
        value = rawValue;
        switch (nextClean()) {
            case '[':
                if (nextClean() == ']') {
                    return true;
                }
                back();
                return validateArray();
            case '{':
                if (nextClean() == '}') {
                    return true;
                }
                back();
                return validateObject();
            default:
                return false;
        }
    }

    /**
     * @return char 下一个有效实义字符 char<=' ' char!=127
     * @throws JSONException 自定义JSON异常
     */
    public static char nextClean() throws JSONException {
        skipComment:
        do {
            next();
            if (curchar == '/') { // 跳过//类型与/*类型注释 遇回车或者null为注释内容结束
                switch (next()) {
                    case 47: // '/'
                        do {
                            curchar = next();
                        } while (curchar != '\n' && curchar != '\r' && curchar != 0);
                        continue;
                    case 42: // '*'
                        do {
                            do {
                                next();
                                if (curchar == 0) {
                                    throw syntaxError("Unclosed comment");
                                }
                            } while (curchar != '*');
                            if (next() == '/') {
                                continue skipComment;
                            }
                            back();
                        } while (true);
                }
                back();
                return '/';
            }
            if (curchar != '#') { //跳过#类型注释 遇回车或者null为注释内容结束
                break;
            }
            do {
                next();
            } while (curchar != '\n' && curchar != '\r' && curchar != 0);
        } while (true);
        if (curchar != 0 && (curchar <= ' ' || curchar == 127)) {
            throw syntaxError("JSON can not contain control character!");
        }
        return curchar;
    }

    /**
     * @return char 下一个字符
     */
    public static char next() {
        if (index < 0 || index >= value.length()) {
            return '\0';
        }
        curchar = value.charAt(index);
        if (curchar <= 0) {
            return '\0';
        } else {
            index++;
            return curchar;
        }
    }

    /**
     * 将指针移至上一个字符，回退一位
     */
    public static void back() { //异常在next中进行返回null
        index--;
    }

    /**
     * @param message 异常自定义信息
     * @return JSONException 自定义JSON异常
     */
    public static JSONException syntaxError(String message) {
        return new JSONException(String.valueOf(message));
    }

    /**
     * @return boolean 是否是JSONArray
     * @throws JSONException 自定义JSON异常
     */
    public static boolean validateArray() throws JSONException {
        do {
            //入口为合法 [ array 起点
            nextClean(); //下一位有效字符，跳过注释
            if (curchar == ']') { //空array 直接闭合返回
                return true;
            } else if (curchar == ',') { //null
                continue;
            } else if (curchar == '"') { //String
                validateString();
            } else if (curchar == '-' || (curchar >= 48 && curchar <= 57)) { // number
                validateNumber();
            } else if (curchar == '{') { // object
                if (!validateObject()) { //递归校验
                    return false;
                }
            } else if (curchar == '[') { // array
                if (!validateArray()) { //递归校验
                    return false;
                }
            } else if (curchar == 't' || curchar == 'f' || curchar == 'n') { // boolean and JSONNull
                validateBooleanAndNull();
            } else {
                return false;
            }
            switch (nextClean()) {
                case ',':
                    continue;
                case ']':
                    return true;
                default:
                    return false;
            }
        } while (true);
    }

    /**
     * @return boolean 是否是JSONObject
     * @throws JSONException 自定义JSON异常
     */
    public static boolean validateObject() throws JSONException {
        do {
            nextClean();
            if (curchar == '}') {
                return true;
            } else if (curchar == '"') { //String
                validateString();
            } else {
                return false;
            }
            if (nextClean() != ':') {
                return false;
            }
            nextClean();
            if (curchar == ',') { //null
                throw syntaxError("Missing value");
            } else if (curchar == '"') { //String
                validateString();
            } else if (curchar == '-' || (curchar >= 48 && curchar <= 57)) { // number
                validateNumber();
            } else if (curchar == '{') { // object
                if (!validateObject()) {
                    return false;
                }
            } else if (curchar == '[') { // array
                if (!validateArray()) {
                    return false;
                }
            } else if (curchar == 't' || curchar == 'f' || curchar == 'n') { // boolean and JSONNull
                validateBooleanAndNull();
            } else {
                return false;
            }
            switch (nextClean()) {
                case ',':
                    continue;
                case '}':
                    return true;
                default:
                    return false;
            }
        } while (true);
    }

    /**
     * @throws JSONException 自定义JSON异常
     */
    public static void validateString() throws JSONException {
        StringBuilder sb = new StringBuilder();
        do {
            curchar = next(); //JSON对字符串中的转义项有严格规定
            sb.append(curchar);
            if (curchar == '\\') {
                if ("\"\\/bfnrtu".indexOf(next()) < 0) {
                    throw syntaxError("Invalid escape string");
                }
                if (curchar == 'u') { //校验unicode格式 后跟4位16进制 0-9 a-f A-F
                    for (int i = 0; i < 4; i++) {
                        next();
                        if (curchar < 48 || (curchar > 57 && curchar < 65) || (curchar > 70 && curchar < 97)
                                || curchar > 102) {
                            throw syntaxError("Invalid hexadecimal digits");
                        }
                    }
                }
            }
        } while (curchar >= ' ' && "\":{[,#/".indexOf(curchar) < 0 && curchar != 127);
        if (curchar == 0) { //仅正常闭合双引号可通过
            throw syntaxError("Unclosed quot");
        } else if (curchar != '"') {
            throw syntaxError("Invalid string {\"" + sb + "}, missing quot ");
        } else if (value.charAt(index) == '"') {
            throw syntaxError("Missing comma after string: \"" + sb);
        } else if (value.charAt(index) == ':') {
            String str = sb.substring(0, sb.length() - 1);
//                if (!validateRouteTableKey(sb.charAt(0), str)) {
//                    throw syntaxError("Invalid RouteTable KEY:\"" + sb);
//                }
            validateRouteTableValue(str);
        }
    }

    /**
     * @throws JSONException 自定义JSON异常
     */
    public static void validateNumber() throws JSONException {
        StringBuilder sb = new StringBuilder();
        if (curchar == '-') { //可选负号
            curchar = next();
        }
        if (curchar > 48 && curchar <= 57) { //整数部分
            do {
                sb.append(curchar);
                curchar = next();
            } while (curchar >= 48 && curchar <= 57);
        } else if (curchar == 48) {
            curchar = next();
        } else {
            throw syntaxError("Invalid number");
        }
        if (curchar == '.') { //小数部分
            do { //.后可不跟数字 如 5. 为合法数字
                curchar = next();
            } while (curchar >= 48 && curchar <= 57);
        }
        if (curchar == 'e' || curchar == 'E') { //科学计数部分
            curchar = next();
            if (curchar == '+' || curchar == '-') {
                curchar = next();
            }
            if (curchar < 48 || curchar > 57) {
                throw syntaxError("Invalid number");
            }
            do {
                curchar = next();
            } while (curchar >= 48 && curchar <= 57);
        }
        if (curchar == '"') {
            throw syntaxError("Missing comma after number: " + sb);
        }
        back(); //指针移至数字值最后一位，取下一位即判断是,或者],或者是合法注释
    }

    public static void validateRouteTableValue(String key) throws JSONException {
        int a = index;
        char c, d;
        List<String> num_list = Collections.singletonList("port");
        List<String> boolean_list = Arrays.asList("useSSL", "default_allow");
        do {
            ++a;
            c = value.charAt(a);
        } while (c == ' ' || c == '"');

        StringBuilder sb = new StringBuilder();
        do {
            d = value.charAt(a);
            sb.append(d);
            a++;
        } while (d != ' ' && ",]}\"".indexOf(d) < 0);
        String str = sb.substring(0, sb.length() - 1);

        if (num_list.contains(key) && !(c == '-' || (c >= 48 && c <= 57))) {
            throw syntaxError("RouteTable KEY:" + key + " match NumberType");
        }
        if (boolean_list.contains(key) && !(c == 't' || c == 'f' || c == 'n')) {
            throw syntaxError("RouteTable KEY:" + key + " match BooleanType");
        }
        String port_reg = "^([0-5]?\\d{0,4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
        if ("port".equals(key) && !str.matches(port_reg)) {
            throw syntaxError("Invalid Port : " + str);
        }
        String ip_reg = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
        if ("ip".equals(key) && !str.matches(ip_reg)) {
            throw syntaxError("Invalid ip : " + str);
        }
    }

    public static boolean validateRouteTableKey(char firstChar, String str) throws JSONException {
        if ("".equals(str)) return false;
        List<String> a_list = Collections.singletonList("allow");
        List<String> c_list = Arrays.asList("certChainFile", "caFile", "coordinator");
        List<String> d_list = Arrays.asList("default", "default_allow", "deny");
        List<String> f_list = Collections.singletonList("from");
        List<String> h_list = Collections.singletonList("host");
        List<String> i_list = Collections.singletonList("ip");
        List<String> n_list = Arrays.asList("negotiationType", "caFile");
        List<String> p_list = Arrays.asList("permission", "port", "privateKeyFile");
        List<String> r_list = Arrays.asList("route_table", "role");
        List<String> s_list = Collections.singletonList("serving");
        List<String> t_list = Collections.singletonList("to");
        List<String> u_list = Collections.singletonList("useSSL");
        switch (firstChar) {
            case ' ':
                return false;
            case 'a':
                return a_list.contains(str);
            case 'f':
                return f_list.contains(str);
            case 't':
                return t_list.contains(str);
            case 'i':
                return i_list.contains(str);
            case 'h':
                return h_list.contains(str);
            case 's':
                return s_list.contains(str);
            case 'u':
                return u_list.contains(str);
            case 'c':
                return c_list.contains(str);
            case 'n':
                return n_list.contains(str);
            case 'r':
                return r_list.contains(str);
            case 'd':
                return d_list.contains(str);
            case 'p':
                return p_list.contains(str);
            default:
                return true;
        }

    }

    /**
     * @throws JSONException 自定义JSON异常
     */
    public static void validateBooleanAndNull() throws JSONException {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(curchar);
            curchar = next();
        } while (curchar >= ' ' && "\",]#/}".indexOf(curchar) < 0 && curchar != 127);
        if (!"null".equals(sb.toString()) && !"true".equals(sb.toString()) && !"false".equals(sb.toString())) {
            throw syntaxError("Boolean/null spelling errors : " + sb);
        }
        if (curchar == '"') {
            throw syntaxError("Missing comma after Boolean: " + sb);
        }
        back();
    }

}
package com.joyveb.tlol.pay.util;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {


    public static String replaceChar(int istr, String strchar) {
        return String.valueOf(istr).replaceAll(strchar, "x");
    }

    /**
     * check String is null or not;
     * 
     * @param checkStr
     * @return boolean
     */
    public static boolean isNull(String checkStr) {
        return checkStr == null || checkStr.trim().length() == 0 || checkStr.trim().equalsIgnoreCase("null");
    }

    public static boolean isNotNull(String checkStr) {
        return !isNull(checkStr);
    }

    /**
     * if input string is null return "".
     * 
     * @param input
     * @return
     */
    public static String formatNullString(String input) {
        if (isNull(input)) {
            return "";
        }
        return input;
    }

    /**
     * parse String to int
     * 
     * @param intStr
     * @param defaultInt
     * @return int
     */
    public static int parseInt(String intStr, int defaultInt) {
        try {
            return Integer.parseInt(intStr);
        } catch (Exception e) {
            return defaultInt;
        }
    }

    /**
     * parse String to int default is 0
     * 
     * @param intStr
     * @return int
     */
    public static int parseInt(String intStr) {
        return parseInt(intStr, 0);
    }
    
    /**
     * parse String to long
     * 
     * @param longStr
     * @param defaultLong
     * @return long
     */
    public static long parseLong(String longStr, long defaultLong) {
        try {
            return Long.parseLong(longStr);
        } catch (Exception e) {
            return defaultLong;
        }
    }

    /**
     * parse String to long default is 0
     * 
     * @param longStr
     * @return long
     */
    public static long parseLong(String longStr) {
        return parseLong(longStr, 0);
    }

    /**
     * check String is int
     * 
     * @param str
     * @return boolean
     */
    public static boolean isInt(String str) {
        Pattern pattern = Pattern.compile("(0|[1-9][0-9]*|-[1-9][0-9]*)");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * get substring term from src
     * 
     * @param src
     * @param term
     * @return String
     */
    public static String subBefore(String src, String term) {
        if (src == null || term == null) {
            return null;
        } else {
            int index = src.indexOf(term);
            return index >= 0 ? src.substring(0, index) : src;
        }
    }

    /**
     * get substring after term from src
     * 
     * @param src
     * @param term
     * @return string
     */
    public static String subAfter(String src, String term) {
        if (src == null || term == null) {
            return null;
        } else {
            int index = src.indexOf(term);
            return index >= 0 ? src.substring(index + term.length()) : src;
        }
    }

    /**
     * get substring last before term from src
     * 
     * @param src
     * @param term
     * @return string
     */
    public static String subLastBefore(String src, String term) {
        if (src == null || term == null) {
            return null;
        } else {
            int index = src.lastIndexOf(term);
            return index >= 0 ? src.substring(0, index) : src;
        }
    }

    /**
     * get substring last after term from src
     * 
     * @param src
     * @param term
     * @return string
     */
    public static String subLastAfter(String src, String term) {
        if (src == null || term == null) {
            return null;
        } else {
            int index = src.lastIndexOf(term);
            return index >= 0 ? src.substring(index + term.length()) : src;
        }
    }

    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(0, len);
        }
    }

    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(str.length() - len);
        }
    }

    /**
     * encode url
     * 
     * @param src
     * @return String
     */
    public static String encodeURL(String src) {
        String result = null;
        try {
            if (!StringUtils.isNull(src)) {
                result = URLEncoder.encode(src, "UTF-8");
            } else {
                result = src;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String encodeURL(String src,String charset) {
        String result = null;
        try {
            if (!StringUtils.isNull(src)) {
                result = URLEncoder.encode(src, charset);
            } else {
                result = src;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * decode url
     * 
     * @param src
     * @return String
     */
    public static String decodeURL(String src) {
        String result = null;
        try {
            if (!StringUtils.isNull(src)) {
                result = URLDecoder.decode(src, "UTF-8");
            } else {
                result = src;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String decodeURL(String src,String charset) {
        String result = null;
        try {
            if (!StringUtils.isNull(src)) {
                result = URLDecoder.decode(src, charset);
            } else {
                result = src;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 
     * @param input
     * @return
     */
    public static String formatXML(String input) {
        if (input == null) {
            return null;
        }
        String res = input;
        res = res.replaceAll("&", "&amp;");
        res = res.replaceAll("<", "&lt;");
        res = res.replaceAll(">", "&gt;");
        res = res.replaceAll("'", "&apos;");
        res = res.replaceAll("\"", "&quot;");
        res = res.replaceAll("\n", "<br/>");
        res = res.replaceAll("\r", "");
        res = res.replaceAll(" ", "&nbsp;");
        return res;
    }

    public static String formatUploadXML(String input) {
        if (input == null) {
            return null;
        }
        String res = input;
        res = res.replaceAll("&amp;", "&");
        res = res.replaceAll("&lt;", "<");
        res = res.replaceAll("&gt;", ">");
        res = res.replaceAll("&apos;", "'");

        res = res.replaceAll("&", "&amp;");
        res = res.replaceAll("<", "&lt;");
        res = res.replaceAll(">", "&gt;");
        res = res.replaceAll("'", "&apos;");
        return res;
    }

    public static String formatAndChar(String input) {
        if (input == null) {
            return null;
        }
        String res = input;
        res = res.replaceAll("&amp;", "&");
        return res;
    }

    public static String formatAndToAmpChar(String input) {
        if (input == null) {
            return null;
        }
        String res = input;
        res = res.replaceAll("&", "&amp;");
        return res;
    }

    /**
     * oracle ��ҳ��ѯ
     * 
     * @param sql
     * @param hasOffset
     * @return
     */
    public static String getLimitString(String sql, boolean hasOffset) {
        StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
        } else {
            pagingSelect.append(" ) where rownum <= ?");
        }
        return pagingSelect.toString();
    }

    public static String getCurDay() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(d);
        return s;
    }

    /**
     * 
     * @return String
     */
    public static String format() {
        return format("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 
     * @param pattern
     * @return string
     */
    public static String format(String pattern) {
        return format(pattern, new Date());
    }

    /**
     * default pattern is yyyy-MM-dd HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format("yyyy-MM-dd HH:mm:ss", date);
    }

    /**
     * 
     * @param pattern
     * @param date
     * @return string
     */
    public static String format(String pattern, Date date) {
        if (date == null) {
            return null;
        } else {
            return new SimpleDateFormat(pattern).format(date);
        }
    }

    /**
     * 
     * @param pattern
     * @param locale
     * @param text
     * @return
     */
    public static Date parse(String pattern, Locale locale, String text) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        try {
            if (text != null && text.trim().length() != 0) {
                return dateFormat.parse(text);
            } else {
                return new Date();
            }
        } catch (ParseException ex) {
            return new Date();
        }
    }

    /**
     * 
     * @param pattern
     * @param text
     * @return Date
     */
    public static Date parse(String pattern, String text) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            if (text != null && text.trim().length() != 0) {
                return dateFormat.parse(text);
            } else {
                return new Date();
            }
        } catch (ParseException ex) {
            return new Date();
        }
    }

    /**
     * pattern is yyyy-MM-dd HH:mm:ss
     * 
     * @param text
     * @return Date
     */
    public static Date parse(String text) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (text != null && text.trim().length() != 0) {
                return dateFormat.parse(text);
            } else {
                return new Date();
            }
        } catch (ParseException ex) {
            return new Date();
        }
    }
    
    public static String formatYYMMDD(Date date) {
        return format("yyyy-MM-dd", date);
    }
    
    public static int reverseBytes(int i) {
        return ((i >>> 24)           ) |
               ((i >>   8) &   0xFF00) |
               ((i <<   8) & 0xFF0000) |
               ((i << 24));
    }
	
}
	


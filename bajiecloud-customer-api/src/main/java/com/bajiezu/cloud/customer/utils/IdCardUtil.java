package com.bajiezu.cloud.customer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class IdCardUtil {

    // 18位身份证正则
    private static final Pattern IDCARD_18 = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$");
    // 15位身份证正则
    private static final Pattern IDCARD_15 = Pattern.compile("^[1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$");

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    static {
        sdf.setLenient(false); // 严格解析日期
    }

    /**
     * 从身份证号获取年龄（周岁）
     */
    public static Integer getAgeByIdCard(String idCard) {
        if (idCard == null || idCard.isBlank()) {
            return null;
        }
        String birthStr = getBirthDateStr(idCard);
        if (birthStr == null) {
            return null;
        }
        try {
            Date birthDate = sdf.parse(birthStr);
            return calculateAge(birthDate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 从身份证号获取出生日期字符串 yyyyMMdd
     */
    public static String getBirthDateStr(String idCard) {
        if (idCard == null) return null;

        if (idCard.length() == 18) {
            if (IDCARD_18.matcher(idCard).matches()) {
                return idCard.substring(6, 14);
            }
        } else if (idCard.length() == 15) {
            if (IDCARD_15.matcher(idCard).matches()) {
                return "19" + idCard.substring(6, 12); // 15位默认补19
            }
        }
        return null;
    }

    /**
     * 根据出生日期计算周岁
     */
    public static int calculateAge(Date birthDate) {
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar now = Calendar.getInstance();

        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        // 未过生日则减1
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
}

package com.bajiezu.cloud.customer.utils;

import com.alibaba.nacos.common.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MobileUtils {

    private MobileUtils() {}

    public static List<String> encryptMobile(List<String> mobiles) {
        if (CollectionUtils.isEmpty(mobiles))
            return mobiles;

        for (int i = 0; i < mobiles.size(); i++) {
            String mobile = mobiles.get(i);
            if (mobile.length() < 4) {
                continue;
            }
            char[] chars = mobile.toCharArray();
            for (int j = 0; j < 4; j++) {
                if (chars.length > j + 3) {
                    chars[j + 3] = '*';
                }
            }
            mobile = new String(chars);
            mobiles.set(i, mobile);
        }
        return mobiles;
    }

    public static String encryptMobile(String mobile) {
        if (StringUtils.isEmpty(mobile))
            return mobile;

        if (mobile.length() < 4) {
            return mobile;
        }
        char[] chars = mobile.toCharArray();
        for (int j = 0; j < 4; j++) {
            if (chars.length > j + 3) {
                chars[j + 3] = '*';
            }
        }
        mobile = new String(chars);
        return mobile;
    }

}

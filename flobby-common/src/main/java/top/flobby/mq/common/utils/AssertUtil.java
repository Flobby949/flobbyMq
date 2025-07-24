package top.flobby.mq.common.utils;

import java.util.List;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 断言工具
 * @create : 2025-05-07 10:43
 **/

public class AssertUtil {

    public static void isTrue(Boolean condition, String msg) {
        if (!condition) {
            throw new RuntimeException(msg);
        }
    }

    public static void isNotNull(Object val, String msg) {
        if (val == null) {
            throw new RuntimeException(msg);
        }
    }

    public static void isNotBlank(String val, String msg) {
        if (val == null || val.trim().isEmpty()) {
            throw new RuntimeException(msg);
        }
    }

    public static void isNotEmpty(List val, String msg) {
        if (val == null || val.isEmpty()) {
            throw new RuntimeException(msg);
        }
    }
}

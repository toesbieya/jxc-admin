package cn.toesbieya.jxc.common.util;

import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {
    public static String UUID() {
        return UUID.randomUUID().toString();
    }

    public static List<Integer> str2List(String string) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyList();
        }
        return Arrays.stream(string.split(","))
                .map(i -> Integer.parseInt(i.trim()))
                .collect(Collectors.toList());
    }

    public static String str2Hex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
        }
        return new String(baKeyword, StandardCharsets.ISO_8859_1);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String exception2Str(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    public static <T> T find(Iterable<T> list, Predicate<T> predicate) {
        for (T t : list) {
            if (predicate.test(t)) return t;
        }
        return null;
    }

    public static <T> int findIndex(Iterable<T> list, Predicate<T> predicate) {
        int i = -1;
        if (list == null) return i;
        for (T t : list) {
            i++;
            if (predicate.test(t)) return i;
        }
        return -1;
    }

    public static <T> boolean some(Iterable<T> list, Predicate<T> predicate) {
        int index = findIndex(list, predicate);
        return index > -1;
    }
}

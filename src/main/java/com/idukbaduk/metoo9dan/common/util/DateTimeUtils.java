package com.idukbaduk.metoo9dan.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// LocalDateTime 값을 원하는 형식의 문자열로 포맷팅하기 위해 문자열 변환
public class DateTimeUtils {
    public static String formatLocalDateTime(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }
}

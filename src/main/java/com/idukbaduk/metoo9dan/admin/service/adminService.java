package com.idukbaduk.metoo9dan.admin.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class adminService {

    public class DateTimeUtils {
        public static String formatLocalDateTime(LocalDateTime localDateTime, String format) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return localDateTime.format(formatter);
        }
    }

}

package br.edu.utfpr.emarketplace.service.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static br.edu.utfpr.emarketplace.service.utils.StringUtils.isNullOrEmpty;

public class DateUtils {
    private DateUtils() {
    }

    public static LocalDate converterLocalDateByString(String date) {
        if (isNullOrEmpty(date)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }
}

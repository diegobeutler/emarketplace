package br.edu.utfpr.emarketplace.service.utils;


import java.util.Collection;

public class ListUtils {
    private ListUtils() {
    }

    public static boolean isNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}

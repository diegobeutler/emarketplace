package br.edu.utfpr.emarketplace.service.utils;


import java.util.Collection;

public class ListUtils {
    public static boolean isNullOrEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}

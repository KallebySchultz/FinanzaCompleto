package com.finanza.desktop.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utilitários para formatação de valores
 */
public class FormatUtils {
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static String formatarMoeda(double valor) {
        return CURRENCY_FORMAT.format(valor);
    }

    public static String formatarData(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatarDataHora(long timestamp) {
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatarValorSemSimbolo(double valor) {
        return String.format("%.2f", valor);
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static double parseDouble(String value) {
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
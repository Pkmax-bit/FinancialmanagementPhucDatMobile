package com.example.financialmanagement.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Currency Formatter - Utility class để format tiền tệ
 * Hỗ trợ format tiền Việt Nam và các loại tiền tệ khác
 */
public class CurrencyFormatter {
    
    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    private static final String VND_SYMBOL = "₫";
    private static final String USD_SYMBOL = "$";
    private static final String EUR_SYMBOL = "€";
    
    /**
     * Format số tiền theo định dạng VND
     * @param amount Số tiền
     * @return Chuỗi đã format
     */
    public static String format(double amount) {
        return format(amount, "VND");
    }
    
    /**
     * Format số tiền theo loại tiền tệ
     * @param amount Số tiền
     * @param currency Loại tiền tệ (VND, USD, EUR)
     * @return Chuỗi đã format
     */
    public static String format(double amount, String currency) {
        if (currency == null || currency.isEmpty()) {
            currency = "VND";
        }
        
        switch (currency.toUpperCase()) {
            case "VND":
                return formatVND(amount);
            case "USD":
                return formatUSD(amount);
            case "EUR":
                return formatEUR(amount);
            default:
                return formatVND(amount);
        }
    }
    
    /**
     * Format VND
     */
    private static String formatVND(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(VIETNAM_LOCALE);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount) + " " + VND_SYMBOL;
    }
    
    /**
     * Format USD
     */
    private static String formatUSD(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount);
    }
    
    /**
     * Format EUR
     */
    private static String formatEUR(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        return formatter.format(amount);
    }
    
    /**
     * Format số tiền với ký hiệu tùy chỉnh
     * @param amount Số tiền
     * @param symbol Ký hiệu tiền tệ
     * @return Chuỗi đã format
     */
    public static String formatWithSymbol(double amount, String symbol) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " " + symbol;
    }
    
    /**
     * Format số tiền đơn giản (chỉ số)
     * @param amount Số tiền
     * @return Chuỗi đã format
     */
    public static String formatSimple(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }
    
    /**
     * Format số tiền với đơn vị
     * @param amount Số tiền
     * @param unit Đơn vị (triệu, tỷ, nghìn)
     * @return Chuỗi đã format
     */
    public static String formatWithUnit(double amount, String unit) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " " + unit;
    }
    
    /**
     * Format số tiền theo đơn vị Việt Nam
     * @param amount Số tiền (đơn vị VND)
     * @return Chuỗi đã format
     */
    public static String formatVietnamese(double amount) {
        if (amount >= 1_000_000_000) {
            // Tỷ
            double billions = amount / 1_000_000_000;
            return String.format("%.1f tỷ", billions);
        } else if (amount >= 1_000_000) {
            // Triệu
            double millions = amount / 1_000_000;
            return String.format("%.1f triệu", millions);
        } else if (amount >= 1_000) {
            // Nghìn
            double thousands = amount / 1_000;
            return String.format("%.1f nghìn", thousands);
        } else {
            return formatVND(amount);
        }
    }
    
    /**
     * Format số tiền ngắn gọn
     * @param amount Số tiền (đơn vị VND)
     * @return Chuỗi đã format ngắn gọn
     */
    public static String formatShort(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1f tỷ", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.1f tr", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.0fk", amount / 1_000);
        } else if (amount == 0) {
            return "0 ₫";
        } else {
            return formatVND(amount);
        }
    }
    
    /**
     * Parse chuỗi tiền tệ thành số
     * @param currencyString Chuỗi tiền tệ
     * @return Số tiền
     */
    public static double parse(String currencyString) {
        if (currencyString == null || currencyString.isEmpty()) {
            return 0.0;
        }
        
        // Loại bỏ các ký tự không phải số
        String cleanString = currencyString.replaceAll("[^0-9.,]", "");
        
        // Thay thế dấu phẩy bằng dấu chấm
        cleanString = cleanString.replace(",", ".");
        
        try {
            return Double.parseDouble(cleanString);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

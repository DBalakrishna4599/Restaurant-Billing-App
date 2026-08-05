package com.hackathon.billing.util;

import com.hackathon.billing.exception.BillingValidationException;

public class ConsoleTablePrinter {
    public String repeat(char character, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(count);
        for (int index = 0; index < count; index++) {
            builder.append(character);
        }
        return builder.toString();
    }

    public String padRight(String text, int width) {
        String value = text == null ? "" : text;
        if (width <= value.length()) {
            return truncate(value, width);
        }
        return value + repeat(' ', width - value.length());
    }

    public String padLeft(String text, int width) {
        String value = text == null ? "" : text;
        if (width <= value.length()) {
            return truncate(value, width);
        }
        return repeat(' ', width - value.length()) + value;
    }

    public String center(String text, int width) {
        String value = text == null ? "" : text;
        if (width <= value.length()) {
            return truncate(value, width);
        }
        int padding = width - value.length();
        int leftPadding = padding / 2;
        int rightPadding = padding - leftPadding;
        return repeat(' ', leftPadding) + value + repeat(' ', rightPadding);
    }

    public String columns(String[] values, int[] widths, String separator) {
        if (values.length != widths.length) {
            throw new BillingValidationException("Values and widths must have the same length.");
        }
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append(separator);
            }
            builder.append(padRight(values[index], widths[index]));
        }
        return builder.toString();
    }

    private String truncate(String value, int width) {
        if (width <= 0) {
            return "";
        }
        if (value.length() <= width) {
            return value;
        }
        if (width <= 3) {
            return value.substring(0, width);
        }
        return value.substring(0, width - 3) + "...";
    }
}

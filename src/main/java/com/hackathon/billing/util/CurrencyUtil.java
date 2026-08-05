package com.hackathon.billing.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CurrencyUtil {
    public static final int SCALE = 2;

    private CurrencyUtil() {
    }

    public static BigDecimal zero() {
        return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal scale(BigDecimal value) {
        if (value == null) {
            return zero();
        }
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateAmount(BigDecimal base, BigDecimal rate) {
        if (base == null || rate == null) {
            return zero();
        }
        return scale(scale(base).multiply(rate));
    }

    public static long toPaisa(BigDecimal amount) {
        return scale(amount).movePointRight(SCALE).longValueExact();
    }

    public static BigDecimal fromPaisa(long paisa) {
        return BigDecimal.valueOf(paisa, SCALE);
    }

    public static String format(BigDecimal amount) {
        return scale(amount).toPlainString();
    }
}

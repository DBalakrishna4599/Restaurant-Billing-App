package com.hackathon.billing.util;

import java.math.BigDecimal;
import java.util.Scanner;

public final class InputUtil {
    private InputUtil() {
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    public static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return new BigDecimal(line);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }

    public static String readString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            String value = readString(scanner, prompt).trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Please enter a non-empty value.");
        }
    }
}

package com.hackathon.billing.service;

import com.hackathon.billing.exception.BillingValidationException;
import com.hackathon.billing.model.Modifier;
import com.hackathon.billing.model.Order;
import com.hackathon.billing.model.OrderItem;
import com.hackathon.billing.util.ConsoleTablePrinter;
import com.hackathon.billing.util.CurrencyUtil;

import java.math.BigDecimal;

public class ReceiptService {
    private static final int RECEIPT_WIDTH = 72;
    private final BillingService billingService = new BillingService();
    private final ConsoleTablePrinter printer = new ConsoleTablePrinter();

    public String generateReceipt(Order order) {
        if (order == null) {
            throw new BillingValidationException("Order cannot be null.");
        }
        order.validateState();

        StringBuilder builder = new StringBuilder();
        builder.append(printer.center("RESTAURANT BILLING RECEIPT", RECEIPT_WIDTH)).append(System.lineSeparator());
        builder.append(printer.center("Order ID: " + order.getOrderId(), RECEIPT_WIDTH)).append(System.lineSeparator());
        builder.append(printer.center("Tax Mode: " + order.getTaxMode().getDisplayName(), RECEIPT_WIDTH)).append(System.lineSeparator());
        builder.append(printer.center("Service Charge Rate: " + formatPercent(order.getServiceChargeRate()), RECEIPT_WIDTH)).append(System.lineSeparator());
        builder.append(printer.repeat('-', RECEIPT_WIDTH)).append(System.lineSeparator());
        builder.append(printer.columns(
                new String[]{"Item", "Qty", "Unit", "Subtot.", "Tax", "Total"},
            new int[]{24, 4, 10, 10, 9, 10},
                " "
        )).append(System.lineSeparator());
        builder.append(printer.repeat('-', RECEIPT_WIDTH)).append(System.lineSeparator());

        for (OrderItem item : order.getItems()) {
            appendItemRow(builder, item);
        }

        builder.append(printer.repeat('-', RECEIPT_WIDTH)).append(System.lineSeparator());
        builder.append(summaryRow("Subtotal", billingService.calculateSubtotal(order))).append(System.lineSeparator());
        builder.append(summaryRow("Tax", billingService.calculateTax(order))).append(System.lineSeparator());
        builder.append(summaryRow("Service Charge", billingService.calculateServiceCharge(order))).append(System.lineSeparator());
        builder.append(summaryRow("Grand Total", billingService.calculateGrandTotal(order))).append(System.lineSeparator());
        builder.append(printer.repeat('-', RECEIPT_WIDTH)).append(System.lineSeparator());
        return builder.toString();
    }

    private void appendItemRow(StringBuilder builder, OrderItem item) {
        BigDecimal subtotal = item.isActive() ? item.getLineSubtotal() : CurrencyUtil.zero();
        BigDecimal tax = item.isActive() ? item.getTaxAmount() : CurrencyUtil.zero();
        BigDecimal total = item.isActive() ? item.getLineTotal() : CurrencyUtil.zero();
        BigDecimal unitPrice = item.isActive() ? item.getUnitPriceWithModifiers() : CurrencyUtil.zero();
        String itemName = item.getItemName();
        if (item.isVoided()) {
            itemName = itemName + " [VOIDED]";
        }

        builder.append(printer.columns(
            new String[]{itemName, String.valueOf(item.getQuantity()), CurrencyUtil.format(unitPrice), CurrencyUtil.format(subtotal), CurrencyUtil.format(tax), CurrencyUtil.format(total)},
            new int[]{24, 4, 10, 10, 9, 10},
                " "
        )).append(System.lineSeparator());

        if (item.isActive()) {
            for (Modifier modifier : item.getModifiers()) {
                BigDecimal modifierImpact = CurrencyUtil.scale(modifier.getEffectivePriceDelta().multiply(BigDecimal.valueOf(item.getQuantity())));
                builder.append(printer.columns(
                new String[]{"  - " + modifier.getName(), "x" + item.getQuantity(), CurrencyUtil.format(modifierImpact)},
                new int[]{28, 8, 12},
                        " "
                )).append(System.lineSeparator());
            }
        }
    }

    private String summaryRow(String label, BigDecimal amount) {
        return printer.columns(
                new String[]{label, CurrencyUtil.format(amount)},
                new int[]{24, 12},
                " "
        );
    }

    private String formatPercent(BigDecimal rate) {
        return CurrencyUtil.scale(rate.multiply(BigDecimal.valueOf(100))).toPlainString() + "%";
    }
}

package com.hackathon.billing.app;

import com.hackathon.billing.data.SampleData;
import com.hackathon.billing.model.MenuItem;
import com.hackathon.billing.model.Modifier;
import com.hackathon.billing.model.Order;
import com.hackathon.billing.model.OrderItem;
import com.hackathon.billing.model.TaxMode;
import com.hackathon.billing.service.BillingService;
import com.hackathon.billing.service.ReceiptService;
import com.hackathon.billing.service.SplitService;
import com.hackathon.billing.util.ConsoleTablePrinter;
import com.hackathon.billing.util.CurrencyUtil;
import com.hackathon.billing.util.InputUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class RestaurantBillingApp {
    private final BillingService billingService = new BillingService();
    private final SplitService splitService = new SplitService();
    private final ReceiptService receiptService = new ReceiptService();
    private final ConsoleTablePrinter printer = new ConsoleTablePrinter();
    private final Map<Integer, MenuItem> sampleMenu = new LinkedHashMap<>(SampleData.getSampleMenu());
    private final Map<Integer, Modifier> sampleModifiers = new LinkedHashMap<>(SampleData.getSampleModifiers());
    private final Scanner scanner = new Scanner(System.in);

    private Order currentOrder = new Order("ORD-1000", new BigDecimal("0.10"), TaxMode.BEFORE_SERVICE_CHARGE);

    public static void main(String[] args) {
        new RestaurantBillingApp().run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = InputUtil.readInt(scanner, "Choose an option: ");
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> showSampleMenu();
                    case 2 -> addItemToOrder();
                    case 3 -> addModifierToExistingItem();
                    case 4 -> voidItem();
                    case 5 -> setServiceCharge();
                    case 6 -> setTaxMode();
                    case 7 -> viewBillSummary();
                    case 8 -> splitBillEvenly();
                    case 9 -> splitBillByItem();
                    case 10 -> printFinalReceipt();
                    case 11 -> loadSampleScenario();
                    case 12 -> running = false;
                    default -> System.out.println("Please choose a valid option.");
                }
            } catch (Exception exception) {
                System.out.println("Action failed: " + exception.getMessage());
            }
            System.out.println();
        }
        System.out.println("Exiting Restaurant Billing App.");
    }

    private void printMainMenu() {
        System.out.println(printer.repeat('=', 78));
        System.out.println(printer.center("RESTAURANT BILLING APP", 78));
        System.out.println(printer.repeat('=', 78));
        System.out.println("1. Show sample menu");
        System.out.println("2. Add item to order");
        System.out.println("3. Add modifier to existing item");
        System.out.println("4. Void item");
        System.out.println("5. Set service charge");
        System.out.println("6. Set tax mode");
        System.out.println("7. View bill summary");
        System.out.println("8. Split bill evenly");
        System.out.println("9. Split bill by item");
        System.out.println("10. Print final receipt");
        System.out.println("11. Load sample scenario");
        System.out.println("12. Exit");
        System.out.println(printer.repeat('=', 78));
    }

    private void showSampleMenu() {
        System.out.println(printer.center("SAMPLE MENU", 78));
        System.out.println(printer.repeat('-', 78));
        System.out.println(printer.columns(new String[]{"ID", "Item", "Price", "Tax"}, new int[]{6, 34, 12, 12}, " "));
        System.out.println(printer.repeat('-', 78));
        for (MenuItem menuItem : sampleMenu.values()) {
            System.out.println(printer.columns(
                    new String[]{
                            String.valueOf(menuItem.getId()),
                            menuItem.getName(),
                            CurrencyUtil.format(menuItem.getBasePrice()),
                            CurrencyUtil.scale(menuItem.getTaxRate().multiply(BigDecimal.valueOf(100))).toPlainString() + "%"
                    },
                    new int[]{6, 34, 12, 12},
                    " "
            ));
        }
        System.out.println(printer.repeat('-', 78));
        System.out.println(printer.center("SAMPLE MODIFIERS", 78));
        System.out.println(printer.repeat('-', 78));
        System.out.println(printer.columns(new String[]{"ID", "Modifier", "Type", "Delta"}, new int[]{6, 34, 18, 12}, " "));
        System.out.println(printer.repeat('-', 78));
        for (Modifier modifier : sampleModifiers.values()) {
            System.out.println(printer.columns(
                    new String[]{
                            String.valueOf(modifier.getId()),
                            modifier.getName(),
                            modifier.getType().getDisplayName(),
                            CurrencyUtil.format(modifier.getPriceDelta())
                    },
                    new int[]{6, 34, 18, 12},
                    " "
            ));
        }
    }

    private void addItemToOrder() {
        int itemId = InputUtil.readInt(scanner, "Enter menu item id: ");
        MenuItem menuItem = sampleMenu.get(itemId);
        if (menuItem == null) {
            System.out.println("Menu item not found.");
            return;
        }
        int quantity = InputUtil.readInt(scanner, "Enter quantity: ");
        OrderItem orderItem = menuItem.toOrderItem(quantity);
        currentOrder.addItem(orderItem);
        System.out.println("Item added to order.");
    }

    private void addModifierToExistingItem() {
        int orderItemId = InputUtil.readInt(scanner, "Enter order item id: ");
        Optional<OrderItem> itemOptional = currentOrder.findItemById(orderItemId);
        if (itemOptional.isEmpty()) {
            System.out.println("Order item not found.");
            return;
        }
        int modifierId = InputUtil.readInt(scanner, "Enter modifier id: ");
        Modifier modifier = sampleModifiers.get(modifierId);
        if (modifier == null) {
            System.out.println("Modifier not found.");
            return;
        }
        itemOptional.get().addModifier(modifier);
        System.out.println("Modifier added.");
    }

    private void voidItem() {
        int orderItemId = InputUtil.readInt(scanner, "Enter order item id to void: ");
        Optional<OrderItem> itemOptional = currentOrder.findItemById(orderItemId);
        if (itemOptional.isEmpty()) {
            System.out.println("Order item not found.");
            return;
        }
        itemOptional.get().voidItem();
        System.out.println("Item voided.");
    }

    private void setServiceCharge() {
        BigDecimal rate = InputUtil.readBigDecimal(scanner, "Enter service charge rate as decimal (e.g. 0.10 for 10%): ");
        currentOrder.updateServiceChargeRate(rate);
        System.out.println("Service charge updated.");
    }

    private void setTaxMode() {
        System.out.println("1. BEFORE SERVICE CHARGE");
        System.out.println("2. AFTER SERVICE CHARGE");
        int choice = InputUtil.readInt(scanner, "Choose tax mode: ");
        switch (choice) {
            case 1 -> currentOrder.updateTaxMode(TaxMode.BEFORE_SERVICE_CHARGE);
            case 2 -> currentOrder.updateTaxMode(TaxMode.AFTER_SERVICE_CHARGE);
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
        System.out.println("Tax mode updated.");
    }

    private void viewBillSummary() {
        System.out.println(printer.center("BILL SUMMARY", 78));
        System.out.println(printer.repeat('-', 78));
        printActiveItems();
        System.out.println(printer.repeat('-', 78));
        System.out.println(summaryLine("Subtotal", billingService.calculateSubtotal(currentOrder)));
        System.out.println(summaryLine("Tax", billingService.calculateTax(currentOrder)));
        System.out.println(summaryLine("Service Charge", billingService.calculateServiceCharge(currentOrder)));
        System.out.println(summaryLine("Grand Total", billingService.calculateGrandTotal(currentOrder)));
    }

    private void splitBillEvenly() {
        int peopleCount = InputUtil.readInt(scanner, "Enter number of people: ");
        Map<Integer, BigDecimal> split = splitService.splitEvenly(currentOrder, peopleCount);
        printSplitResult("EVEN SPLIT", split);
    }

    private void splitBillByItem() {
        int peopleCount = InputUtil.readInt(scanner, "Enter number of people: ");
        Map<Integer, Integer> itemAssignments = new LinkedHashMap<>();
        for (OrderItem item : currentOrder.getActiveItems()) {
            int personId = InputUtil.readInt(scanner, "Assign item " + item.getItemId() + " (" + item.getItemName() + ") to person id: ");
            itemAssignments.put(item.getItemId(), personId);
        }
        Map<Integer, BigDecimal> split = splitService.splitByItem(currentOrder, itemAssignments, peopleCount);
        printSplitResult("ITEM-BASED SPLIT", split);
    }

    private void printFinalReceipt() {
        System.out.println(receiptService.generateReceipt(currentOrder));
    }

    private void loadSampleScenario() {
        currentOrder = SampleData.createSampleOrder();
        System.out.println("Sample scenario loaded.");
    }

    private void printActiveItems() {
        System.out.println(printer.columns(
                new String[]{"ID", "Item", "Qty", "Unit", "Subtotal", "Tax", "Total", "Status"},
            new int[]{5, 16, 4, 8, 9, 8, 9, 8},
                " "
        ));
        for (OrderItem item : currentOrder.getItems()) {
            String status = item.isActive() ? "ACTIVE" : "VOIDED";
            System.out.println(printer.columns(
                    new String[]{
                            String.valueOf(item.getItemId()),
                            item.getItemName(),
                            String.valueOf(item.getQuantity()),
                            CurrencyUtil.format(item.getUnitPriceWithModifiers()),
                            CurrencyUtil.format(item.isActive() ? item.getLineSubtotal() : CurrencyUtil.zero()),
                            CurrencyUtil.format(item.isActive() ? item.getTaxAmount() : CurrencyUtil.zero()),
                            CurrencyUtil.format(item.isActive() ? item.getLineTotal() : CurrencyUtil.zero()),
                            status
                    },
                new int[]{5, 16, 4, 8, 9, 8, 9, 8},
                    " "
            ));
        }
    }

    private String summaryLine(String label, BigDecimal value) {
        return printer.columns(
                new String[]{label, CurrencyUtil.format(value)},
                new int[]{20, 12},
                " "
        );
    }

    private void printSplitResult(String title, Map<Integer, BigDecimal> split) {
        System.out.println(printer.center(title, 78));
        System.out.println(printer.repeat('-', 78));
        BigDecimal runningTotal = CurrencyUtil.zero();
        for (Map.Entry<Integer, BigDecimal> entry : split.entrySet()) {
            runningTotal = runningTotal.add(entry.getValue());
            System.out.println(printer.columns(
                    new String[]{"Person " + entry.getKey(), CurrencyUtil.format(entry.getValue())},
                    new int[]{20, 12},
                    " "
            ));
        }
        System.out.println(printer.repeat('-', 78));
        System.out.println(summaryLine("Split Total", runningTotal));
        System.out.println(summaryLine("Grand Total", billingService.calculateGrandTotal(currentOrder)));
    }
}

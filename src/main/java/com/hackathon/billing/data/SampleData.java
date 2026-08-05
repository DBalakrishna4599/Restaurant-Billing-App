package com.hackathon.billing.data;

import com.hackathon.billing.model.MenuItem;
import com.hackathon.billing.model.Modifier;
import com.hackathon.billing.model.ModifierType;
import com.hackathon.billing.model.Order;
import com.hackathon.billing.model.OrderItem;
import com.hackathon.billing.model.TaxMode;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SampleData {
    private static final Map<Integer, MenuItem> SAMPLE_MENU = createMenu();
    private static final Map<Integer, Modifier> SAMPLE_MODIFIERS = createModifiers();

    private SampleData() {
    }

    public static Map<Integer, MenuItem> getSampleMenu() {
        return Collections.unmodifiableMap(SAMPLE_MENU);
    }

    public static Map<Integer, Modifier> getSampleModifiers() {
        return Collections.unmodifiableMap(SAMPLE_MODIFIERS);
    }

    public static Order createSampleOrder() {
        Order order = new Order("ORD-1001", new BigDecimal("0.10"), TaxMode.BEFORE_SERVICE_CHARGE);

        OrderItem pizza = SAMPLE_MENU.get(1).toOrderItem(2);
        pizza.addModifier(SAMPLE_MODIFIERS.get(101));
        pizza.addModifier(SAMPLE_MODIFIERS.get(103));
        order.addItem(pizza);

        OrderItem burger = SAMPLE_MENU.get(2).toOrderItem(1);
        burger.addModifier(SAMPLE_MODIFIERS.get(102));
        order.addItem(burger);

        OrderItem pasta = SAMPLE_MENU.get(3).toOrderItem(1);
        pasta.addModifier(SAMPLE_MODIFIERS.get(102));
        order.addItem(pasta);

        OrderItem coke = SAMPLE_MENU.get(4).toOrderItem(3);
        order.addItem(coke);

        OrderItem brownie = SAMPLE_MENU.get(5).toOrderItem(2);
        brownie.addModifier(SAMPLE_MODIFIERS.get(104));
        order.addItem(brownie);

        return order;
    }

    private static Map<Integer, MenuItem> createMenu() {
        Map<Integer, MenuItem> menu = new LinkedHashMap<>();
        menu.put(1, new MenuItem(1, "Margherita Pizza", new BigDecimal("250.00"), new BigDecimal("0.05")));
        menu.put(2, new MenuItem(2, "Veg Burger", new BigDecimal("180.00"), new BigDecimal("0.05")));
        menu.put(3, new MenuItem(3, "Pasta Alfredo", new BigDecimal("220.00"), new BigDecimal("0.12")));
        menu.put(4, new MenuItem(4, "Coke", new BigDecimal("40.00"), new BigDecimal("0.18")));
        menu.put(5, new MenuItem(5, "Brownie", new BigDecimal("120.00"), new BigDecimal("0.12")));
        return menu;
    }

    private static Map<Integer, Modifier> createModifiers() {
        Map<Integer, Modifier> modifiers = new LinkedHashMap<>();
        modifiers.put(101, new Modifier(101, "Extra Cheese", ModifierType.ADD_ON, new BigDecimal("20.00")));
        modifiers.put(102, new Modifier(102, "Extra Sauce", ModifierType.ADD_ON, new BigDecimal("10.00")));
        modifiers.put(103, new Modifier(103, "No Onion", ModifierType.CUSTOMIZATION, new BigDecimal("-5.00")));
        modifiers.put(104, new Modifier(104, "Add Ice Cream", ModifierType.ADD_ON, new BigDecimal("35.00")));
        return modifiers;
    }
}

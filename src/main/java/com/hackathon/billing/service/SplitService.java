package com.hackathon.billing.service;

import com.hackathon.billing.exception.BillingValidationException;
import com.hackathon.billing.model.Order;
import com.hackathon.billing.model.OrderItem;
import com.hackathon.billing.util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SplitService {
    private final BillingService billingService = new BillingService();

    public Map<Integer, BigDecimal> splitEvenly(Order order, int peopleCount) {
        validateSplitInputs(order, peopleCount);
        return splitAmountEvenly(billingService.calculateGrandTotal(order), peopleCount);
    }

    public Map<Integer, BigDecimal> splitByItem(Order order, Map<Integer, Integer> itemToPersonMap, int peopleCount) {
        validateSplitInputs(order, peopleCount);
        if (itemToPersonMap == null) {
            throw new BillingValidationException("Item to person map cannot be null.");
        }

        Map<Integer, BigDecimal> totals = createBlankSplit(peopleCount);
        BigDecimal allocatedItemTotal = CurrencyUtil.zero();

        List<OrderItem> activeItems = order.getActiveItems();
        for (OrderItem item : activeItems) {
            Integer personId = itemToPersonMap.get(item.getItemId());
            if (personId == null) {
                throw new BillingValidationException("Missing person assignment for item id " + item.getItemId() + ".");
            }
            validatePersonId(personId, peopleCount);
            BigDecimal itemTotal = item.getLineTotal();
            totals.put(personId, totals.get(personId).add(itemTotal));
            allocatedItemTotal = allocatedItemTotal.add(itemTotal);
        }

        BigDecimal remainder = billingService.calculateGrandTotal(order).subtract(allocatedItemTotal);
        Map<Integer, BigDecimal> remainderSplit = splitAmountEvenly(remainder, peopleCount);
        for (Map.Entry<Integer, BigDecimal> entry : remainderSplit.entrySet()) {
            totals.put(entry.getKey(), totals.get(entry.getKey()).add(entry.getValue()));
        }
        return totals;
    }

    private Map<Integer, BigDecimal> splitAmountEvenly(BigDecimal amount, int peopleCount) {
        Map<Integer, BigDecimal> totals = createBlankSplit(peopleCount);
        long totalPaisa = CurrencyUtil.toPaisa(amount);
        long baseShare = totalPaisa / peopleCount;
        long remainder = totalPaisa % peopleCount;

        for (int personId = 1; personId <= peopleCount; personId++) {
            long sharePaisa = baseShare;
            if (personId <= remainder) {
                sharePaisa += 1;
            }
            totals.put(personId, CurrencyUtil.fromPaisa(sharePaisa));
        }
        return totals;
    }

    private Map<Integer, BigDecimal> createBlankSplit(int peopleCount) {
        validatePeopleCount(peopleCount);
        Map<Integer, BigDecimal> totals = new LinkedHashMap<>();
        for (int personId = 1; personId <= peopleCount; personId++) {
            totals.put(personId, CurrencyUtil.zero());
        }
        return totals;
    }

    private void validateSplitInputs(Order order, int peopleCount) {
        if (order == null) {
            throw new BillingValidationException("Order cannot be null.");
        }
        order.validateState();
        validatePeopleCount(peopleCount);
    }

    private void validatePeopleCount(int peopleCount) {
        if (peopleCount <= 0) {
            throw new BillingValidationException("People count must be greater than zero.");
        }
    }

    private void validatePersonId(int personId, int peopleCount) {
        if (personId < 1 || personId > peopleCount) {
            throw new BillingValidationException("Person id must be between 1 and " + peopleCount + ".");
        }
    }
}

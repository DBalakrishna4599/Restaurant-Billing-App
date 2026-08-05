# Restaurant Billing App

Lightweight console-based restaurant billing demo implemented in Java. Shows menu, supports modifiers, voiding items, tax/service charge modes, bill splitting (even / by item), and formatted receipt output.

## Features
- Sample menu with base prices and tax rates
- Modifiers (add-ons, customizations, discounts) with price deltas
- Item voiding and validation
- Tax modes: BEFORE_SERVICE_CHARGE and AFTER_SERVICE_CHARGE
- Service charge calculation
- Grand total calculation and deterministic paise allocation when splitting
- Pretty printed receipt and simple console table printer

## Project layout

`pom.xml` - Maven descriptor

`src/main/java/com/hackathon/billing/` - main source tree

- `app/RestaurantBillingApp.java` - interactive console entrypoint
- `data/SampleData.java` - sample menu & modifiers and sample scenario
- `model/` - domain models (`MenuItem`, `Modifier`, `OrderItem`, `Order`, `TaxMode`, ...)
- `service/` - `BillingService`, `SplitService`, `ReceiptService`
- `util/` - `CurrencyUtil`, `ConsoleTablePrinter`, `InputUtil`
- `exception/` - domain exceptions

## Prerequisites
- Java 17+ (JDK) installed and `java` / `javac` on PATH
- Git (to clone repository)
- Optional: Maven if you prefer `mvn` commands

Check Java installed:

```bash
java -version
javac -version
```

## Clone the repository

```bash
git clone https://github.com/DBalakrishna4599/Restaurant-Billing-App
cd restaurant-billing
```

## Build & Run

Two ways: using Maven (if available) or direct `javac`/`java`.

### Using Maven (recommended if available)

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.hackathon.billing.app.RestaurantBillingApp
```

Or build jar and run:

```bash
mvn package
java -cp target/classes com.hackathon.billing.app.RestaurantBillingApp
```

### Without Maven (javac)

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
java -cp out com.hackathon.billing.app.RestaurantBillingApp
```

## Non-interactive examples (automated/demo)

Load sample scenario, view summary, print receipt, then exit:

```bash
printf '11\n7\n10\n12\n' | java -cp out com.hackathon.billing.app.RestaurantBillingApp
```

Split evenly for 4 people from sample scenario:

```bash
printf '11\n8\n4\n12\n' | java -cp out com.hackathon.billing.app.RestaurantBillingApp
```

Item-based split demo (interactive prompts are piped in this order):

```bash
printf '11\n9\n4\n1\n2\n3\n4\n1\n12\n' | java -cp out com.hackathon.billing.app.RestaurantBillingApp
```

## Quick Guide — Menu Flow

1. Show sample menu
2. Add item to order (enter menu item id and quantity)
3. Add modifier to existing item (enter order item id, modifier id)
4. Void item (mark item as voided)
5. Set service charge (decimal, e.g. `0.10` for 10%)
6. Set tax mode (before/after service charge)
7. View bill summary (subtotal, tax, service charge, grand total)
8. Split bill evenly (enter people count)
9. Split bill by item (assign items to person ids)
10. Print final receipt
11. Load sample scenario (prepopulated order)
12. Exit

## Deterministic split behavior

All money calculations use `BigDecimal` scaled to 2 decimals (paise). When splitting, the algorithm converts amounts to integer paise, divides evenly, and distributes the remainder starting from person id `1` upward to ensure deterministic allocation.

## Notes
- The app uses `com.hackathon.billing.exception` for domain errors. Runtime validation throws `BillingValidationException` or `BillingStateException` as appropriate.
- The sample data is initialized in `SampleData#createSampleOrder()`; use option `11` to load it quickly.

## Troubleshooting
- If `mvn` is missing, use the `javac` instructions above.
- If you see `java.lang.NoClassDefFoundError`, ensure you compiled with `javac -d out` and are running with the correct classpath.

## Next steps (optional)
- Add a Maven Shade plugin to create a runnable fat-jar for distribution.
- Add unit tests for `BillingService` and `SplitService`.
- Add a simple HTTP wrapper if you want a web API.

---
Enjoy the demo — open an issue or PR if you want enhancements.
- build by DAMULURI BALAKRISHNA 
- Github: https://github.com/DBalakrishna4599
- LinkedIn: https://www.linkedin.com/in/d-balakrishna/
- Remain Projects: https://manapatalu.me and https://airesearchassist.tech

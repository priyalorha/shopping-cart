# Shopping Cart System

A Java-based shopping cart system that applies promotional offers to fruit purchases, including a "Three for Two" discount offer.

## Features

- Fruit inventory management
- Promotional offer application:
    - **Three for Two**: Buy 3 items, pay for 2
    - ** BOGO ** : Buy 1 get 1
- Bill calculation with discount breakdown
- Extensible rule system for additional offers
- To add or update product use order.json,
- To generate bill use POST /api/bill, sample body [{"name":"lime", "quantity":4}, {"name":"melon", "quantity":3}, {"name":"apple", "quantity":3},
  {"name":"banana", "quantity":3}]
## Prerequisites

- Java 17+
- Maven 3.8+
- JUnit 5 (for testing)
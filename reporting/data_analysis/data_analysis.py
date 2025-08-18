import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sqlalchemy import create_engine
from datetime import datetime, timedelta

from database import engine

# 1. Connect to Database and Load Data
query = """
    SELECT ci.name, ci.price, ci.charged, ci.quantity, ci.offer_type, 
           ci.created_at, c.status 
    FROM cart_items ci
    JOIN carts c ON ci.cart_id = c.id
    WHERE c.status = 'CLOSED'
"""
df = pd.read_sql(query, engine)

# 2. Data Preparation
df['created_at'] = pd.to_datetime(df['created_at'])
df['date'] = df['created_at'].dt.date
df['total_sales'] = df['charged']  # Align with your model's 'charged' field
df['price_per_unit'] = df['charged'] / df['quantity']

# 3. Sales Analysis by Product
sales_by_product = df.groupby('name')['charged'].sum().sort_values(ascending=False)
print("Total Sales by Product:")
print(sales_by_product)

plt.figure(figsize=(10, 6))
sales_by_product.plot(kind='bar', title='Total Sales by Product')
plt.ylabel('Total Sales ($)')
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig('sales_by_product.png')
plt.show()

# 4. Time-Based Analysis
df.set_index('created_at', inplace=True)

# Monthly sales trend
monthly_sales = df['charged'].resample('M').sum()
monthly_sales.plot(title='Monthly Sales Trend', marker='o')
plt.ylabel('Sales ($)')
plt.savefig('monthly_sales_trend.png')
plt.show()

# Weekly moving average
weekly_avg = df['charged'].rolling('7D').mean()
plt.plot(weekly_avg, label='7-Day Moving Average')
df['charged'].plot(style='.', alpha=0.3, label='Daily Sales')
plt.legend()
plt.title('Sales Trend with 7-Day Moving Average')
plt.savefig('weekly_moving_avg.png')
plt.show()

# 5. Price Analysis
avg_price = df.groupby('name')['price'].mean().sort_values()
print("\nAverage Original Price by Product:")
print(avg_price)

plt.figure(figsize=(10, 6))
avg_price.plot(kind='barh', title='Average Original Price by Product')
plt.xlabel('Price ($)')
plt.tight_layout()
plt.savefig('avg_price_by_product.png')
plt.show()

# Price distribution
plt.figure(figsize=(12, 6))
sns.boxplot(x='name', y='price', data=df.reset_index())
plt.title('Price Distribution by Product')
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig('price_distribution.png')
plt.show()

# 6. Quantity Analysis
quantity_by_product = df.groupby('name')['quantity'].sum().sort_values(ascending=False)
print("\nTotal Quantity Sold by Product:")
print(quantity_by_product)

plt.figure(figsize=(8, 8))
quantity_by_product.plot(kind='pie', autopct='%1.1f%%', title='Quantity Sold by Product')
plt.savefig('quantity_by_product.png')
plt.show()

# Quantity vs. Price
plt.figure(figsize=(10, 6))
sns.scatterplot(x='price', y='quantity', hue='name', data=df.reset_index())
plt.title('Quantity Sold vs. Original Price')
plt.savefig('quantity_vs_price.png')
plt.show()

# 7. Offer Type Analysis
if 'offer_type' in df.columns:
    offer_performance = df.groupby('offer_type').agg({
        'charged': 'sum',
        'quantity': 'sum',
        'price': 'mean'
    })
    offer_performance['discount_pct'] = 1 - (offer_performance['charged'] /
                                             (offer_performance['price'] * offer_performance['quantity']))
    print("\nPerformance by Offer Type:")
    print(offer_performance)

# 8. Product Performance Summary
performance = df.groupby('name').agg({
    'charged': ['sum', 'mean', 'std'],
    'quantity': ['sum', 'mean'],
    'price': ['mean', 'min', 'max'],
    'price_per_unit': 'mean'
})
performance.columns = ['_'.join(col).strip() for col in performance.columns.values]
performance['sales_per_unit'] = performance['charged_sum'] / performance['quantity_sum']

print("\nProduct Performance Summary:")
print(performance)

# 9. Export Results
performance.to_excel('product_performance.xlsx')

# 10. Association Rules (if transaction data available)
try:
    from mlxtend.frequent_patterns import apriori, association_rules

    # Create transaction matrix
    pivot = pd.crosstab(df.reset_index()['cart_id'], df['name'])
    pivot = pivot.applymap(lambda x: 1 if x > 0 else 0)

    # Find frequent itemsets
    frequent_itemsets = apriori(pivot, min_support=0.05, use_colnames=True)
    rules = association_rules(frequent_itemsets, metric="lift", min_threshold=1)

    print("\nTop Product Associations:")
    print(rules.sort_values('lift', ascending=False).head(10).to_string())

    # Save rules to CSV
    rules.sort_values('lift', ascending=False).to_csv('product_association_rules.csv', index=False)
except ImportError:
    print("\nmlxtend package not available - skipping association rules analysis")
except Exception as e:
    print(f"\nCould not generate association rules: {str(e)}")

print("\nAnalysis complete. Results saved to:")
print("- sales_by_product.png")
print("- monthly_sales_trend.png")
print("- weekly_moving_avg.png")
print("- avg_price_by_product.png")
print("- price_distribution.png")
print("- quantity_by_product.png")
print("- quantity_vs_price.png")
print("- product_performance.xlsx")
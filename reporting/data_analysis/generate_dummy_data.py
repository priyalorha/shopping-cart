from database import Base, engine

import matplotlib.pyplot as plt
import seaborn as sns
from model_factories.model_factory import UserFactory, CartFactory, CartItemFactory

Base.metadata.create_all(bind=engine)
#
# print("Tables created successfully!")
#
#
#
# for cart in CartFactory.create_batch(100):  # assuming 'carts' is your queryset/list of carts
#     try:
#
#         # Create 5 cart items for each cart
#         cart_items = CartItemFactory.create_batch(5, cart=cart)
#         print(f"Created {len(cart_items)} cart items for cart {cart.id}")
#     except Exception as e:
#         print(f"Error creating cart items for cart {cart.id}: {str(e)}")
#         continue  # this will move to the next cart




# Total Sales by Fruit

sales_by_fruit = df.groupby('fruit')['total_sales'].sum().sort_values(ascending=False)
print(sales_by_fruit)

# Visualization
sales_by_fruit.plot(kind='bar', title='Total Sales by Fruit')
plt.ylabel('Total Sales ($)')
plt.show()



# df = pd.DataFrame([user.__dict__ for user in users])
# print(df)
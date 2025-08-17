from faker import Faker
import factory
from datetime import datetime, timedelta

from database import SessionLocal
from models import Cart, CartItem
from models.CartModel import CartStatus
from models.UserModel import User, UserRole
from factory.alchemy import SQLAlchemyModelFactory

fake = Faker()
session = SessionLocal()

class UserFactory(SQLAlchemyModelFactory):
    class Meta:
        model = User
        sqlalchemy_session = session
        sqlalchemy_session_persistence = "commit"  # Auto-commit instances



    # ID generation - better alternatives
    # id = factory.Sequence(lambda n: n + 1)  # Simple auto-increment
    # OR for UUID:
    # id = factory.LazyFunction(lambda: str(uuid.uuid4()))

    name = factory.Faker("name")

    # Improved email generation with Faker's built-in email
    email = factory.Faker('email')
    # Alternative using Faker's email:
    # email = factory.Faker("email", domain="example.com")

    role = factory.Iterator([UserRole.admin, UserRole.customer])

    # More secure password options
    password = factory.Faker("password", length=12, special_chars=True, digits=True, upper_case=True)

    # Better timestamp handling
    created_at = factory.LazyFunction(datetime.now)
    updated_at = factory.LazyFunction(datetime.now)

    # Admin trait with additional admin-specific fields



class CartFactory(SQLAlchemyModelFactory):
    class Meta:
        model = Cart
        sqlalchemy_session = session  # Will be set during app initialization
        sqlalchemy_session_persistence = "commit"

    user = factory.SubFactory(UserFactory)  # Assuming you have a UserFactory
    status = factory.Iterator([CartStatus.OPEN, CartStatus.CLOSED])
    total = factory.Faker("pydecimal", left_digits=5, right_digits=2, positive=True)
    quantity = factory.Faker("random_int", min=0, max=10)
    created_at = factory.LazyFunction(datetime.utcnow)
    updated_at = factory.LazyFunction(datetime.utcnow)


class CartItemFactory(SQLAlchemyModelFactory):
    class Meta:
        model = CartItem
        sqlalchemy_session = session  # Will be set during app initialization
        sqlalchemy_session_persistence = "commit"


    cart = factory.SubFactory(CartFactory)  # Requires CartFactory
    name = factory.Iterator(['lime', 'melon', 'apple', 'banana'])
    price = factory.Faker("pydecimal", left_digits=2, right_digits=2, positive=True)
    charged = factory.Faker("pydecimal", left_digits=2, right_digits=2, positive=True)
    avg_price = factory.Faker("pydecimal", left_digits=2, right_digits=2, positive=True)
    quantity = factory.Faker("random_int", min=1, max=10)
    offer_type = factory.Iterator(["NONE", "DISCOUNT", "BOGO"])
    created_at = factory.LazyFunction(datetime.utcnow)
    updated_at = factory.LazyFunction(datetime.utcnow)

    class Params:
        discounted = factory.Trait(
            offer_type="DISCOUNT",
            price=factory.Faker("pydecimal", left_digits=2, right_digits=2,
                              positive=True, max_value=50)
        )
        bulk = factory.Trait(
            quantity=factory.Faker("random_int", min=10, max=100)
        )
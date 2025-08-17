from datetime import datetime
from sqlalchemy import Column, Integer, String, Numeric, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from sqlalchemy.schema import Index
from database import Base  # Assuming you have a Base from your database setup

class CartItem(Base):
    __tablename__ = 'cart_items'
    __table_args__ = (
        Index('unique_cart_item',  'cart_id', 'name', unique=True),
    )

    id = Column(Integer, primary_key=True, autoincrement=True)

    cart_id = Column(Integer, ForeignKey('carts.id'), nullable=False)
    name = Column(String, nullable=False)
    price = Column(Numeric(10, 2))
    charged = Column(Numeric(10, 2))
    avg_price = Column(Numeric(10, 2))
    quantity = Column(Integer, nullable=False, default=1)
    offer_type = Column(String, default="NONE")
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow,
                       onupdate=datetime.utcnow, nullable=False)

    # Relationships
    cart = relationship("Cart", back_populates="items")


    def __repr__(self):
        return f"<CartItem(id={self.id}, name='{self.name}', quantity={self.quantity})>"
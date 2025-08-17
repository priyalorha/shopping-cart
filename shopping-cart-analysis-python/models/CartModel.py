from enum import Enum
from datetime import datetime
from sqlalchemy import Column, Integer, Numeric, DateTime, ForeignKey, Enum as SQLAlchemyEnum
from sqlalchemy.orm import relationship, declarative_base
from database import Base
# Assuming you have a Base from your database setup
class CartStatus(Enum):
    OPEN = "OPEN"
    CLOSED = "CLOSED"

class Cart(Base):
    __tablename__ = 'carts'

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey('users.id'), nullable=False)
    status = Column(SQLAlchemyEnum(CartStatus), default=CartStatus.OPEN, nullable=False)
    total = Column(Numeric(10, 2), default=0, nullable=False)
    quantity = Column(Integer, default=0, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)

    # Relationships
    user = relationship("User", back_populates="carts")
    items = relationship("CartItem", back_populates="cart", cascade="all, delete-orphan")
    def __repr__(self):
        return f"<Cart(id={self.id}, status={self.status}, total={self.total})>"
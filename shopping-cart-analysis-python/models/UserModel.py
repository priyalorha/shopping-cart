from datetime import datetime
from sqlalchemy import Column, String, Integer, Enum, DateTime, ForeignKey
from sqlalchemy.orm import relationship, validates
import enum
import bcrypt
from database import Base


class UserRole(enum.Enum):
    admin = "admin"
    customer = "customer"


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    email = Column(String, unique=True, nullable=False, index=True)
    password = Column(String, nullable=False)
    role = Column(Enum(UserRole), nullable=False, default=UserRole.customer)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)

    # Relationship with Cart (one-to-many)
    carts = relationship("Cart", back_populates="user", cascade="all, delete-orphan")


    # ------------------------
    # Password Handling
    # ------------------------
    def set_password(self, password: str):
        """Hash and set password."""
        salt = bcrypt.gensalt()
        self.password = bcrypt.hashpw(password.encode("utf-8"), salt).decode("utf-8")

    def valid_password(self, password: str) -> bool:
        """Check if provided password matches hashed password."""
        return bcrypt.checkpw(password.encode("utf-8"), self.password.encode("utf-8"))

    # ------------------------
    # Validation
    # ------------------------
    @validates("email")
    def validate_email(self, key, address):
        if "@" not in address:
            raise ValueError("Invalid email address")
        return address

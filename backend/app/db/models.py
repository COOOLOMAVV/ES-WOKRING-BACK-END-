from sqlalchemy import Column, Integer, BigInteger, String, Numeric, Text, DateTime, ForeignKey, Index
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from app.db.database import Base


class Agent(Base):
    """
    Agent Table - stores licensed real estate agent profiles.
    Maps to PostgreSQL 'agents' table.
    """
    __tablename__ = "agents"

    id = Column(
        BigInteger().with_variant(Integer, "sqlite"),
        primary_key=True,
        index=True,
        autoincrement=True
    )
    first_name = Column(String(100), nullable=False)
    last_name = Column(String(100), nullable=False)
    email = Column(String(255), unique=True, index=True, nullable=False)
    phone = Column(String(50), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now(), nullable=False)

    # 1-to-Many relationship: One Agent manages many Properties
    properties = relationship("Property", back_populates="agent", cascade="all, delete-orphan")


class Client(Base):
    """
    Client Table - stores prospective buyers and renters.
    Maps to PostgreSQL 'clients' table.
    """
    __tablename__ = "clients"

    id = Column(
        BigInteger().with_variant(Integer, "sqlite"),
        primary_key=True,
        index=True,
        autoincrement=True
    )
    first_name = Column(String(100), nullable=False)
    last_name = Column(String(100), nullable=False)
    email = Column(String(255), unique=True, index=True, nullable=False)
    phone = Column(String(50), nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now(), nullable=False)


class Property(Base):
    """
    Property Table - stores real estate listings linked to managing agents.
    Maps to PostgreSQL 'properties' table.
    Uses NUMERIC for price and financial/measure calculations (no float).
    """
    __tablename__ = "properties"

    id = Column(
        BigInteger().with_variant(Integer, "sqlite"),
        primary_key=True,
        index=True,
        autoincrement=True
    )
    title = Column(String(255), nullable=False, index=True)
    description = Column(Text, nullable=True)
    property_type = Column(String(100), nullable=False, index=True)
    price = Column(Numeric(12, 2), nullable=False)  # Stored as precise NUMERIC(12, 2)
    address = Column(String(255), nullable=False)
    city = Column(String(100), nullable=False, index=True)
    bedrooms = Column(Integer, nullable=False, default=1)
    bathrooms = Column(Numeric(3, 1), nullable=False, default=1.0)
    area = Column(Numeric(10, 2), nullable=False, default=0.0)  # square feet / meters
    status = Column(String(50), nullable=False, default="available", index=True)
    agent_id = Column(
        BigInteger().with_variant(Integer, "sqlite"),
        ForeignKey("agents.id", ondelete="CASCADE"),
        nullable=False,
        index=True
    )
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now(), nullable=False)

    # Relationship back to Agent
    agent = relationship("Agent", back_populates="properties")


# Composite index for search queries filtering by city and listing status
Index("ix_properties_city_status", Property.city, Property.status)

from datetime import datetime
from decimal import Decimal
from typing import Optional
from pydantic import BaseModel, ConfigDict, Field, field_validator
from app.schemas.agent import AgentResponse

# Standard supported property types and listing statuses
VALID_PROPERTY_TYPES = {
    "house", "apartment", "condo", "villa", "townhouse", "commercial", "land", "loft", "penthouse"
}
VALID_STATUSES = {
    "available", "pending", "sold", "rented", "under_offer"
}


class PropertyBase(BaseModel):
    title: str = Field(..., min_length=2, max_length=255, description="Property listing title")
    description: Optional[str] = Field(None, description="Detailed description of the property")
    property_type: str = Field(..., min_length=2, max_length=100, description="e.g. House, Apartment, Condo, Villa, Townhouse")
    price: Decimal = Field(..., gt=0, description="Property price in currency units (must be strictly positive, NUMERIC)")
    address: str = Field(..., min_length=3, max_length=255, description="Street address")
    city: str = Field(..., min_length=2, max_length=100, description="City location")
    bedrooms: int = Field(default=1, ge=0, description="Number of bedrooms (non-negative integer)")
    bathrooms: Decimal = Field(default=Decimal("1.0"), ge=0, description="Number of bathrooms (e.g. 1.0, 1.5, 2.0)")
    area: Decimal = Field(default=Decimal("0.0"), gt=0, description="Total usable area in sq ft or sq meters (must be positive)")
    status: str = Field(default="available", max_length=50, description="Listing status: available, pending, sold, rented")
    agent_id: int = Field(..., gt=0, description="Foreign key referencing a valid Agent ID")

    @field_validator("property_type")
    @classmethod
    def validate_property_type(cls, v: str) -> str:
        cleaned = v.strip().lower()
        if cleaned not in VALID_PROPERTY_TYPES:
            # Allow capitalized custom types if non-empty, but enforce sensible length
            if len(cleaned) < 2:
                raise ValueError("Property type must be at least 2 characters long.")
        return v.strip().title()

    @field_validator("status")
    @classmethod
    def validate_status(cls, v: str) -> str:
        cleaned = v.strip().lower()
        if cleaned not in VALID_STATUSES:
            raise ValueError(
                f"Invalid status '{v}'. Allowed values: {', '.join(sorted(VALID_STATUSES))}"
            )
        return cleaned


class PropertyCreate(PropertyBase):
    pass


class PropertyUpdate(BaseModel):
    title: Optional[str] = Field(None, min_length=2, max_length=255)
    description: Optional[str] = None
    property_type: Optional[str] = Field(None, min_length=2, max_length=100)
    price: Optional[Decimal] = Field(None, gt=0)
    address: Optional[str] = Field(None, min_length=3, max_length=255)
    city: Optional[str] = Field(None, min_length=2, max_length=100)
    bedrooms: Optional[int] = Field(None, ge=0)
    bathrooms: Optional[Decimal] = Field(None, ge=0)
    area: Optional[Decimal] = Field(None, gt=0)
    status: Optional[str] = Field(None, max_length=50)
    agent_id: Optional[int] = Field(None, gt=0)

    @field_validator("property_type")
    @classmethod
    def validate_property_type(cls, v: Optional[str]) -> Optional[str]:
        if v is not None:
            cleaned = v.strip().lower()
            if len(cleaned) < 2:
                raise ValueError("Property type must be at least 2 characters long.")
            return v.strip().title()
        return v

    @field_validator("status")
    @classmethod
    def validate_status(cls, v: Optional[str]) -> Optional[str]:
        if v is not None:
            cleaned = v.strip().lower()
            if cleaned not in VALID_STATUSES:
                raise ValueError(
                    f"Invalid status '{v}'. Allowed values: {', '.join(sorted(VALID_STATUSES))}"
                )
            return cleaned
        return v


class PropertyResponse(PropertyBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None
    agent: Optional[AgentResponse] = None

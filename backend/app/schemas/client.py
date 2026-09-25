from datetime import datetime
from typing import Optional
from pydantic import BaseModel, ConfigDict, EmailStr, Field


class ClientBase(BaseModel):
    first_name: str = Field(..., min_length=1, max_length=100, description="Client first name")
    last_name: str = Field(..., min_length=1, max_length=100, description="Client last name")
    email: EmailStr = Field(..., description="Unique email address for client")
    phone: Optional[str] = Field(None, max_length=50, description="Phone contact number")


class ClientCreate(ClientBase):
    pass


class ClientUpdate(BaseModel):
    first_name: Optional[str] = Field(None, min_length=1, max_length=100)
    last_name: Optional[str] = Field(None, min_length=1, max_length=100)
    email: Optional[EmailStr] = None
    phone: Optional[str] = Field(None, max_length=50)


class ClientResponse(ClientBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

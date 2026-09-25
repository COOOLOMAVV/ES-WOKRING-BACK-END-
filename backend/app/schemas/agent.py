from datetime import datetime
from typing import Optional
from pydantic import BaseModel, ConfigDict, EmailStr, Field


class AgentBase(BaseModel):
    first_name: str = Field(..., min_length=1, max_length=100, description="Agent first name")
    last_name: str = Field(..., min_length=1, max_length=100, description="Agent last name")
    email: EmailStr = Field(..., description="Unique email address for agent")
    phone: Optional[str] = Field(None, max_length=50, description="Phone contact number")


class AgentCreate(AgentBase):
    pass


class AgentUpdate(BaseModel):
    first_name: Optional[str] = Field(None, min_length=1, max_length=100)
    last_name: Optional[str] = Field(None, min_length=1, max_length=100)
    email: Optional[EmailStr] = None
    phone: Optional[str] = Field(None, max_length=50)


class AgentResponse(AgentBase):
    model_config = ConfigDict(from_attributes=True)

    id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

from typing import Optional
from pydantic import BaseModel, ConfigDict, Field


class AppointmentBase(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)

    id: str
    property_id: Optional[str] = Field(alias="propertyId", default=None)
    property_title: str = Field(alias="propertyTitle", default="")
    property_address: str = Field(alias="propertyAddress", default="")
    client_name: str = Field(alias="clientName", default="")
    client_phone: str = Field(alias="clientPhone", default="")
    client_email: str = Field(alias="clientEmail", default="")
    appointment_date: str = Field(alias="date", default="")
    time_slot: str = Field(alias="timeSlot", default="")
    appointment_type: str = Field(alias="type", default="Viewing")
    status: str = "UPCOMING"
    notes: str = ""
    timestamp_epoch: int = Field(alias="timestamp", default=0)


class AppointmentCreate(AppointmentBase):
    pass


class AppointmentResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)

    id: str
    property_id: Optional[str] = None
    property_title: str
    property_address: str
    client_name: str
    client_phone: str
    client_email: str
    appointment_date: str
    time_slot: str
    appointment_type: str
    status: str
    notes: str
    timestamp_epoch: int
    created_at: Optional[str] = None

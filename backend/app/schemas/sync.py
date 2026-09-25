from typing import List, Optional, Any, Dict
from pydantic import BaseModel, ConfigDict
from app.schemas.property import PropertyCreate
from app.schemas.appointment import AppointmentCreate
from app.schemas.inquiry import InquiryCreate


class SyncPushRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    properties: Optional[List[Dict[str, Any]]] = []
    appointments: Optional[List[Dict[str, Any]]] = []
    inquiries: Optional[List[Dict[str, Any]]] = []
    notifications: Optional[List[Dict[str, Any]]] = []


class SyncPushResponse(BaseModel):
    success: bool = True
    message: str = "Successfully pushed and synchronized local data to PostgreSQL!"
    syncedAt: str


class RemoteSnapshotResponse(BaseModel):
    properties: List[Dict[str, Any]] = []
    appointments: List[Dict[str, Any]] = []
    inquiries: List[Dict[str, Any]] = []
    notifications: List[Dict[str, Any]] = []

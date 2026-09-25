from typing import Optional, List
from pydantic import BaseModel, ConfigDict, Field


class ChatMessageBase(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)

    id: str
    inquiry_id: Optional[str] = Field(alias="inquiryId", default=None)
    sender: str
    message_text: str = Field(alias="text", default="")
    time_sent: str = Field(alias="time", default="")
    is_from_me: bool = Field(alias="isFromMe", default=True)


class ChatMessageCreate(ChatMessageBase):
    pass


class ChatMessageResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)

    id: str
    sender: str
    text: str = Field(validation_alias="message_text")
    time: str = Field(validation_alias="time_sent")
    isFromMe: bool = Field(validation_alias="is_from_me")


class InquiryBase(BaseModel):
    model_config = ConfigDict(populate_by_name=True, from_attributes=True)

    id: str
    property_id: Optional[str] = Field(alias="propertyId", default=None)
    property_title: str = Field(alias="propertyTitle", default="")
    property_address: str = Field(alias="propertyAddress", default="")
    sender_name: str = Field(alias="senderName", default="")
    sender_email: str = Field(alias="senderEmail", default="")
    sender_phone: str = Field(alias="senderPhone", default="")
    message: str = ""
    time_ago: str = Field(alias="timeAgo", default="Just now")
    is_unread: bool = Field(alias="isUnread", default=True)
    updated_at_epoch: int = Field(alias="updatedAt", default=0)
    messages: List[ChatMessageCreate] = []


class InquiryCreate(InquiryBase):
    pass


class InquiryResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)

    id: str
    property_id: Optional[str] = None
    property_title: str
    property_address: str
    sender_name: str
    sender_email: str
    sender_phone: str
    message: str
    time_ago: str
    is_unread: bool
    updated_at_epoch: int
    created_at: Optional[str] = None
    messages: List[ChatMessageResponse] = []

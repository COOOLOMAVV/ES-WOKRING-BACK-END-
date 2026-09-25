from typing import List, Dict, Any
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.services.inquiry_service import InquiryService

router = APIRouter()


@router.get("/inquiries", tags=["Inquiries"])
def list_inquiries(db: Session = Depends(get_db)):
    """
    Returns all buyer inquiries with nested chat messages, ordered by updated_at_epoch DESC.
    """
    return InquiryService.get_all(db)


@router.post("/inquiries", tags=["Inquiries"])
def create_or_update_inquiry(payload: Dict[str, Any], db: Session = Depends(get_db)):
    """
    Upserts an inquiry and inserts nested chat messages if present.
    """
    try:
        return InquiryService.upsert(db, payload)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to upsert inquiry: {str(e)}"
        )

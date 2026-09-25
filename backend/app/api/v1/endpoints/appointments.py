from typing import List, Dict, Any
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.services.appointment_service import AppointmentService

router = APIRouter()


@router.get("/appointments", tags=["Appointments"])
def list_appointments(db: Session = Depends(get_db)):
    """
    Returns all viewing appointments ordered by timestamp_epoch DESC.
    """
    return AppointmentService.get_all(db)


@router.post("/appointments", tags=["Appointments"])
def create_or_update_appointment(payload: Dict[str, Any], db: Session = Depends(get_db)):
    """
    Upserts an appointment by ID. Accepts both camelCase and snake_case fields.
    """
    try:
        return AppointmentService.upsert(db, payload)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to upsert appointment: {str(e)}"
        )

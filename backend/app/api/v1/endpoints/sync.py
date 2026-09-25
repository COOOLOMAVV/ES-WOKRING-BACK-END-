from typing import Dict, Any
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.services.sync_service import SyncService

router = APIRouter()


@router.post("/sync/push", tags=["Sync"])
def push_sync_data(payload: Dict[str, Any], db: Session = Depends(get_db)):
    """
    Transactional bulk synchronization from mobile device to PostgreSQL.
    """
    try:
        return SyncService.push_data(db, payload)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Sync push transaction failed: {str(e)}"
        )


@router.get("/sync/pull", tags=["Sync"])
def pull_sync_data(db: Session = Depends(get_db)):
    """
    Retrieves full remote snapshot of all properties, appointments, inquiries, and notifications.
    """
    try:
        return SyncService.pull_data(db)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Sync pull failed: {str(e)}"
        )

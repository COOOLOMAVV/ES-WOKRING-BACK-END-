from typing import List, Dict, Any
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.database import get_db
from app.services.property_service import PropertyService

router = APIRouter()


@router.get("/properties", tags=["Properties"])
def list_properties(db: Session = Depends(get_db)):
    """
    Returns all properties ordered by created_at DESC.
    """
    return PropertyService.get_all(db)


@router.post("/properties", tags=["Properties"])
def create_or_update_property(payload: Dict[str, Any], db: Session = Depends(get_db)):
    """
    Upserts a property by ID. Accepts both camelCase and snake_case fields.
    """
    try:
        return PropertyService.upsert(db, payload)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to upsert property: {str(e)}"
        )


@router.delete("/properties/{property_id}", tags=["Properties"])
def delete_property(property_id: str, db: Session = Depends(get_db)):
    """
    Deletes a property by ID.
    """
    success = PropertyService.delete(db, property_id)
    if not success:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Property '{property_id}' not found."
        )
    return {"success": True, "id": property_id}

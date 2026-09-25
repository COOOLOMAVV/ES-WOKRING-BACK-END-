from fastapi import APIRouter
from app.api.v1.endpoints import health, properties, appointments, inquiries, sync

api_router = APIRouter()

api_router.include_router(health.router)
api_router.include_router(properties.router)
api_router.include_router(appointments.router)
api_router.include_router(inquiries.router)
api_router.include_router(sync.router)

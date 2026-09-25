"""
Property Model - Re-exports canonical Property mapped to PostgreSQL 'properties' table.
"""
from app.db.models import Property

__all__ = ["Property"]

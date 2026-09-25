from typing import List, Optional, Dict, Any
from sqlalchemy.orm import Session
from sqlalchemy import desc
from app.models.property import Property


def _normalize_string_list(val: Any) -> str:
    if isinstance(val, list):
        return ",".join(str(x) for x in val)
    return str(val or "")


class PropertyService:

    @staticmethod
    def get_all(db: Session) -> List[Property]:
        return db.query(Property).order_by(desc(Property.created_at)).all()

    @staticmethod
    def get_by_id(db: Session, property_id: str) -> Optional[Property]:
        return db.query(Property).filter(Property.id == property_id).first()

    @staticmethod
    def upsert(db: Session, data: Dict[str, Any]) -> Property:
        prop_id = str(data.get("id"))
        existing = db.query(Property).filter(Property.id == prop_id).first()

        title = data.get("title", "")
        address = data.get("address", "")
        city_state_zip = data.get("cityStateZip") or data.get("city_state_zip", "")
        price = int(data.get("price", 0))
        price_formatted = data.get("priceFormatted") or data.get("price_formatted", f"₱{price:,}")
        is_rental = bool(data.get("isRental") if "isRental" in data else data.get("is_rental", False))
        beds = float(data.get("beds", 3.0))
        baths = float(data.get("baths", 2.0))
        sqft = int(data.get("sqft", 1500))
        property_type = data.get("propertyType") or data.get("property_type", "House")
        description = data.get("description", "")
        image_res_id = int(data.get("imageResId") or data.get("image_res_id", 0))
        media_uris = _normalize_string_list(data.get("mediaUris") or data.get("media_uris", ""))
        is_favorite = bool(data.get("isFavorite") if "isFavorite" in data else data.get("is_favorite", False))
        status = data.get("status", "Active")
        available_dates = _normalize_string_list(data.get("availableDates") or data.get("available_dates", ""))
        available_time_slots = _normalize_string_list(data.get("availableTimeSlots") or data.get("available_time_slots", ""))
        amenities = _normalize_string_list(data.get("amenities", ""))
        year_built = int(data.get("yearBuilt") or data.get("year_built", 2023))
        agent_name = data.get("agentName") or data.get("agent_name", "Sarah Jenkins")
        agent_title = data.get("agentTitle") or data.get("agent_title", "")
        agent_phone = data.get("agentPhone") or data.get("agent_phone", "+63 917 555 0192")
        agent_email = data.get("agentEmail") or data.get("agent_email", "sarah.jenkins@estateflow.com")

        if existing:
            existing.title = title
            existing.price = price
            existing.price_formatted = price_formatted
            existing.description = description
            existing.is_favorite = is_favorite
            existing.media_uris = media_uris
            existing.status = status
            db.commit()
            db.refresh(existing)
            return existing
        else:
            new_prop = Property(
                id=prop_id,
                title=title,
                address=address,
                city_state_zip=city_state_zip,
                price=price,
                price_formatted=price_formatted,
                is_rental=is_rental,
                beds=beds,
                baths=baths,
                sqft=sqft,
                property_type=property_type,
                description=description,
                image_res_id=image_res_id,
                media_uris=media_uris,
                is_favorite=is_favorite,
                status=status,
                available_dates=available_dates,
                available_time_slots=available_time_slots,
                amenities=amenities,
                year_built=year_built,
                agent_name=agent_name,
                agent_title=agent_title,
                agent_phone=agent_phone,
                agent_email=agent_email
            )
            db.add(new_prop)
            db.commit()
            db.refresh(new_prop)
            return new_prop

    @staticmethod
    def delete(db: Session, property_id: str) -> bool:
        prop = db.query(Property).filter(Property.id == property_id).first()
        if prop:
            db.delete(prop)
            db.commit()
            return True
        return False

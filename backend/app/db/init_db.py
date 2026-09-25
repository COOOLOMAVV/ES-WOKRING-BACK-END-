import logging
from decimal import Decimal
from app.db.database import Base, engine, SessionLocal
from app.db.models import Agent, Client, Property

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("estateflow.init_db")


def init_db(seed_sample_data: bool = True) -> None:
    """
    Creates database tables and optionally seeds initial presentation records.
    """
    logger.info("Creating database tables...")
    Base.metadata.create_all(bind=engine)
    logger.info("Database tables successfully verified / created.")

    if not seed_sample_data:
        return

    db = SessionLocal()
    try:
        # Check if agents already exist
        if db.query(Agent).first():
            logger.info("Database already contains data. Skipping initial seeding.")
            return

        logger.info("Seeding initial Agents, Clients, and Properties for demonstration...")

        # 1. Seed Agents
        agent_sarah = Agent(
            first_name="Sarah",
            last_name="Jenkins",
            email="sarah.jenkins@estateflow.com",
            phone="+1 (555) 234-5678"
        )
        agent_marcus = Agent(
            first_name="Marcus",
            last_name="Vance",
            email="marcus.vance@estateflow.com",
            phone="+1 (555) 345-6789"
        )
        agent_elena = Agent(
            first_name="Elena",
            last_name="Rostova",
            email="elena.rostova@estateflow.com",
            phone="+1 (555) 456-7890"
        )
        db.add_all([agent_sarah, agent_marcus, agent_elena])
        db.commit()
        db.refresh(agent_sarah)
        db.refresh(agent_marcus)
        db.refresh(agent_elena)

        # 2. Seed Clients
        client_david = Client(
            first_name="David",
            last_name="Miller",
            email="david.miller@example.com",
            phone="+1 (555) 789-0123"
        )
        client_sophia = Client(
            first_name="Sophia",
            last_name="Chen",
            email="sophia.chen@example.com",
            phone="+1 (555) 890-1234"
        )
        client_james = Client(
            first_name="James",
            last_name="Wilson",
            email="james.wilson@example.com",
            phone="+1 (555) 901-2345"
        )
        db.add_all([client_david, client_sophia, client_james])
        db.commit()

        # 3. Seed Properties linked to Agents
        prop1 = Property(
            title="Pinecrest Modern Townhouse",
            description="Contemporary multi-level townhouse in prime residential district with private garage and terrace.",
            property_type="Townhouse",
            price=Decimal("680000.00"),
            address="742 Evergreen Terrace",
            city="Springfield",
            bedrooms=3,
            bathrooms=Decimal("2.5"),
            area=Decimal("2100.00"),
            status="available",
            agent_id=agent_sarah.id
        )
        prop2 = Property(
            title="Skyline Luxury Penthouse",
            description="Stunning penthouse with panoramic 360-degree skyline views, private elevator access, and designer finishes.",
            property_type="Apartment",
            price=Decimal("1450000.00"),
            address="100 Ocean Avenue, Unit 42A",
            city="Metropolis",
            bedrooms=4,
            bathrooms=Decimal("3.5"),
            area=Decimal("3400.00"),
            status="available",
            agent_id=agent_sarah.id
        )
        prop3 = Property(
            title="Urban Industrial Loft",
            description="Spacious brick-and-beam open layout loft featuring 14ft ceilings, polished concrete floors, and oversized windows.",
            property_type="Condo",
            price=Decimal("495000.00"),
            address="512 Foundry Way, #3B",
            city="Downtown",
            bedrooms=2,
            bathrooms=Decimal("2.0"),
            area=Decimal("1650.00"),
            status="available",
            agent_id=agent_marcus.id
        )
        prop4 = Property(
            title="Sunny Meadows Family Home",
            description="Charming suburban single-family home with expansive landscaped backyard, updated kitchen, and top-rated school district.",
            property_type="House",
            price=Decimal("785000.00"),
            address="12 Meadowbrook Drive",
            city="Greenville",
            bedrooms=4,
            bathrooms=Decimal("3.0"),
            area=Decimal("2850.00"),
            status="pending",
            agent_id=agent_elena.id
        )
        prop5 = Property(
            title="Boutique Coastal Villa",
            description="Exclusive waterfront villa featuring private infinity pool, solar energy system, and private dock access.",
            property_type="Villa",
            price=Decimal("2200000.00"),
            address="88 Seaside Boulevard",
            city="Bayshore",
            bedrooms=5,
            bathrooms=Decimal("5.0"),
            area=Decimal("4800.00"),
            status="available",
            agent_id=agent_marcus.id
        )
        db.add_all([prop1, prop2, prop3, prop4, prop5])
        db.commit()

        logger.info("Seeding completed successfully! 3 Agents, 3 Clients, and 5 Properties created.")
    except Exception as e:
        db.rollback()
        logger.error(f"Error seeding database: {e}")
        raise e
    finally:
        db.close()


if __name__ == "__main__":
    init_db()

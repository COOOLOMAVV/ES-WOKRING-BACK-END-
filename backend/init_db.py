#!/usr/bin/env python3
"""
Quick Database Initialization Runner
Run with:
    python init_db.py
"""
import sys
import os

# Ensure backend root is on Python module search path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from app.db.init_db import init_db

if __name__ == "__main__":
    print("Initializing Database & Seeding Sample Data...")
    init_db(seed_sample_data=True)
    print("Database ready for demonstration!")

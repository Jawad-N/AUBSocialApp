'''
This file creates the database
'''

from app import db
from app import app
print("hi")
with app.app_context():
    db.create_all()


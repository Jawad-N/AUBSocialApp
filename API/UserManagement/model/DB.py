from ..app import db, bcrypt, ma
from sqlalchemy.sql import func
import json

class User( db.Model ):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(30), nullable = False, unique = True)
    email = db.Column(db.String(30), nullable = False, unique = True)
    hashed_password = db.Column(db.String(128), nullable = False)
    def __init__(self, username, password, email):
        super(User, self).__init__(username=username, email=email)
        self.hashed_password = bcrypt.generate_password_hash(password)
    #A user has a password a username a handle inside the app and an id which will often be used as a foreign key

class UserSchema(ma.Schema):
    class Meta:
        fields = ("id", "username")
        model = User
        
user_schema = UserSchema()


class Friendship(db.Model):
    __tablename__ = 'friendship'
    id = db.Column(db.Integer, primary_key=True)
    user1_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable = False)
    user2_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable = False)
    #Friends are represented as a graph, these are the edges, the vertices are the users
    #It is a pairwise friendship
    def __init__( self, user1_id, user2_id ):
        self.user1_id = user1_id
        self.user2_id = user2_id

class FriendshipSchema( ma.Schema ):
    class Meta:
        fields = ("id", "user1_id", "user2_id")
        model = Friendship
friendship_schema = FriendshipSchema()
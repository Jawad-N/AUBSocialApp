from ..app import db, bcrypt, ma
from sqlalchemy.sql import func

#Common database components 

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

class Message(db.Model):
    #These are the msgs, to form a chat we just point these msgs to the corresponding chat witha foreign key
    __tablename__ = 'message'
    id = db.Column(db.Integer, primary_key=True)
    content = db.Column(db.Text)
    friendship_id = db.Column(db.Integer, db.ForeignKey('friendship.id'))
    timestamp = db.Column(db.DateTime, default=func.now())

class TutoringSession(db.Model):
    __tablename__ = 'tutoring_session'
    id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.Text)
    price = db.Column(db.Float())
    time = db.Column(db.DateTime)
    duration = db.Column(db.Integer)
    course = db.Column(db.Text)
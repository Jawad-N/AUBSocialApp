from ..app import db, bcrypt, ma
from sqlalchemy.sql import func
import json
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
    def __init__( self, user1_id, user2_id ):
        self.user1_id = user1_id
        self.user2_id = user2_id

class FriendshipSchema( ma.Schema ):
    class Meta:
        fields = ("id", "user1_id", "user2_id")
        model = Friendship
friendship_schema = FriendshipSchema()


class Message(db.Model):
    #These are the msgs, to form a chat we just point these msgs to the corresponding chat witha foreign key
    __tablename__ = 'message'
    id = db.Column(db.Integer, primary_key=True)
    content = db.Column(db.Text)
    friendship_id = db.Column(db.Integer, db.ForeignKey('friendship.id'))
    timestamp = db.Column(db.DateTime, default=func.now())


class Course(db.Model):
    __tablename__ = 'course'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable = False)
    section = db.Column(db.String(10), nullable = False)
    professor = db.Column(db.String(100), nullable = False)
    location = db.Column(db.String(100), nullable = False)
    room = db.Column(db.String(10), nullable = False)
    meeting_time = db.Column(db.Text, nullable = False)  # meeting time is a serialized json

    def __init__(self, name, section, professor, location, room, meeting_time):
        self.name = name
        self.section = section
        self.professor = professor
        self.location = location
        self.room = room
        self.meeting_time = json.dumps(meeting_time)

class CourseSchema(ma.Schema):
    class Meta:
        fields = ("id", "name", "section","professor", "location", "room", "meeting_time")
        model = Course

course_schema = CourseSchema()


class tutoringSession( db.Model ):
    __tablename__ = "tutoringSession"
    id = db.Column( db.Integer, primary_key=True )
    courseID = db.Column( db.Integer, db.ForeignKey("course.id"), nullable = False )
    description = db.Column( db.String(100), nullable = False )
    price = db.Column( db.Float, nullable = False )
    def __init__( self, courseID, description, price ):
        self.courseID = courseID
        self.description = description
        self.price = price


class tutoringSchema(ma.Schema):
    class Meta:
        fields = ("id", "courseID", "description","price")
        model = tutoringSession

tutoring_schema = tutoringSchema()


class courseFeedback( db.Model ):
    id = db.Column(db.Integer, primary_key=True)
    course_id = db.Column(db.Integer, db.ForeignKey('course.id'), nullable = False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    content = db.Column(db.Text, nullable = False)
    def __init__(self, course_id, user_id, content):
        super(courseFeedback, self).__init__(course_id=course_id, content=content, user_id=user_id)

class courseFeedbackSchema(ma.Schema):
    class Meta:
        fields = ("id", "course_id", "user_id", "content")
        model = courseFeedback
        
course_feedback_schema = courseFeedbackSchema()

from ..app import db, bcrypt, ma
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


class StudyGroup( db.Model ):
    id = db.Column( db.Integer, primary_key=True )
    group_name = db.Column( db.String(150), nullable = False, unique = True )
    creator_id = db.Column( db.Integer, db.ForeignKey("user.id"), nullable = False )
    description = db.Column( db.String(150), nullable = True )
    #potentially add groupChat
    def __init__(self, group_name, creator_id, description=None):
        self.group_name = group_name
        self.creator_id = creator_id
        self.description = description

class StudyGroupSchema( ma.Schema ):
    class Meta:
        fields = ("id", "group_name", "creator_id", "description")
        model = StudyGroup
group_schema = StudyGroupSchema()


class GroupMembership( db.Model ):
    id = db.Column( db.Integer, primary_key = True )
    member = db.Column( db.Integer, db.ForeignKey("user.id"), nullable = False )
    group = db.Column( db.Integer, db.ForeignKey("study_group.id"), nullable = False )
    def __init__(self, member, group):
        self.member = member
        self.group = group

class GroupMembershipSchema( ma.Schema ):
    class Meta:
        fields = ("id", "member", "group")
        model = GroupMembership
group_membership_schema = GroupMembershipSchema()


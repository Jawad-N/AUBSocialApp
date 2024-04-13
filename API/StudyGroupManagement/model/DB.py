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


class Group( db.Model ):
    id = db.Column( db.Integer, primary_key=True )
    group_name = db.Column( db.String(150), nullable = False, unique = True )
    creator = db.Column( db.Integer, db.ForeignKey("user.id"), nullable = False )
    description = db.Column( db.String(150), nullable = True )
    #potentially add groupChat
    def __init__(self, group_name, creator_id, description=None):
        self.group_name = group_name
        self.creator_id = creator_id
        self.description = description

class GroupSchema( ma.Schema ):
    class Meta:
        fields = ("id", "group_name", "creator", "description")
        model = Group
group_schema = GroupSchema()


class GroupMembership( db.Model ):
    id = db.Column( db.Integer, primary_key = True )
    member = db.Column( db.Integer, db.ForeignKey("user.id"), nullable = False )
    group = db.Column( db.Integer, db.ForeignKey("group.id"), nullable = False )
    def __init__(self, member_id, group_id):
        self.member_id = member_id
        self.group_id = group_id

class GroupMembershipSchema( ma.Schema ):
    class Meta:
        fields = ("id", "member", "group")
        model = GroupMembership
group_membership_schema = GroupMembershipSchema()


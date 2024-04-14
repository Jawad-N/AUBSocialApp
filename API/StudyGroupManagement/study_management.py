from flask import Blueprint, request, jsonify, abort
from .app import db
from .model.DB import User, StudyGroup, GroupMembership
from .model.DB import group_schema, group_membership_schema
import requests

study_management = Blueprint('studyGateway', __name__)

#API call that reutnrs all the groups, used to display the current groups
@study_management.route('/getGroups', methods = ["GET"])
def getGroups():
    groups = StudyGroup.query.all()
    groups = [group_schema.dump( group ) for group in groups]
    result = { "data": groups }
    return jsonify( result )



#Adding a groupmembership, i.e. a user to a group
#Steps are, take the group name and username
#Check these are valid, i.e. user and group found
#Check user isn't already part of the group
#Add the user to the group
@study_management.route('/addMember', methods = ["POST"])
def postMembership():
    data = request.json
    try:
        groupName = data["group"]
        userName = data["member"]
    except:
        abort( 400, "Missing required fields" )

    group = StudyGroup.query.filter_by( group_name = groupName ).first()

    if not group:
        return jsonify({"error": "Group not found"}), 404

    user = User.query.filter_by(username=userName).first()
    if not user:
        return jsonify({"error": "User not found"}), 404
   
    membership = GroupMembership.query.filter_by(member=user.id, group=group.id).first()
    print( group_membership_schema.dump(membership) )
    print( membership )
    if membership:
        print("here")
        return jsonify( group_membership_schema.dump(membership) ), 409
    

    new_membership = GroupMembership( member=user.id, group=group.id)
    db.session.add( new_membership )
    db.session.commit()

    serialized_membership = group_membership_schema.dump( new_membership )
    return jsonify( serialized_membership ), 201
    



#Adding a group
#check data fields
#check groupname and creator are valid
#create the group
#add the creator as a member
@study_management.route('/addGroup', methods = ["POST"])
def postGroup():
    data = request.json
    try:
        groupName = data["group_name"]
        creator = data["creator"]
        desc = data["description"]
    except:
        abort( 400, "Missing fields" )

    studygroup = StudyGroup.query.filter_by( group_name = groupName ).first()
    print( studygroup )
    if studygroup:
        return jsonify( {"error": "Group exists already"}, 409 )
    
    user = User.query.filter_by( username = creator ).first()
    if not user:
        return jsonify( {"error": "Creator not found in the user database"}, 404 )

    print( user.id )
    new_group = StudyGroup( group_name = groupName, creator_id = user.id, description = desc )
    db.session.add( new_group )
    db.session.commit()
    print( user.id, new_group.id ) 
    #Adding the membership of the creator
    new_membership = GroupMembership(member = user.id, group = new_group.id)
    db.session.add( new_membership )
    db.session.commit()

    serialized_group = group_schema.dump( new_group )
    return jsonify( serialized_group ), 201










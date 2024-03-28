from flask import Blueprint, request, jsonify, url_for, abort
from flask_mail import Mail,Message
import random
import string
import datetime
import jwt
from ..db_config import DB_CONFIG
from ..app import db, bcrypt, app  # Import the necessary objects directly from app
from ..model.DB import Course, course_schema, tutoringSession, tutoring_schema  # Adjust the path to import User and user_schema
from ..app import registered_users

course_management = Blueprint('courseGateway', __name__)

#API call that returns a list of all the courses in the database
#If you need any customized filtering it's easy to add
@course_management.route('/getCourses', methods = ["GET"])
def getCourses():
    courses = Course.query.all()
    courses = [course_schema.dump( course ) for course in courses]
    result = { "data": courses }
    return jsonify( result )

#Get a course by id
#Utility: for adding tutoring sessions having a course as  foreign key
@course_management.route('/getCourseById/<int:course_id>', methods=["GET"])
def getCourseById(course_id):
    course = Course.query.get(course_id)    
    if course:
        serialized_course = course_schema.dump(course)
        return jsonify(serialized_course)
    else:
        return jsonify({"error": "Course not found"}), 404



@course_management.route('/addTutoring', methods = ["POST"] )
def addTutoring():
    data = request.json

    try:
        courseID = data["courseID"]
        description = data["description"]
        price = data["price"]
    except:
        abort(400, "Missing required fields")

    tutoring_session = tutoringSession( courseID = courseID, description = description, price = price )
    db.session.add(tutoring_session)
    db.session.commit()
    serialized_tutoring_session = tutoring_schema.dump(tutoring_session)
    return jsonify(serialized_tutoring_session), 201  


@course_management.route('/getTutoring', methods=["GET"])
def getTutoring():
    tutoring_sessions = tutoringSession.query.all()
    print(tutoring_sessions)
    
    serialized_tutoring_sessions = [ tutoring_schema.dump(tutoring_session) for tutoring_session in tutoring_sessions ]
    result = { "data": serialized_tutoring_sessions }
    return jsonify(result)
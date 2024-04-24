from flask import Blueprint, request, jsonify, abort
from .app import db
from .model.DB import Course, course_schema, tutoringSession, tutoring_schema,courseFeedback,course_feedback_schema  # Adjust the path to import User and user_schema
import requests

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
    return jsonify( serialized_tutoring_sessions )


##COURSE FEEDBACK
@course_management.route('/addCourseFeedback', methods = ["POST"])
def addCourseFeedback():
    # Parse JSON payload from the request
    data = request.json
    headers = dict(request.headers)
    body = request.get_json()
    try:
        user = requests.post("http://127.0.0.1:5001/user/get_user", headers=headers, json=body).json()
        user_id = user["id"]
    except:
        user_id = None
    try:
        course_name = data['course_name']
        course_section = data['section']
        content = data['content']
    except KeyError as e:
        # If any field is missing in the JSON payload
        return jsonify({"error": f"Missing field: {str(e)}"}), 400

    # Find the course ID based on course name and section
    course = Course.query.filter_by(name=course_name, section=course_section).first()
    if not course:
        return jsonify({"error": "Course not found"}), 404

    feedback = courseFeedback(course_id=course.id, user_id=user_id, content=content)
    db.session.add(feedback)
    db.session.commit()

    # Assuming you have a feedback_schema for serializing Feedback objects
    serialized_feedback = course_feedback_schema.dump(feedback)
    return jsonify(serialized_feedback), 200

@course_management.route('/getFeedback', methods=["GET"])
def get_feedback():
    course_name = request.json.get('course_name', None)  # Get course name from query parameters, if provided

    # Start with a base query for all feedback
    feedback_query =courseFeedback.query.join(Course, Course.id == courseFeedback.course_id)

    if course_name:
        # If a course name is provided, filter the feedback for that course
        feedback_query = feedback_query.filter(Course.name == course_name)

    # Order the results by course ID
    feedback_query = feedback_query.order_by(courseFeedback.course_id)

    # Execute the query and fetch all results
    feedback_records = feedback_query.all()

    # Serialize the feedback records
    serialized_feedback = [course_feedback_schema.dump(feedback) for feedback in feedback_records]
    for feedback in serialized_feedback:
        # Add the course name to each feedback record
        feedback['course_name'] = Course.query.get(feedback['course_id']).name
        feedback['course_section'] = Course.query.get(feedback['course_id']).section
        feedback['professor'] = Course.query.get(feedback['course_id']).professor
    return jsonify(serialized_feedback)
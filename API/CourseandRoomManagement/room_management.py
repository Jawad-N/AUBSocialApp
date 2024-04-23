from flask import request, jsonify, Blueprint
from datetime import datetime
from .model.DB import db, Course 
import json

room_management = Blueprint('room_management', __name__)

@room_management.route('/find_empty_rooms', methods=['POST'])
def find_empty_rooms():
    building = request.json.get('building')
    day = request.json.get('day')
    start_time_input = request.json.get('start_time')  # Start of the time slot in HHMM format
    end_time_input = request.json.get('end_time')  # End of the time slot in HHMM format

    start_time = datetime.strptime(start_time_input, '%H%M').time()
    end_time = datetime.strptime(end_time_input, '%H%M').time()

    # Find all courses that are given in the building and on the day provided
    conflicting_courses = Course.query.filter(
        Course.location == building,
        Course.meeting_time.contains(day)
    ).all()

    # Find all rooms that are occupied during the time slot
    occupied_rooms = set()
    for course in conflicting_courses:
        if course.meeting_time is None:
            continue

        meeting_times=course.meeting_time
        meeting_times = meeting_times.replace("'", '"') # NEED DOUBLE QUOTES INSTEAD OF SINGLE FOR JSON.LOADS
        meeting_times= json.loads(meeting_times) # Convert the string to a dictionary

        session_start = datetime.strptime(meeting_times['start_time'], '%H%M').time()
        session_end = datetime.strptime(meeting_times['end_time'], '%H%M').time()
        if (start_time < session_start and session_start<end_time) or (start_time < session_end and session_end<end_time):
            occupied_rooms.add(course.room)

        
    all_rooms = get_all_rooms_in_building(building)

    # Determine the empty rooms
    empty_rooms = []
    empty_rooms = [room for room in all_rooms if (room not in occupied_rooms and room not in empty_rooms)]
    return jsonify(empty_rooms)

def get_all_rooms_in_building(building):
    # Get all rooms in the building specified
    room_query = db.session.query(Course.room).filter(Course.location == building).distinct()
    all_rooms = [room[0] for room in room_query.all()]
    return all_rooms


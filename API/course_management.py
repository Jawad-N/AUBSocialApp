from flask import Blueprint, request, jsonify, url_for, abort
from flask_mail import Mail,Message
import random
import string
import datetime
import jwt
from db_config import DB_CONFIG
from ..app import db, bcrypt, app  # Import the necessary objects directly from app
from ..model.DB import Course, course_schema  # Adjust the path to import User and user_schema
from ..app import registered_users

course_management = Blueprint('courseGateway', __name__)


@course_management.route('/getCourses', methods = ["GET"])
def getCourses():
    courses = Course.query.all()
    result = course_schema.dump(courses)
    return jsonify( result )

@course_management.route('/getCourseById', methods = ["GET"])
def getCourseById():
    return -1

@course_management.route('/addTutoring', methods = ['Post'] )
def addTutoring():
    return -1
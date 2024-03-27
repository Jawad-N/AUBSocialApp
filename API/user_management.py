from flask import Blueprint, request, jsonify, url_for, abort
from flask_mail import Mail,Message
import random
import string
import datetime
import jwt
from db_config import DB_CONFIG
from app import db, bcrypt, app  # Import the necessary objects directly from app
from model.DB import User, user_schema  # Adjust the path to import User and user_schema
from app import registered_users

from API.email_pass import emailPass

app.config['MAIL_SERVER'] = 'smtp.outlook.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USE_SSL'] = False
app.config['MAIL_USERNAME'] = 'aubsocialapp@outlook.com'
app.config['MAIL_PASSWORD'] = emailPass
mail = Mail(app)

# Create a blueprint for the gateway
user_management = Blueprint('gateway', __name__)


def generate_code(length=6):
    """Generate a random code of given length."""
    letters_and_digits = string.ascii_letters + string.digits
    return ''.join(random.choice(letters_and_digits) for i in range(length))

def check_email_end(input_string):
    if input_string.endswith("@mail.aub.edu"):
        return True
    else:
        return False

@user_management.route('/register', methods=['POST'])
def register():
    try:
        username = request.json['username']
        email = request.json['email']
        password = request.json['password']
    except:
        return jsonify({"error": "Missing values or values with incorrect names in the json"}), 400
    if(username == None or password == None or email is None):
        return jsonify({"error": "Values given should not be empty"}), 400
    if(len(username) > 30):
        return jsonify({"error": "username is too long"}), 400
    if(len(username) < 3):
        return jsonify({"error": "username is too short"}), 400
    user = User.query.filter_by(email=email).first()
    if user is not None:
        return jsonify({"error": "email already used"}), 409
    user = User.query.filter_by(username=username).first()
    if user is not None:
        return jsonify({"error": "username already used"}), 409  
    if not check_email_end(email):  
        return jsonify({"error": "Use your AUB email."}), 403
    code = generate_code()
    msg = Message('AUB Social App One-time Code Verification', sender='aubsocialapp@outlook.com', recipients=[email])
    msg.body = f'Your one-time code is: {code}'
    try:
        mail.send(msg)
    except Exception as e:
        return jsonify({"error": "Internal Error"}), 400
    registered_users[email] = [code, username, password]
    return jsonify({'redirect': url_for('gateway.verify_code', email=email)}), 200


@user_management.route('/verify/<email>', methods=['POST'])
def verify_code(email):
    user_code = request.json['code']
    if user_code == registered_users.get(email)[0]:
        user = User(username=registered_users.get(email)[1], email=email, password=registered_users.get(email)[2])
        db.session.add(user)
        db.session.commit()
        return jsonify(user_schema.dump(user)), 200
    else:
        return jsonify({"error": "invalid code"}), 403


def create_token(user_id):
    payload = {
        'exp': datetime.datetime.utcnow() + datetime.timedelta(days=4),
        'iat': datetime.datetime.utcnow(),
        'sub': user_id
    }
    return jwt.encode(
        payload,
        DB_CONFIG,
        algorithm='HS256'
    )


@user_management.route('/authentication', methods=['POST'])
def authenticateUser():
    try:
        username = request.json['username']
        password = request.json['password']
    except:
        abort(400)
    user = User.query.filter_by(username=username).first()
    if(user is None):
        abort(403)
    if not(bcrypt.check_password_hash(user.hashed_password, password)):
        abort(403)
    token = create_token(user.id)
    return jsonify({"token": token}), 200

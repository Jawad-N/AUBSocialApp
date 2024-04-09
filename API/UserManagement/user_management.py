from flask import Blueprint, request, jsonify, url_for, abort
from flask_mail import Mail,Message
import random
import string
import datetime
import jwt

from .db_config import DB_CONFIG
from .model.DB import User, user_schema, Friendship, friendship_schema  # Adjust the path to import User and user_schema
from .email_pass import emailPass
from .app import db, bcrypt, app,registered_users  # Import the necessary objects directly from app



app.config['MAIL_SERVER'] = 'smtp.outlook.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USE_SSL'] = False
app.config['MAIL_USERNAME'] = 'aubsocialapp@outlook.com'
app.config['MAIL_PASSWORD'] = emailPass
mail = Mail(app)

# Create a blueprint for the gateway
user_management = Blueprint('gateway', __name__)

#THIS FUNCTION GENERATES 6 DIGIT VERIFICATION CODE
def generate_code(length=6):
    """Generate a random code of given length."""
    letters_and_digits = string.ascii_letters + string.digits
    return ''.join(random.choice(letters_and_digits) for i in range(length))

#THIS FUNCTION VERIFY AUB EMAIL
def check_email_end(input_string):
    if input_string.endswith("@mail.aub.edu"):
        return True
    else:
        return False

@user_management.route('/')
def hello_world():
    return "Hello World"

#THIS FUNCTION VALIDATE USER REGISTER INFORMATION
@user_management.route('/register', methods=['POST'])
def register():
    try:
        print(request.json)
        username = request.json['username']
        email = request.json['email']
        password = request.json['password']
    except:
        print(1)
        return jsonify({"error": "Missing values or values with incorrect names in the json"}), 400
    #Check if input is valid
    if(username == None or password == None or email is None):
        print(2)
        return jsonify({"error": "Values given should not be empty"}), 400
    if(len(username) > 30):
        print(3)
        return jsonify({"error": "username is too long"}), 400
    if(len(username) < 3):
        print(4)
        return jsonify({"error": "username is too short"}), 400
    
    #Check if email and username are registered in database
    user = User.query.filter_by(email=email).first()
    if user is not None:
        return jsonify({"error": "email already used"}), 409
    user = User.query.filter_by(username=username).first()
    if user is not None:
        return jsonify({"error": "username already used"}), 409  
    
    #Check if email is AUB email
    if not check_email_end(email):  
        return jsonify({"error": "Use your AUB email."}), 403
    
    #Send verification code to email given
    code = generate_code()
    msg = Message('AUB Social App One-time Code Verification', sender='aubsocialapp@outlook.com', recipients=[email])
    msg.body = f'Your one-time code is: {code}'
    try:
        mail.send(msg)
    except Exception as e:
        return jsonify({"error": "Internal Error"}), 400
    registered_users[email] = [code, username, password]
    return jsonify({'redirect': url_for('gateway.verify_code', email=email)}), 200

#THIS FUNCTION VERIFIES AND ADDS USER TO DATABASE
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

# THIS FUNCTION GENERATES A JSON WEB TOKEN FOR A USER
def create_token(user_id):
    # create a payload containing issue & expiration date of user token
    payload = {
        'exp': datetime.datetime.utcnow() + datetime.timedelta(days=4),
        'iat': datetime.datetime.utcnow(),
        'sub': user_id
    }
    # encode payload into a JSON web token and sign it to prevent tampering
    return jwt.encode(
        payload,
        DB_CONFIG,
        algorithm='HS256'
    )

# THIS FUNCTION AUTHENTICATES THE USER INTO THE APP
@user_management.route('/authentication', methods=['POST'])
def authenticateUser():
    try:
        username = request.json['username']
        password = request.json['password']
    except:
        abort(400)
    #Check if user is registered in database
    user = User.query.filter_by(username=username).first()
    if(user is None):
        abort(403)
    #Check if user password is correct
    if not(bcrypt.check_password_hash(user.hashed_password, password)):
        abort(403)
    
    #Assign user JWT to maintain user session
    token = create_token(user.id)
    return jsonify({"token": token}), 200

def get_current_user_id():
    token = request.headers.get('Authorization')
    if not token:
        return None
    try:
        payload = jwt.decode(token, DB_CONFIG, algorithms=['HS256'])
        return payload['sub']
    except jwt.ExpiredSignatureError:
        return None
    except jwt.InvalidTokenError:
        return None

def extract_auth_token(authenticated_request):
    auth_header = authenticated_request.headers.get('Authorization')
    if auth_header:
        return auth_header.split(" ")[1]
    else:
        return None


def decode_token(token):
    try:
        payload = jwt.decode(token, DB_CONFIG, 'HS256')
        return payload['sub']
    except:
        return None

@user_management.route('/add_friend', methods=['POST'])
def add_friend():
    token = extract_auth_token( request )
    user_id = decode_token( token )
    try:
        user_2 = User.query.filter_by(username= request.json['username_2']).first()
        user_2_id = user_2.id
    except:
        return jsonify({"error": "Invalid Input"}), 401
    
    
    if not user_2_id:
        return jsonify({"error": "User not authenticated"}), 401

    if user_id == user_2_id:
        return jsonify({"error": "Cannot add yourself as a friend"}), 400

    existing_friendship = Friendship.query.filter(
        (Friendship.user1_id == user_id and Friendship.user2_id == user_2_id) |
        (Friendship.user1_id == user_2_id and Friendship.user2_id == user_id)
    ).first()
    if existing_friendship!=None:
        return jsonify({"error": "Friends already added"}), 401
    # Users are not friends, add the friendship
    l = [user_id, user_2_id]
    l.sort()
    new_friendship = Friendship(user1_id=l[0], user2_id=l[1])
    db.session.add(new_friendship)
    db.session.commit()
    return friendship_schema.dump(new_friendship), 200

resetting_password = {}

#RESET PASSWORD 
@user_management.route('/request_password_reset', methods=['POST'])
def request_password_reset():
    email = request.json.get('email')
    user = User.query.filter_by(email=email).first()
    if not user:
        return jsonify({'error': 'User not found.'}), 404

    # Generate a random verification code
    verification_code = ''.join(random.choices(string.ascii_letters + string.digits, k=6))
    resetting_password[email] = verification_code

    # Send the verification code via email
    msg = Message('Your Password Reset Code', sender='aubsocialapp@outlook.com', recipients=[email])
    msg.body = f'Your password reset code is: {verification_code}'
    try:
        mail.send(msg)
    except Exception as e:
        
        return jsonify({"error": "Internal Error"}), 400
    return jsonify({'message': 'A password reset code has been sent to your email.'}), 200

@user_management.route('/reset_password', methods=['POST'])
def reset_password():
    email = request.json.get('email')
    code = request.json.get('code')
    new_password = request.json.get('new_password')
    user = User.query.filter_by(email=email).first()
    user_code = resetting_password[email] 
    if user and user_code == code:
        # Hash and update the new password
        hashed_password = bcrypt.generate_password_hash(new_password)
        user.hashed_password = hashed_password
        resetting_password.pop(email)  # Clear the reset code
        db.session.commit()
        return jsonify({'message': 'Your password has been successfully reset.'}), 200
    else:
        return jsonify({'error': 'Invalid email or reset code.'}), 400
    
@user_management.route('/get_user', methods=['POST'])
def get_user():
    token = extract_auth_token(request)
    user_id = decode_token( token )
    if user_id==None:
        return None, 200
    user = User.query.filter_by(id=user_id).first()
    if user is not None:
        return jsonify(user_schema.dump(user)), 200
    return None, 200
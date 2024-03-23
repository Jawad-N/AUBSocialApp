from flask import Flask, request, abort, url_for, jsonify
from flask_mail import Mail, Message
from flask_cors import CORS
import random
import string
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_marshmallow import Marshmallow
import datetime
import jwt
from db_config import DB_CONFIG


app = Flask(__name__)


app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:0000@localhost:3306/AUBSocialApp'
CORS(app)
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
ma = Marshmallow(app)

from .model.DB import User, user_schema

# Flask-Mail configuration
app.config['MAIL_SERVER'] = 'smtp.gmail.com'  # replace with your email provider's SMTP server
app.config['MAIL_PORT'] = 587
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USE_SSL'] = False
app.config['MAIL_USERNAME'] = ''  # replace with your email address
app.config['MAIL_PASSWORD'] = 'yourpassword'  # replace with your email password

mail = Mail(app)

# Temporary storage for registered users and their codes
registered_users = {}

def generate_code(length=6):
    """Generate a random code of given length."""
    letters_and_digits = string.ascii_letters + string.digits
    return ''.join(random.choice(letters_and_digits) for i in range(length))

@app.route('/register', methods=['POST'])
def register():
    try:
        username = request.json['username']
        email = request.json['email']
        password = request.json['password']
    except:
        return jsonify({"error":"Missing values or values with incorrect names in the json"}), 400
    if(username == None or password == None or email is None):
        return jsonify({"error":"Values given should not be empty"}), 400
    if(len(username)>30):
        return jsonify({"error":"username is too long"}), 400
    if(len(username)<3):
        return jsonify({"error":"username is too short"}), 400
    user = User.query.filter_by(email=email).first()
    if user is not None:
        return jsonify({"error":"email already used"}), 409
    user = User.query.filter_by(username=username).first()
    if user is not None:
        return jsonify({"error":"username already used"}), 409    
    code = generate_code()
    print(code)
    registered_users[email] = [code, username, password]
    return jsonify({'redirect': url_for('verify_code', email=email)}), 200
    
    
    #return jsonify(user_schema.dump(user)), 200
    
    # Generate a one-time code
      
    """
    # Store the email and its associated code
    
    # Send email
    msg = Message('One-time Code Verification', sender='youremail@example.com', recipients=[email])
    msg.body = f'Your one-time code is: {code}'
    mail.send(msg)
    
    flash('A one-time code has been sent to your email.')
    """
    

@app.route('/verify/<email>', methods=['POST'])
def verify_code(email):
    user_code = request.json['code']
    
    if user_code == registered_users.get(email)[0]:
        user = User(username=registered_users.get(email)[1], email=email, password=registered_users.get(email)[2])
        
        # Add the new user to the database
        db.session.add(user)
        db.session.commit()
        return jsonify(user_schema.dump(user)), 200
    else:
        return jsonify({"error":"invalid code"}), 403


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


@app.route('/authentication', methods = ['POST'])
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
    return jsonify({"token":token}), 200

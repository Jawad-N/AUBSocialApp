'''
This file configures and defines the Flask application by initializing the database and incorporating the API routes blueprint.
'''

from flask import Flask
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from flask_bcrypt import Bcrypt

from .db_config import DB_CONFIG 

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = DB_CONFIG
CORS(app)
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
ma = Marshmallow(app)

from .chat_management import chat_management

app.register_blueprint(chat_management, url_prefix='/chat')

if __name__ == '__main__':
    app.run(debug=True, port = 5003)

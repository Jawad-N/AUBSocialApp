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
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:example_pass@172.19.0.2:3307/aubsocial'
CORS(app)
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
ma = Marshmallow(app)


from .course_management import course_management
from .room_management import room_management

app.register_blueprint(course_management, url_prefix='/course')
app.register_blueprint(room_management, url_prefix='/room')


if __name__ == '__main__':
    app.run(debug=True,port = 5002)

from flask import Flask
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
from flask_marshmallow import Marshmallow
from db_config import DB_CONFIG 
app = Flask(__name__)


app.config['SQLALCHEMY_DATABASE_URI'] = DB_CONFIG
CORS(app)
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
ma = Marshmallow(app)
registered_users = {}

from API.user_management import user_management

app.register_blueprint(user_management, url_prefix='/user')


if __name__ == '__main__':
    app.run(debug=True)

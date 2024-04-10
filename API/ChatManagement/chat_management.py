from flask import Blueprint, request, jsonify
from .model.DB import db, Message, Friendship  

chat_management = Blueprint('chat_management', __name__)

@chat_management.route('/send_message', methods=['POST'])
def send_message():
    # Assuming the request includes the IDs of the two users and the message content
    user1_id = request.json.get('user1_id')
    user2_id = request.json.get('user2_id')
    content = request.json.get('content')

    if not all([user1_id, user2_id, content]):
        return jsonify({"error": "Missing required fields"}), 400

    # Look for an existing friendship between the two users
    friendship = Friendship.query.filter(
        ((Friendship.user1_id == user1_id) & (Friendship.user2_id == user2_id)) |
        ((Friendship.user1_id == user2_id) & (Friendship.user2_id == user1_id))
    ).first()

    if not friendship:
        return jsonify({"error": "Friendship does not exist"}), 404

    # Create and save the new message tied to the found friendship
    new_message = Message(content=content, friendship_id=friendship.id)
    db.session.add(new_message)
    db.session.commit()

    return jsonify({"message": "Message sent successfully"}), 201


@chat_management.route('/get_messages/<int:user1_id>/<int:user2_id>', methods=['GET'])
def get_messages(user1_id, user2_id):
    # Look for an existing friendship between the two users
    friendship = Friendship.query.filter(
        ((Friendship.user1_id == user1_id) & (Friendship.user2_id == user2_id)) |
        ((Friendship.user1_id == user2_id) & (Friendship.user2_id == user1_id))
    ).first()

    if not friendship:
        return jsonify({"error": "No friendship found between the specified users"}), 404

    # Retrieve all messages linked to the identified friendship
    messages = Message.query.filter_by(friendship_id=friendship.id).order_by(Message.timestamp.asc()).all()

    return jsonify([{"content": msg.content, "timestamp": msg.timestamp.isoformat()} for msg in messages]), 200


##trying to implement socketio, not working yet 


# from flask import Flask
# from flask_sqlalchemy import SQLAlchemy
# from flask_socketio import SocketIO, emit
# import datetime


# app = Flask(__name__)
# app.config['SECRET_KEY'] = 'sharaf12345'
# app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:12345@localhost:3306/aubsocial'
# db = SQLAlchemy(app)
# socketio = SocketIO(app)

# from model.DB import Friendship, Message




# @socketio.on('private_message')
# def handle_private_message(data):
#     user1_id = data['sender']
#     user2_id = data['recipient']
#     content = data['content']
    
#     #Look for an existing friendship between the two users
#     friendship = Friendship.query.filter(
#         ((Friendship.user1_id == user1_id) & (Friendship.user2_id == user2_id)) |
#         ((Friendship.user1_id == user2_id) & (Friendship.user2_id == user1_id))
#     ).first()

#     if not friendship:
#         return 
    
#     # Create and save the new message tied to the found friendship
#     new_message = Message(content=content, friendship_id=friendship.id)

#     # Save the message
#     db.session.add(new_message)
#     db.session.commit()

#     emit('new_private_message', data, room=friendship.id)

# @socketio.on('load_messages')
# def load_messages(data):
#     user1_id = data['sender']
#     user2_id = data['recipient']
#     friendship = Friendship.query.filter(
#         ((Friendship.user1_id == user1_id) & (Friendship.user2_id == user2_id)) |
#         ((Friendship.user1_id == user2_id) & (Friendship.user2_id == user1_id))
#     ).first()
#     messages = Message.query.filter_by(friendship_id=friendship.id).order_by(Message.timestamp.asc()).all()

#     messages_list = [{'sender': message.sender, 'content': message.content, 'timestamp': message.timestamp.isoformat()} for message in messages]
#     emit('messages_loaded', messages_list)

# if __name__ == '__main__':
#     socketio.run(app, debug=True)

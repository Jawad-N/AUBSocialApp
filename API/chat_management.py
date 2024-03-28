from flask import Blueprint, request, jsonify
from ..model.DB import db, Message, Friendship  

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


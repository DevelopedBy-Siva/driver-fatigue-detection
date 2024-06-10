from datetime import datetime

from flask import Flask, request, jsonify
from flask_cors import CORS
from pymongo import MongoClient

app = Flask(__name__)
CORS(app, origins="*")

client = MongoClient('localhost', 27017)
db = client['drowsiness-detection']
driver_collection = db['drivers']


@app.route('/signup', methods=['POST'])
def signup():
    data = request.json

    name = data.get('name')
    email = data.get('email')
    password = data.get('password')

    if not (name and email and password):
        return jsonify({'error': 'Missing required fields.'}), 400

    email = email.lower()

    if driver_collection.find_one({'email': email}):
        return jsonify({'error': 'Email already exists! Try another one.'}), 400

    driver = {'name': name, 'email': email, 'password': password, 'journeys': []}
    driver_collection.insert_one(driver)
    return jsonify({'name': name, 'email': email, 'password': password}), 200


@app.route('/signin', methods=['POST'])
def signin():
    data = request.json

    email = data.get('email')
    password = data.get('password')

    if not (email and password):
        return jsonify({'error': 'Missing required fields.'}), 400

    email = email.lower()

    driver = driver_collection.find_one({'email': email, 'password': password})
    if driver:
        return jsonify({'name': driver["name"], 'email': email, 'password': password}), 200
    else:
        return jsonify({'error': 'Invalid email or password.'}), 404


@app.route('/data', methods=['POST'])
def receive_data():
    data = request.json
    email = data.get('user')
    journey_data = {
        'journey': data.get('journey'),
        'start_time': data.get('start_time'),
        'end_time': datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
        'data': data.get('data', [])
    }

    if not email or not journey_data:
        return jsonify({'error': 'Invalid data...'}), 400

    email = email.lower()

    driver = driver_collection.find_one({'email': email})
    if driver:
        driver_collection.update_one({'email': email}, {'$push': {'journeys': journey_data}})
        return jsonify({'message': 'Data stored...'}), 200
    else:
        return jsonify({'error': 'Driver not found...'}), 404


@app.route('/data', methods=['GET'])
def get_data():
    data = request.json
    email = data.get('email')
    if not email:
        return jsonify({'error': 'Invalid field...'}), 400

    email = email.lower()

    driver = driver_collection.find_one({'email': email})
    if driver:
        return jsonify({'journeys': driver["journeys"]}), 200
    else:
        return jsonify({'error': 'Driver not found...'}), 404


if __name__ == '__main__':
    app.run(debug=True, port=8080)

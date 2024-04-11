from flask import Flask, request, jsonify, abort
from pymongo import MongoClient
from flask_cors import CORS

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

    if driver_collection.find_one({'email': email}):
        return jsonify({'error': 'Email already exists! Try another one.'}), 400

    driver = {'name': name, 'email': email, 'password': password}
    driver_collection.insert_one(driver)
    return jsonify({'name': name, 'email': email, 'password': password}), 200


@app.route('/signin', methods=['POST'])
def signin():
    data = request.json

    email = data.get('email')
    password = data.get('password')

    if not (email and password):
        return jsonify({'error': 'Missing required fields.'}), 400

    driver = driver_collection.find_one({'email': email, 'password': password})
    if driver:
        return jsonify({'name': driver["name"], 'email': email, 'password': password}), 200
    else:
        return jsonify({'error': 'Invalid email or password.'}), 404


if __name__ == '__main__':
    app.run(debug=True, port=8080)

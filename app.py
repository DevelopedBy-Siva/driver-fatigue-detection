from flask import Flask, request, jsonify, abort
from pymongo import MongoClient

app = Flask(__name__)

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
        abort(400, 'Missing required fields.')

    if driver_collection.find_one({'email': email}):
        abort(400, 'Email already exists! Try another one.')

    driver = {'name': name, 'email': email, 'password': password}
    inserted_driver = driver_collection.insert_one(driver)
    return jsonify(driver), 201


@app.route('/signin', methods=['POST'])
def signin():
    data = request.json

    email = data.get('email')
    password = data.get('password')

    if not (email and password):
        abort(400, 'Missing required fields.')

    driver = driver_collection.find_one({'email': email, 'password': password})
    if driver:
        return jsonify(driver)
    else:
        abort(404, 'Invalid email or password.')


if __name__ == '__main__':
    app.run(debug=True)

from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
import json
import ast

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///food.db'
db = SQLAlchemy(app)


class Food(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    foodName = db.Column(db.String(1023), unique=True, nullable=False)
    foodCondData = db.Column(db.String(1023), nullable=False)

    def getDict(self):
        return dict(foodName=self.foodName, foodCondData=ast.literal_eval(self.foodCondData), id=self.id)


@app.route('/food/<foodID>', methods=['GET'])
def getFood(foodID):
    if foodID is None:
        return jsonify(status='Fail', error="id not specified"), 412
    food = Food.query.filter_by(id=int(foodID)).first()
    return jsonify(food.getDict())


@app.route('/searchFood/<querystr>', methods=['GET'])
def searchFood(querystr):
    if querystr is None:
        return jsonify(status='Fail', error="querystr not specified"), 412
    foodListRaw = Food.query.filter(Food.foodName.contains(querystr)).all()
    foodList = []
    for food in foodListRaw:
        foodList.append(dict(foodName=food.foodName, foodID=food.id))
    return jsonify(foodList=foodList)


@app.route('/food', methods=['POST'])
def addFood():
    meta = request.get_json()
    print(meta)
    newFood = Food(foodName=meta['foodName'], foodCondData=str(meta['foodCondData']))
    db.session.add(newFood)
    db.session.commit()
    return jsonify(status='OK', id=newFood.id), 200


if __name__ == "__main__":
    app.run()

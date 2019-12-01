import requests
from bs4 import BeautifulSoup
import json

searchIndexList = ['26', '25', '9', '27', '7', '28', '6', '31', '30', '5']
for searchIndex in searchIndexList:
    for p in range(1, 10):
        r = requests.get('https://www.stilltasty.com/searchitems/index/{}?page={}'.format(searchIndex, p))
        assert r.status_code == 200
        soup = BeautifulSoup(r.content.decode('UTF-8'), 'html.parser')
        searchList = soup.find_all('div', class_='search-list')[0]
        itemList = searchList.find_all('li')
        for item in itemList:
            try:
                itemName = item.a.text.lower()
            except:
                continue
            result = {'foodName': itemName}
            r = requests.get(item.a['href'])
            assert r.status_code == 200
            soup = BeautifulSoup(r.content.decode('UTF-8'), 'html.parser')
            foodCondDataList = soup.find_all('div', class_='food-storage-left')
            foodExpDataList = soup.find_all('div', class_='food-storage-right')
            condData = {}
            for i in range(len(foodCondDataList)):
                foodCond = foodCondDataList[i].span.text.strip().lower()
                foodExp = ' '.join(foodExpDataList[i].div.span.text.strip().split(' ')[:2]).lower()
                print(foodCond, foodExp)
                condData[foodCond] = foodExp
            result['foodCondData'] = condData
            print(json.dumps(result))
            r = requests.post('http://127.0.0.1:5000/food', json=result)

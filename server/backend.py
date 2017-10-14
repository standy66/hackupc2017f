from flask import Flask, request
import json
import pymongo

NAME = 'findmycar'

app = Flask(NAME)
cli = pymongo.MongoClient('localhost', 27017)
db = cli[NAME]


@app.route("/api", methods=['GET', 'POST'])
def api():
	try:
		r = json.loads(request.data)
		for hyp in r['hypotheses']:
			number = hyp['number']
			db.numbers.update({'_id': number}, {'longitude': float(r['longitude']), 'latitude': float(r['latitude']), 'confidence': float(hyp['confidence']), 'photo': hyp['photo']}, upsert=True)
	except:
		return "Format mismatch"
	return "ok"


@app.route("/debug", methods=['GET', 'POST'])
def debug():
	out = []
	for row in db.numbers.find():
		out.append(str(row))
	return "<br>\n".join(out)


@app.route("/clear", methods=['GET', 'POST'])
def clear():
	db.numbers.remove({})
	return ""


@app.route("/query", methods=['GET'])
def query():
	cur = db.numbers.find_one({'_id': str(request.args.get('number'))})
	if cur is None:
		result = {"status": False}
	else:
		del cur['_id']
		result = {'status': True, 'result': cur}
	return json.dumps(result)


if __name__ == "__main__":
    app.run('0.0.0.0', port=12345)


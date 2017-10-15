from flask import Flask, request, send_from_directory
import json
import pymongo
import argparse
import os


parser = argparse.ArgumentParser()
parser.add_argument("-d", "--debug", type=bool, help="debug mode", default=False)
args = parser.parse_args()

NAME = 'findmycar'

print('DEBUG: %d' % args.debug)

app = Flask(NAME, static_url_path=os.path.dirname(os.getcwd()))
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
	if not args.debug:
		return ""
	out = []
	for row in db.numbers.find():
		out.append(str(row))
	return "<br>\n".join(out)


@app.route("/clear", methods=['GET', 'POST'])
def clear():
	if args.debug:
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


@app.route('/')
def serve_static():
	return app.send_static_file('index.html')


@app.route('/favicon.png')
def favicon():
	return app.send_static_file('favicon.png')


@app.route('/style.css')
def style():
	return app.send_static_file('style.css')


@app.route('/main.js')
def mainjs():
	return app.send_static_file('main.js')


@app.route('/bg.png')
def bgpng():
	return app.send_static_file('bg.png')


if __name__ == "__main__":
    app.run('0.0.0.0', port=12345)



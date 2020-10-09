from flask import Flask, render_template, request
from youtube_search import YoutubeSearch

app = Flask(__name__)


@app.route('/', methods=['GET'])
def home():
    return render_template('home.html')


@app.route('/search/', methods=['GET'])
def searchFunction():
    searchTerm = request.args['term']
    apiKey = request.args['key']

    if apiKey == "APIKEY":
        results = YoutubeSearch(searchTerm, max_results=10).to_json()
        return results

    return "Unauthorised Request !"

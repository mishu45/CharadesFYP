from flask import Flask, jsonify, request
import os
import os.path
import spacy
import nlp
from nltk import word_tokenize
import csv
import pandas as pd
from moviepy.editor import VideoFileClip, concatenate_videoclips
import shutil
from flask import send_from_directory, abort
import base64
import string
import random



if not os.path.exists('static'):
    os.makedirs('static')
if not os.path.exists('venv/test/requestvideo'):
    os.makedirs('venv/test/requestvideo')
if not os.path.exists('venv/train/requestvideo'):
    os.makedirs('venv/train/requestvideo')

app = Flask(__name__)

def remove_punctuation(value):
    result = ""
    for c in value:
        # If char is not punctuation, add it to the result.
        if c not in string.punctuation:
            result += c
    return result

def Prediction(sentence):
    predictedSentence=''
    WriteFile(sentence.lower())
    os.system("python OpenNMT-py/translate.py -model model_step_3500.pt -src static/input.txt -output static/pred.txt -replace_unk -verbose")
    if os.path.exists('static/pred.txt'):
        return (ReadFile())


def WriteFile(sentence):
    f=open("static/input.txt","w")
    f.write(sentence)
    f.close()
    #print("---------------------Writing To File Saved in Directory-------------")
    f = open("static/input.txt", "rt")
    for eachLine in f:
        print(eachLine)
    f.close()



def ReadFile():
    #print("---------------------Reading From File Saved in Directory-------------")
    predictedSentence=''
    if os.path.exists('static/pred.txt'):
        if os.path.isfile('static/pred.txt'):
            try:
                f = open('static/pred.txt','rt')
                for eachSenetence in f:
                    predictedSentence=eachSenetence
                    f.close()
                    return predictedSentence

            except FileNotFoundError:
                print('The File not exit')

def ReadFile2():
    #print("---------------------Reading From File Saved in Directory-------------")
    predictedSentence=''
    if os.path.exists('static/sentence.txt'):
        if os.path.isfile('static/senttence.txt'):
            try:
                f = open('static/sentence.txt','rt')
                for eachSenetence in f:
                    predictedSentence=eachSenetence
                    f.close()
                    return predictedSentence

            except FileNotFoundError:
                print('The File not exit')


# GET
@app.route('/predicttext/<string:sentence>',methods=['GET'])
def Get_Predicted_Sentence(sentence):
    if request.method=='GET':
        #sentence = request.json['sentence']
        prediction_Result = Prediction(sentence=sentence)
        prediction_Result = prediction_Result.rstrip()
        os.remove('static/pred.txt')
        os.remove('static/input.txt')

        dir_name = "static/"
        test = os.listdir(dir_name)

        for item in test:
            if item.endswith(".mp4"):
                os.remove(os.path.join(dir_name, item))

        if os.path.exists('merge.mp4'):
            os.remove('merge.mp4')
        if os.path.exists('static/merge.mp4'):
            os.remove('static/merge.mp4')

        video = GetVideo(prediction_Result.upper())
        filename = "static/"+prediction_Result+".mp4"
        #dest = shutil.copyfile('merge.mp4', 'static/merge.mp4')
        good_referrer = 'http://{0}/'.format(request.host) + filename


    return jsonify({'prediction': prediction_Result,'videoPath': filename , 'videoUrl':good_referrer}), 200

# POST
@app.route('/predictvideo',methods=['GET', 'POST'])
def Get_Predicted_Video():
    if request.method=='POST':

        if os.path.exists('test/requestvideo/requestvideo.mov'):
            os.remove('test/requestvideo/requestvideo.mov')
        if os.path.exists('test/requestvideo'):
            shutil.rmtree('test/requestvideo')

        if not os.path.exists('test/requestvideo'):
            os.makedirs('test/requestvideo')

        video_request = request.values['video']
        videocode = bytes(video_request, 'utf-8')
        video_64_decode = base64.decodestring(videocode)
        video_result = open('test/requestvideo/requestvideo.mov', 'wb')
        video_result.write(video_64_decode)


        os.system("python main.py demo -c config/sl/demo.yaml --openpose openpose/build/ --video requestvideo.mov --output sentence.txt --device 0")
        

        prediction_Result = readfile2()
        print("Predicted word: ",prediction_Result)

    return jsonify({'prediction': prediction_Result}), 200


# driver function
if __name__ == '__main__':
    app.run(debug=True, port=5000, host='0.0.0.0')


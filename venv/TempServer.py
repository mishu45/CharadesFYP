from flask import Flask, jsonify, request
import os
import os.path
import spacy
import nlp

app = Flask(__name__)

def getSentence(ellipsis_text):
    nlp = spacy.load('en_core_web_sm')
    newText_str = ellipsis_text.lower()
    ellipsis_doc = nlp(newText_str)
    ellipsis_sentences = list(ellipsis_doc.sents)
    for sentence in ellipsis_sentences:
        return (sentence)

def Prediction(sentence):
    predictedSentence=''
    WriteFile(sentence.lower())
    os.system("python OpenNMT-py/translate.py -model model_step_3500.pt -src input.txt -output pred.txt -replace_unk -verbose")
    if os.path.exists('pred.txt'):
        return (ReadFile())


def WriteFile(sentence):
    f=open("input.txt","w")
    f.write(sentence)
    f.close()
    #print("---------------------Writing To File Saved in Directory-------------")
    f = open("input.txt", "rt")
    for eachLine in f:
        print(eachLine)
    f.close()



def ReadFile():
    #print("---------------------Reading From File Saved in Directory-------------")
    predictedSentence=''
    if os.path.exists('pred.txt'):
        if os.path.isfile('pred.txt'):
            try:
                f = open('pred.txt','rt')
                for eachSenetence in f:
                    predictedSentence=eachSenetence
                    f.close()
                    return predictedSentence

            except FileNotFoundError:
                print('The File not exit')

#prediction_Result = Prediction(sentence='My Name is Ramsha')
#print(prediction_Result)

# GET
@app.route('/predicttext/<string:sentence>',methods=['GET'])
def Get_Predicted_Sentence(sentence):
    if request.method=='GET':
        #sentence = request.json['sentence']
        prediction_Result = Prediction(sentence=sentence)
        prediction_Result = prediction_Result.rstrip()
        os.remove('pred.txt')
        os.remove('input.txt')

    return jsonify({'prediction': prediction_Result}), 200

# driver function
if __name__ == '__main__':
    app.run(debug=True, port=5000, host='0.0.0.0')

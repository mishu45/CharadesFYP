"""
Run a holdout set of data through our trained RNN. Requires we first
run train_rnn.py and save the weights.
"""
from rnn_utils import get_network, get_network_deep, get_network_wide, get_data
import tflearn
import os
import numpy as np


def main(filename, frames, batch_size, num_classes, input_length):
    """From the blog post linked above."""
    # Get our data.
    X_train, y_train = get_data(
        filename, frames, num_classes, input_length, False)
    # print X_train
    # print y_train

    # Get sizes.
    print ("Y train :- ", y_train[0])
    num_classes = len(y_train[0])

    # Get our network.
    net = get_network_wide(frames, input_length, num_classes)

    dir = os.path.join(os.getcwd(), "checkpoints")
    if not os.path.exists(dir):
        os.makedirs(dir)
    print ("HELLO1")
    # Get our model.
    model = tflearn.DNN(net, tensorboard_verbose=0)
    model.load('checkpoints/rnn.tflearn',weights_only="true")
    
    print ("HELLO2")

    # Evaluate.
    hc = model.predict(X_train)
    print ("HELLO3")
    hc = [np.argmax(every) for every in hc]
    print ("HELLO4")
    aadi = [np.argmax(every) for every in y_train]
    print ("HELLO5")
    print ("l1 :", len(aadi))
    print ("HELLO6")
    print ("l2 ", len(hc))
    print ("HELLO7")
    answer = []

    for i in range(0, len(hc)):
        answer.append([aadi[i], hc[i]])
    
    print ("HELLO8")

    answer.sort()
    print ("HELLO9")
    f = open("results.txt", "wb")
    print ("HELLO10")
    for x in answer:
        print (x[0], x[1])
        f.write(str(x[0])+" "+str(x[1])+"\n")
    
    print ("HELLO11")

    print(model.evaluate(X_train, y_train))
    

if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--method',
        type=str,
        default=None,
        help="""\
        Specify the method used: 'prediction' or 'pool'.\
        """
        )
    args = parser.parse_args()
    if args.method in ["prediction", "pool"]:
        print ("HELLO12")
        if args.method == "prediction":
            filename = '/home/jupyter/Sign-Language/charades-dataset/data/predicted-frames-2.pkl'
            input_length = 64
            print ("HELLO13")
        elif args.method == "pool":
            filename = '/home/jupyter/Sign-Language/charades-dataset/data/cnn-features-frames-2.pkl'
            input_length = 2048
            print ("HELLO14")
        frames = 201
        batch_size = 32
        num_classes = 64
        print ("HELLO15")

        main(filename, frames, batch_size, num_classes, input_length)
    else:
        print("Invalid input! Use 'python extract_frames.py -h' for more info.")
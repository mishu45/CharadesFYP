import cv2
import os
import pickle
from os.path import join, exists
import handsegment as hs
hc = []
type = 0


def convert(dataset):
    global type
    fcount = 0
    rootPath = os.getcwd()
    dirName = "train_frames" if type == 1 else "test_frames"
    majorData = os.path.join(os.getcwd(), dirName)
    if (not exists(majorData)):
        os.makedirs(majorData)
    dataset = os.path.join(os.getcwd(), dataset)
    os.chdir(dataset)
    x = os.listdir(os.getcwd())

    for gesture in x:
        adhyan = gesture
        gesture = os.path.join(dataset, gesture)
        os.chdir(gesture)
        frames = os.path.join(majorData, adhyan)
        if(not os.path.exists(frames)):
            os.makedirs(frames)
        videos = os.listdir(os.getcwd())
        videos = [video for video in videos if(os.path.isfile(video))]

        for video in videos:
            name = os.path.abspath(video)
            fcount = fcount+1
            print(fcount, " : ", name)
            cap = cv2.VideoCapture(name)  # capturing input video
            frameCount = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
            print(frameCount)
            count = 0
            os.chdir(frames)
            lastFrame = None
            while(1):
                ret, frame = cap.read()  # extract frame
                if ret is False:
                    break
                framename = os.path.splitext(video)[0]
                framename = framename+"_frame_"+str(count)+".jpeg"
                hc.append([join(frames, framename), adhyan, frameCount])

                if(not os.path.exists(framename)):
                    frame = hs.handsegment(frame)
                    frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
                    lastFrame = frame
                    cv2.imwrite(framename, frame)

                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
                count += 1
            while(count < 201):
                framename = os.path.splitext(video)[0]
                framename = framename+"_frame_"+str(count)+".jpeg"
                hc.append([join(frames, framename), adhyan, frameCount])
                if(not os.path.exists(framename)):
                    cv2.imwrite(framename, lastFrame)
                count += 1

            os.chdir(gesture)
            cap.release()
            cv2.destroyAllWindows()
    print(hc)
    # for frame in hc:
    #     print frame
    os.chdir(rootPath)
    dataDir = os.path.join(os.getcwd(), "data")
    if (not exists(dataDir)):
        os.makedirs(dataDir)
    path = 'data/labeled-frames-1.pkl' if type == 1 else 'data/labeled-frames-2.pkl'
    with open(path, 'wb') as handle:
        pickle.dump(hc, handle, protocol=pickle.HIGHEST_PROTOCOL)

import argparse
parser = argparse.ArgumentParser()
parser.add_argument(
        '--input_type',
        type=str,
        default=None,
        help="""\
        Specify the type of data: 'train' or 'test'.\
        """
        )
args = parser.parse_args()
if args.input_type == "train":
    type = 1
    convert("train/")
    print("Extracting frames for training")
elif args.input_type == "test":
    type = 2
    convert("test/")
    print("Extracting frames for testing")
else:
    print("Invalid input! Use 'python extract_frames.py -h' for more info.")
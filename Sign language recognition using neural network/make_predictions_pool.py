"""
Go through all of our images and save out the pool layer
representation of our images. This is not a prediction, but rather the
convolutional representation of features that we can then pass to an
RNN. The idea is that later we'll add an RNN layer directly onto
the CNN. This gives us a way to test if that's a good strategy
before putting in the work required to do so.
"""
import tensorflow as tf
import sys
import pickle
from tqdm import tqdm


def predict_on_frames(frames, batch):
    # Unpersists graph from file
    with tf.gfile.FastGFile("/home/jupyter/Sign-Language/charades-dataset/retrained_graph.pb", 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        _ = tf.import_graph_def(graph_def, name='')

    with tf.Session() as sess:
        pool_tensor = sess.graph.get_tensor_by_name('pool_3:0')

        # Moving this into the session to make it faster.
        cnn_features = []
        # image_path = 'images/' + batch + '/'
        pbar = tqdm(total=len(frames))
        for i, frame in enumerate(frames):
            filename = frame[0]
            label = frame[1]

            # For some reason, we have a tar reference in our pickled file.
            if 'tar' in filename:
                continue

            # Get the image path.
            image = filename

            # Read in the image_data
            image_data = tf.gfile.FastGFile(image, 'rb').read()

            try:
                cnn_representation = sess.run(pool_tensor,
                                              {'DecodeJpeg/contents:0': image_data})
            except KeyboardInterrupt:
                print("You quit with ctrl+c")
                sys.exit()
            except:
                print("Error making prediction, continuing.")
                continue

            # Save it out.
            frame_row = [cnn_representation, label]
            cnn_features.append(frame_row)

            if i > 0 and i % 10 == 0:
                pbar.update(10)

        pbar.close()

        return cnn_features


def main(type):
    batch = 1 if type == "train" else 2

    print("Doing batch %s" % batch)
    with open('data/labeled-frames-' + str(batch) + '.pkl', 'rb') as fin:
        frames = pickle.load(fin)

    # Build the convoluted features for this batch.
    cnn_features = predict_on_frames(frames, batch)

    # Save it.
    with open('data/cnn-features-frames-' + str(batch) + '.pkl', 'wb') as fout:
        pickle.dump(cnn_features, fout)

    print("Done.")

if __name__ == '__main__':
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
    if args.input_type in ["train", "test"]:
        main(args.input_type)
    else:
        print("Invalid input! Use 'python extract_frames.py -h' for more info.")

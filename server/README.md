# Charades_FlaskServer

## Steps to Install this project:

1-Move to you directory folder


2-cd venv


3- Install OpenNMT-py:
```bash
git clone https://github.com/OpenNMT/OpenNMT-py.git 
```


4-Make another copy of onmt folder and place it under venv


5- Install setup for OpenNMT:
```bash
cd OpenNMT-py
python setup.py install
```



6- Install the other requirments:
```bash
pip install -r requirements.opt.txt
```

7- Move to venv:
```bash
cd..
```

8-Install Pytorch, torchvision and torchtext 
(Note: Currently we only support pytorch version 1.4.0, torchvision version 0.4.0 and torchtext version 0.4.0)
```bash
pip install torch==1.4.0+cpu torchvision==0.4.0+cpu -f https://download.pytorch.org/whl/torch_stable.html
pip install torchtext==0.4.0
```

9-Install Configargparse:
```bash
pip install configargparse
```

10- Some features requires python 3.6 or higher


11- Dowload model file and save it under venv
```bash
https://drive.google.com/open?id=1bMHwz_ADFO0JGAD8M5YXCRGxeNY8qKc6
```


To start the flask server follow the following steps
Run Flask Server:

	1) cd Your_Project_Path/venv/Scripts
	2)  activate

Virtual Environment is now primary.

	3) cd ..
	4) set FLASK_APP=./TempServer.py
	5) flask run -h 0.0.0.0 -p 5000

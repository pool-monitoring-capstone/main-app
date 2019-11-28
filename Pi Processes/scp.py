import os

PATH = '/home/pi/dev/frames/'
known_files = []

#new_file_num = 0

for r, d, f, in os.walk(PATH):
    known_files = f

try:
    while True:
        #new_file = "image" + str(new_file_num) + ".jpeg"    
        for r, d, f, in os.walk(PATH):
            for file in f:
                if file not in known_files:
                    known_files.append(file)
                    
                    command = "scp -i ~/.ssh/the-final-key.pem "+ PATH + file + " ubuntu@3.88.34.147:~/frames/"
                    os.system(command)
except KeyboardInterrupt:
    pass

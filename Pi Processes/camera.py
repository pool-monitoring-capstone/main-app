import os
from time import sleep

count = 0

try:
    while True:
        os.system('cp /tmp/stream/pic.jpg /home/pi/dev/frames/image' + str(count) + '.jpg')
        sleep(3)
        count+=1
except KeyboardInterrupt:
    pass

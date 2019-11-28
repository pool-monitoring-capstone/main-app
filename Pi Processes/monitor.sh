#!/bin/bash
mkdir /tmp/stream
rm frames/*
raspistill -w 1024 -h 768 -n -q 4 -o /tmp/stream/pic.jpg -tl 100 -t 9999999 -th 0:0:0 &

sleep 2
python camera.py &

python scp.py &

cd ../mjpg-streamer/mjpg-streamer-experimental/
LD_LIBRARY_PATH=./ ./mjpg_streamer -i "input_file.so -f /tmp/stream -n pic.jpg" -o "output_http.so -w ./www"

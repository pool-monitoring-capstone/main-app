# -*- coding: utf-8 -*-
"""
Created on Wed May 15 15:20:29 2019

@author: Callon
"""

#Image detection for 2-radar system.
#Using codebase described at: https://www.pyimagesearch.com/2015/11/16/hog-etectmultiscale-parameters-explained/
from __future__ import print_function
import cv2
import os
import tensorflow as tf
from centroidtracker import CentroidTracker
#import sendsms as sms

import datetime
import time

timestamp = datetime.datetime.fromtimestamp(time.time()-18000)

sms_message = "POOL MONITOR ALERT\n\nPotential drowning in your pool at " + str(timestamp) + "\n\nConsider alerting appropriate authorities."


cv2.setUseOptimized(True)

ct = CentroidTracker()

computedImages = []
num_people = 0
last_num_people = 0

set_num_people = 0

start_count = False
num_images = 0

#Clear Frames folder
#os.system("rm ../frames/*")


# Read the graph.
with tf.compat.v1.gfile.FastGFile('frozen_inference_graph.pb', 'rb') as f:
    graph_def = tf.compat.v1.GraphDef()
    graph_def.ParseFromString(f.read())
    

with tf.compat.v1.Session() as sess:
    # Restore session
    sess.graph.as_default()
    tf.compat.v1.import_graph_def(graph_def, name='')
    
    directory = "frames2\\"
    #directory = "D:\\COOPResearch\\Fen Data\\20190531depth\\"
    count = 0
    try:
        while True:
            for filename in os.listdir(directory):
                #if filename.endswith(".png") or filename.endswith(".jpg"):
                if filename not in computedImages:
                    #print(os.path.join(directory, filename))
                    image_np = cv2.imread(os.path.join(directory, filename))
                    if image_np is None:
                        break;
      
                    #img = Image.fromarray(image_np, 'RGB')
                    rows = image_np.shape[0]
                    cols = image_np.shape[1]
                    inp = cv2.resize(image_np, (300, 300))
                    inp = inp[:, :, [2, 1, 0]]  # BGR2RGB
                    
                    
                    # Run the model
                    out = sess.run([sess.graph.get_tensor_by_name('num_detections:0'),
                                    sess.graph.get_tensor_by_name('detection_scores:0'),
                                    sess.graph.get_tensor_by_name('detection_boxes:0'),
                                    sess.graph.get_tensor_by_name('detection_classes:0')],
                                   feed_dict={'image_tensor:0': inp.reshape(1, inp.shape[0], inp.shape[1], 3)})
                    
                    rects =  []
                    # Visualize detected bounding boxes.
                    num_detections = int(out[0][0])
                    last_num_people = num_people
                    num_people = 0
                    for i in range(num_detections):
                        classId = int(out[3][0][i])
                        score = float(out[1][0][i])
                        bbox = [float(v) for v in out[2][0][i]]
                        print(classId)
                        if score > 0.3 and classId == 77:
                            num_people += 1
                            x = bbox[1] * cols
                            y = bbox[0] * rows
                            right = bbox[3] * cols
                            bottom = bbox[2] * rows
                            box = [x, y, right, bottom]
                            rects.append(box)
                            cv2.rectangle(image_np, (int(x), int(y)), (int(right), int(bottom)), (125, 255, 51), thickness=2)
                
                    # update our centroid tracker using the computed set of bounding
                    #box rectangles
                    if not start_count:
                        if num_people < last_num_people:
                            start_count = True
                            set_num_people = last_num_people
                            
                    if start_count:
                        num_images += 1
                        if num_images == 15:
                            #sms.send_sms("1234", sms_message)
                            num_images = 0
                        elif num_people >= set_num_people:
                            start_count = False
                            set_num_people = 0
                            num_images = 0
                    
                    objects = ct.update(rects)
                    # loop over the tracked objects
                    for (objectID, centroid) in objects.items():
                            # draw both the ID of the object and the centroid of the
                            # object on the output frame
                            #text = "Person {}".format(objectID + 1)
                            #Temp Fix Below
                            text = "Person{}".format(objectID + 1)
                            cv2.putText(image_np, text, (centroid[0] - 10, centroid[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2) 
                            cv2.circle(image_np, (centroid[0], centroid[1]), 4, (0, 255, 0), -1)
                    
                    cv2.imshow("test",image_np)
                    computedImages.append(filename)
                    print("Computed " + filename + ". Number of detections: " + str(num_detections))
            count += 1
    except KeyboardInterrupt:
        pass
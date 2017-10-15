#! /usr/bin/env python

import sys
from openalpr import Alpr
import cv2
import numpy as np
import requests
import base64

alpr = Alpr("eu", "/etc/openalpr/openalpr.conf", "/etc/openalpr/runtime_data")
if not alpr.is_loaded():
    print("Error loading OpenALPR")
    sys.exit(1)

alpr.set_top_n(20)
alpr.set_default_region("md")

capture = cv2.VideoCapture("http://10.42.0.1:8081/videoView")
# is_to_send = 0
while(True):
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

    frame = None
    while frame is None:
        et, frame = capture.read()

    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    print(rgb_frame.shape)
    print("processing frame")

    ret, image = cv2.imencode('.bmp', rgb_frame)
    results = alpr.recognize_array(bytes(bytearray(image)))
    print(results)
    print("done")

    i = 0
    hypotheses = []
    for plate in results['results']:
        i += 1
        print("Plate #%d" % i)
        print("   %12s %12s" % ("Plate", "Confidence"))
        for j, candidate in enumerate(plate['candidates']):
            if j > 3:
                break

            prefix = "-"
            if candidate['matches_template']:
                prefix = "*"
            print("  %s %12s%12f" % (prefix, candidate['plate'], candidate['confidence']))

            height, width = image.shape[:2]
            image_resized = cv2.resize(image, (height, width)) 
            hypotheses.append({"number": candidate["plate"],
                               "confidence": candidate['confidence'],
                               "photo": base64.b64encode(image_resized)})
            print(candidate["plate"], candidate["confidence"])

    if len(hypotheses) == 0:
        continue

    r = requests.post("http://findmycar.standy.me/api",
            json={
                "latitude": 41.387427, 
                "longitude": 2.112993, 
                "hypotheses": hypotheses
                }
            )
    print(r.text)
    break

# Call when completely done to release memory
alpr.unload()
capture.release()
cv2.destroyAllWindows()


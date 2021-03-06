#! /usr/bin/env python

import sys
from openalpr import Alpr
import cv2
import numpy as np
import requests
import base64

OPENALPR_CONF = "/etc/openalpr/openalpr.conf"
RUNTIME_DATA = "/etc/openalpr/runtime_data"
VIDEO_CAPTURE = "http://10.42.0.1:8081/videoView"
WEB = "http://car-radar.standy.me/api"

alpr = Alpr("eu", OPENALPR_CONF, RUNTIME_DATA)
if not alpr.is_loaded():
    print("Error loading OpenALPR")
    sys.exit(1)

alpr.set_top_n(20)
alpr.set_default_region("md")

capture = cv2.VideoCapture(VIDEO_CAPTURE)
# is_to_send = 0
while(True):
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

    frame = None
    while frame is None:
        et, frame = capture.read()

    print("processing frame")

    ret, image = cv2.imencode('.bmp', frame)
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

            re, image_jpg = cv2.imencode(".jpg", frame)
            hypotheses.append({"number": candidate["plate"],
                               "confidence": candidate['confidence'],
                               "photo": base64.b64encode(image_jpg)})
            print(candidate["plate"], candidate["confidence"])

    if len(hypotheses) == 0:
        continue

    r = requests.post(WEB,
            json={
                "latitude": 41.387427, 
                "longitude": 2.112993, 
                "hypotheses": hypotheses
                }
            )
    print(r.text)

# Call when completely done to release memory
alpr.unload()
capture.release()
cv2.destroyAllWindows()


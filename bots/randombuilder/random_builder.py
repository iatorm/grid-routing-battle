import random as r
import sys

while True:
    msg = input().split()
    sys.stderr.write(" ".join(msg)+'\n')
    if msg[0] == "BEGIN":
        side_len = int(msg[3])
    elif msg[0] == "DESTROY":
        print("NONE")
    elif msg[0] == "ACTIVATE":
        print("VERTEX " + str(r.randrange(side_len)) + "," + str(r.randrange(side_len)))
    elif msg[0] == "SCORE":
        break

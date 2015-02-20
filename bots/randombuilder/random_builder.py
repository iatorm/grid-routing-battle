import sys
import random as r

while True:
    msg = input().split()
    if msg[0] == "BEGIN":
        side_len = int(msg[3])
    elif msg[0] == "DESTROY":
        print("NONE")
    elif msg[0] == "ACTIVATE":
        print("VERTEX %d,%d"%(r.randrange(side_len), r.randrange(side_len)))
        sys.stdout.flush()
    elif msg[0] == "SCORE":
        break

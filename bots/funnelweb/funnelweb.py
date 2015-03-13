from random import *
import sys
ME = 0
def pt(x,y): return '%u,%u' % (x % side_len, y)

while True:
    msg = raw_input().split()

    if msg[0] == 'BEGIN':
        turn = 0
        numbots, turns, side_len = map(int, msg[1:])
        R = range(side_len)
        top = side_len - 1
        grid = dict((pt(x, y), []) for x in R for y in R)
        mynodes = set()
        deadnodes = set()
        freenodes = set(grid.keys())
        mycol = choice(R)
        extra = sample([pt(x,top) for x in R], side_len)
        path = [(mycol, y) for y in range(top, top - side_len/6, -1)]
        moves = []
        fence = []
        for x,y in path:
            moves.append( [pt(x,y), pt(x+1,y), pt(x-1,y)] )
            fence.extend( [pt(x+1,y), pt(x-1,y)] )
        for dx in range(2, side_len):
            fence.extend( [pt(x+dx,y), pt(x-dx,y)] )
        for x,y in [(mycol, y) for y in 
                range(top - side_len/6, top - 3*side_len/4, -1)]:
            moves.append( [pt(x,y), pt(x+1,y), pt(x-1,y)] )

    elif msg[0] == 'DESTROY':
        target = 'NONE'
        while fence:
            loc = fence.pop(0)
            if loc in freenodes:
                target = 'VERTEX ' + loc
                break
        print target
        sys.stdout.flush()

    elif msg[0] == 'BROKEN':
        for rid, loc in enumerate(msg[2:]):
            if loc != 'N':
                grid[loc] = None
                deadnodes.add(loc)
                freenodes.discard(loc)
                if loc in extra: extra.remove(loc)

    elif msg[0] == 'ACTIVATE':
        target = 'NONE'
        while moves:
            loclist = moves.pop(0)
            goodlocs = [loc for loc in loclist if loc in freenodes]
            if goodlocs:
                target = 'VERTEX ' + goodlocs[0]
                break
        if target == 'NONE':
            if extra:
                target = 'VERTEX ' + extra.pop(0)
            else:
                target = 'VERTEX ' + pt(choice(R), choice(R))
        print target
        sys.stdout.flush()

    elif msg[0] == 'OWNED':
        for rid, loc in enumerate(msg[2:]):
            if loc != 'N':
                grid[loc].append(rid)
                if rid == ME:
                    mynodes.add(loc)
                freenodes.discard(loc)
                if loc in extra: extra.remove(loc)
        turn += 1

    elif msg[0] == 'SCORE':
        break

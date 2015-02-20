
import sys, os
import subprocess as sub
import random as r
import time as t

verbose = False

def pos_to_str(pos):
    if pos is None:
        return "N"
    else:
        return "%d,%d"%pos

class Bot:
    "A bot."

    def __init__(self, name, directory, command, initial=None):
        self.name = name
        self.directory = directory
        self.command = command
        self.initial = initial
        self.score = 0
        self.delta_score = 0
        self.handle = None
        self.enemies = None
        self.last_choice = None
        self.report = None

    def push_msg(self, message):
        self.handle.stdin.write(message + '\n')
        self.handle.stdin.flush()

    def pull_msg(self):
        return self.handle.stdout.readline().rstrip()

class Vertex:
    "A vertex."

    INACTIVE, ACTIVE, BROKEN = 0, 1, 2

    def __init__(self, children):
        self.children = children
        self.is_sink = not children
        self.status = self.INACTIVE
        self.owners = set()

def open_bots(bot_path):
    global verbose
    alp = list("abcdefghijklmnopqrstuvwxyz")
    bots = []
    bot_file = open(bot_path)
    while True:
        line = bot_file.readline().rstrip()
        if not line:
            break
        if line[0] == '#':
            continue
        bot_name = line
        directory = bot_file.readline().rstrip()
        command = bot_file.readline().rstrip()
        if sys.platform != "win32":
            command = command.split()
        if verbose:
            bots.append(Bot(bot_name, directory, command, initial=alp.pop(0)))
        else:
            bots.append(Bot(bot_name, directory, command))
    bot_file.close()
    for i in range(len(bots)):
        order = list(range(len(bots)))
        order.remove(i)
        r.shuffle(order)
        bots[i].enemies = [bots[j] for j in order]
    return bots
        
def run_round(bots):
    slows = set()
    # Initial message: number of bots, number of turns, sidelength
    turns = len(bots)**2
    side = 2*turns
    for bot in bots:
        directory = os.path.join("bots", bot.directory)
        bot.handle = sub.Popen(bot.command, bufsize=1, universal_newlines=True, cwd=directory, stdin=sub.PIPE, stdout=sub.PIPE)
        bot.report = "BEGIN %d %d %d"%(len(bots), turns, side)
    grid = [[0]*side for y in range(side)]
    for y in reversed(range(side)):
        for x in range(side):
            grid[y][x] = Vertex([] if y == side-1 else [grid[y+1][(x-1)%side], grid[y+1][x], grid[y+1][(x+1)%side]])
    for turn in range(turns):
        # Destruction phase
        for bot in bots:
            bot.last_choice = None
            time = t.perf_counter()
            bot.push_msg(bot.report)
            bot.push_msg("DESTROY %d"%(turn,))
            response = bot.pull_msg()
            resp_split = response.split()
            delta = t.perf_counter() - time
            if delta > 1:
                print("  Bot %s was too slow to destroy (%f seconds)."%(bot.name, delta))
                slows.add(bot)
            if resp_split and resp_split[0] == "VERTEX":
                pos = (x,y) = tuple(map(int, resp_split[1].split(",")))
                if grid[y][x].status == Vertex.INACTIVE:
                    grid[y][x].status = Vertex.BROKEN
                    bot.last_choice = pos
            elif resp_split and resp_split[0] == "NONE":
                pass
            else:
                print("Bot %s gave malformed response (%s)."%(bot.name, response))
                sys.exit(0)
        # Make destruction reports
        for bot in bots:
            bot.report = "BROKEN %d %s %s"%(turn, pos_to_str(bot.last_choice), " ".join(pos_to_str(enemy.last_choice) for enemy in bot.enemies))
        # Activation phase
        activated = set()
        for bot in bots:
            bot.last_choice = None
            time = t.perf_counter()
            bot.push_msg(bot.report)
            bot.push_msg("ACTIVATE %d"%(turn,))
            response = bot.pull_msg()
            resp_split = response.split()
            delta = t.perf_counter() - time
            if delta > 1:
                print("  Bot %s was too slow to claim (%f seconds)."%(bot.name, delta))
                slows.add(bot)
            if resp_split and resp_split[0] == "VERTEX":
                pos = (x,y) = tuple(map(int, resp_split[1].split(",")))
                if grid[y][x].status == Vertex.INACTIVE:
                    grid[y][x].owners.add(bot)
                    activated.add(pos)
                    bot.last_choice = pos
            elif resp_split and resp_split[0] == "NONE":
                pass
            else:
                print("Bot %s gave malformed response (%s)."%(bot.name, response))
                sys.exit(0)
        for (x,y) in activated:
            grid[y][x].status = Vertex.ACTIVE
        # Make activation reports
        for bot in bots:
            bot.report = "OWNED %d %s %s"%(turn, pos_to_str(bot.last_choice), " ".join(pos_to_str(enemy.last_choice) for enemy in bot.enemies))
    # Compute scores by DFS
    print("  Finished, computing score.")
    if verbose:
        for row in reversed(grid):
            print("  " + "".join([r.sample(list(v.owners), 1)[0].initial if v.status == Vertex.ACTIVE else ("." if v.status == Vertex.INACTIVE else " ") for v in row]))
    for bot in bots:
        bot.delta_score = 0
    for x in range(side):
        if grid[0][x].status != Vertex.ACTIVE:
            continue
        for i in range(len(bots)):
            visited = set()
            cell = grid[0][x]
            succs = [child for child in cell.children if child.status == Vertex.ACTIVE]
            r.shuffle(succs)
            path = [(cell, succs)]
            while path:
                cell, children = path[0]
                if cell.is_sink:
                    break
                if children:
                    child = children.pop(0)
                    if child not in visited:
                        succs = [gchild for gchild in child.children if gchild.status == Vertex.ACTIVE]
                        r.shuffle(succs)
                        path = [(child, succs)] + path
                        visited.add(child)
                else:
                    path = path[1:]
            if path:
                for (cell, c) in path:
                    for bot in cell.owners:
                        bot.score += 1
                        bot.delta_score += 1
    # Report back scores
    for bot in bots:
        message = "SCORE %d %s"%(bot.delta_score, " ".join(str(enemy.delta_score) for enemy in bot.enemies))
        time = t.perf_counter()
        bot.push_msg(bot.report)
        bot.push_msg(message)
        bot.handle.stdin.close()
        bot.handle.wait()
        delta = t.perf_counter() - time
        if delta > 1:
            print("  Bot %s was too slow to halt (%f seconds)."%(bot.name, delta))
            slows.add(bot)
    return slows

def main():
    global verbose
    if len(sys.argv) > 1:
        rounds = int(sys.argv[-1])
    else:
        rounds = 100
    if "-v" in sys.argv:
        verbose = True
    print("Initializing bots.")
    for bot_dir in os.listdir("bots"):
        open(os.path.join("bots", bot_dir, "data.txt"), "w").close()
    bots = open_bots("bots.txt")
    print("Bots:")
    for bot in bots:
        if verbose:
            print("  %s (%s)"%(bot.name, bot.initial))
        else:
            print("  " + bot.name)
    print("Running %d rounds."%(rounds,))
    slow_bots = set()
    for i in range(rounds):
        print("  Round %d"%(i,))
        slow_bots.update(run_round(bots))
        print("  Results: " + " ".join(str(bot.delta_score) for bot in bots))
    print("Final results:")
    for bot in sorted(bots, key=lambda b: -b.score):
        print("  %s: %d"%(bot.name, bot.score))
    if slow_bots:
        print("The following bots were too slow:")
        for bot in slow_bots:
            print("  " + bot.name)
    

if __name__ == "__main__":
    main()

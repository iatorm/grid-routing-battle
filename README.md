# Grid-Routing Battle

## The Challenge

See the PPCG question for rules and other details.

## Using This Controller

Call the controller simply with

```
python controller.py
```

or pass the number of rounds as an argument (the default is 100):

```
python controller.py 50
```

The option `-v` can be added to obtain graphical output in ASCII, although it is probably too large to fit on a screen if more than 4 bots are present.

## Bot Files

Scripts/executables for bots should be placed in the subdirectories `bots/<botname>`.
When the controller is run, it places an empty text file called `data.txt` in each subdirectory.
Each bot can freely access this file during the game.

The controller uses the file `bots.txt` to enumerate the bots.
Any line beginning with `#` in that file is a comment.
The non-comment lines should occur in pairs, with the first one containing the name of a bot, and the second the command to run it.
The commands are executed with the main directory as the working directory.

## Bot API

Each bot is executed at the beginning of every round.
The controller writes to the bots' STDIN and reads from their STDOUT; their STDERR is not redirected, so it can be used for debugging.
There are six types of messages that the controller can send, and they are listed in the following.
A letter in brackets, as in `[n]`, signifies a variable, as opposed to the actual character `n`, and each message and response shall be followed by a newline.

- `BEGIN [B] [T] [S]` is given at the beginning of a round. The numbers `B`, `T` and `S` are the number of bots, the number of turns, and the side length of the grid, respectively.

- `DESTROY [T]` is given at the beginning of the destruction phase of turn `T`. Your bot shall answer with either `VERTEX [x],[y]` to break the vertex `(x,y)`, or `NONE` for no action. Trying to break an active of broken vertex results in your choice being interpreted as `NONE`.

- `BROKEN [T] [C1] [C2] ... [Cn]` is given at the end of the destruction phase of turn `T`. Each `Ci` is either of the form `[x],[y]`, if bot `i` chose to break the inactive vertex `(x,y)`, or the character `N`, if it made no choice. Every bot will receive its own choice as `C1`, and the rest in a random order that stays fixed throughout the game.

- `ACTIVATE [T]` is given at the beginning of the activation phase of turn `T`. It has the same semantics and expected answer format as `DESTROY [T]`.

- `OWNED [T] [C1] [C2] ... [Cn]` is given at the end of the activation phase of turn `T`. It has the same semantics as `BROKEN [T] [C1] [C2] ... [Cn]`.

- `SCORE [S1] [S2] ... [Sn]` is given after the final turn. Each `[Si]` is the score of bot `i` obtained in this round.

If more than **1 second** passes between the initial message/activation report and the next choice of vertex your bot makes, or more than **1 second** passes between the score report and the termination of your bot, it is disqualified.
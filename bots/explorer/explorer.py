import sys

class bd:

    def __init__(s, l):

        s.l=l
        s.b=[]
        s.v=[]
        s.m=[]
        s.bm=[]
        s.utd=False #up_to_date
        s.bmc=1

        for i in range(s.l):
            s.b+=[[]]
            s.v+=[[]]
            s.m+=[[]]
            s.bm+=[[]]
            for k in range(s.l):
                s.b[i]+=[0]
                s.v[i]+=[0]
                s.m[i]+=[0]
                s.bm[i]+=[s.bmc]

    def update(s):
        s.utd=True

        vu=[]
        vd=[]
        for i in range(s.l):
            vu+=[[]]
            vd+=[[]]
            for k in range(s.l):
                vu[i]+=[1]
                vd[i]+=[1]

        #spread up
        for i in range(s.l):
            vu[i][0]*=s.bm[i][0]

        for k in range(1,s.l):
            for i in range(s.l):
                sumv=vu[(i-1)%s.l][k-1]+vu[(i)%s.l][k-1]+vu[(i+1)%s.l][k-1]  
                vu[i][k]*=sumv*s.bm[i][k]/3

        #spread down
        t=s.l-1
        for i in range(s.l):
            vd[i][t]*=s.bm[i][t]

        for k in range(s.l-2,-1,-1):
            for i in range(s.l):
                sumv=vd[(i-1)%s.l][k+1]+vd[(i)%s.l][k+1]+vd[(i+1)%s.l][k+1]  
                vd[i][k]*=sumv*s.bm[i][k]/3

        #mult
        for i in range(s.l):
            for k in range(s.l):
                if s.b[i][k]==-1 or s.m[i][k]==1:
                    s.v[i][k]=float(-1)
                else:
                    s.v[i][k]=vu[i][k]*vd[i][k]/(s.b[i][k]+1)

    def add_act(s,al):
        s.utd=False

        for ind, ap in enumerate(al):
            i,k=ap
            s.b[i][k]+=1            
            s.bm[i][k]=2*s.bmc            
            #doesn't work alone WHY???
            if ind==0: s.m[i][k]=1

    def add_ina(s,il):
        s.utd=False

        for ind, ip in enumerate(il):
            i,k=ip
            s.b[i][k]=-1
            s.bm[i][k]=0                    

    def get_newact(s):
        s.update()
        vm=-28
        pm=None
        for i in range(s.l):
            for k in range(s.l):
                if s.v[i][k]>vm:
                    vm=s.v[i][k]
                    pm=(i,k)
        #doesn't work alone WHY???
        s.m[pm[0]][pm[1]]=1
        return pm


b=None

while True:
    inp=input()
    msg = inp.split()
    if msg[0] == "BEGIN":        
        b = bd(int(msg[3]))
    elif msg[0] == "DESTROY":
        print("NONE")
    elif msg[0] == "BROKEN":
        pl=[]
        for m in msg[2:]:
            if m!='N':
                pl+=[tuple(map(int,m.split(',')))]
        b.add_ina(pl)
    elif msg[0] == "ACTIVATE":
        at=b.get_newact()
        print("VERTEX %d,%d"%(at[0], at[1]))
    elif msg[0] == "OWNED":
        pl=[]
        for m in msg[2:]:
            if m!='N':
                pl+=[tuple(map(int,m.split(',')))]        
        b.add_act(pl)
    elif msg[0] == "SCORE":
        break       

    sys.stdout.flush()

import sys


membership = ""
nProc = -1
nMsgs = -1

# causalPast[p][m] = [(q1,m1), (q2,m2), ...] is the causal past of message m of process p
causalPast = None # to be initialised when CL arguments are parsed

# delivered[p][q][m] = True/False tells whether process p has delivered message m of process q
delivered = None # to be initialised when CL arguments are parsed

# depends[p][q] = True/False tells whether process p depends on process q
depends = None # to be initialised when CL arguments are parsed


def parseArguments():
    global membership
    global nProc
    global nMsgs
    global causalPast
    global delivered
    global depends

    if len(sys.argv) != 3:
        print("Usage:", sys.argv[0], "<membership> <nMsgs>")
        exit(1)
    membership = sys.argv[1]
    nMsgs = int(sys.argv[2])

    with open(membership) as memFile:
        firstLine = memFile.readline()
        nProc = int(firstLine)

    # process and message indices start from 1
    causalPast = [ [[] for j in range(1+nMsgs) ] for i in range(1+nProc)]
    delivered = [[[False for j in range (1+nMsgs)] for i in range(1+nProc)] for k in range(1+nProc)]
    depends = [[ False for j in range (1+nProc)] for i in range(1+nProc)]


def findDepends():
    global depends
    with open(membership) as memFile:

        for nLine, line in enumerate(memFile):
            if nLine <= nProc:
                continue	# ignore lines with IP-port assignments
            p = nLine - nProc
            for q in [int(strQ) for strQ in line.split()]:
                depends[p][q] = True

    return


# causalPast[p][m] contains the causal past of message m from process p UP TO MESSAGE m-1 OF PROCESS p
# When process p tries to deliver message m from process q, it checks up to message m-1 of process q,
# since messages previously delivered by q are in the causal past of message m-1 form q, and have already been checked for
def findCausalPast():
    global causalPast

    for p in range(1, 1+nProc):
        # Messages delivered now by p all go in the causal past of the next message process p broadcasts
        nextMsg = 1
        with open("da_proc_" + str(p) + ".out") as outFile:
            for line in outFile:
                tokens = line.split()
                if tokens[0] == "d":
                    q = int(tokens[1])
                    m = int(tokens[2])
                    if depends[p][q]:
                        causalPast[p][nextMsg].append((q,m))
                elif tokens[0] == "b":
                    m = int(tokens[1])
                    assert m == nextMsg
                    nextMsg += 1
                    if nextMsg == nMsgs:
                        return
                    causalPast[p][nextMsg].append((p,m))
                else:
                    raise Exception("Malformed file:", line)
    return


def checkDeliverable(p, q, m):
    for (qp, mp) in causalPast[q][m]:
        if not delivered[p][qp][mp]:
            # print(causalPast[q][m])
            return False
    return True


def checkLocal():
    global delivered

    for p in range(1, 1+nProc):
        filename = "da_proc_" + str(p) + ".out"
        with open(filename) as outFile:
            for line in outFile:
                tokens = line.split()
                if tokens[0] == "d":
                    q = int(tokens[1])
                    m = int(tokens[2])
                    if not checkDeliverable(p, q, m):
                        return False, filename, line
                    delivered[p][q][m] = True
                elif tokens[0] == "b":
                    m = int(tokens[1])
                    delivered[p][p][m] = True
                else:
                    raise Exception("Malformed file", "da_proc_" + p + ".out:", line)
    return True, "bravo"



parseArguments()
findDepends()
findCausalPast()
res = checkLocal()
if not res[0]:
    filename = res[1]
    line = res[2]
    print("Test failed")
    print(filename + ":", line)
else:
    print("Test passed")
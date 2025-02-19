#!/bin/bash

if [ "$1" = "" ] || [ "$2" = "" ]; then
    echo "Validates the submission (i.e., valid input for a simple test-case) for the given excercise"
    echo -e "usage: \n$ $0 <exercice_name (\"FIFO\" or \"LCausal\")> <language (\"C\" or \"JAVA\")>"
    exit 1

fi

# time to wait for correct processes to broadcast all messages (in seconds)
# (should be adapted to the number of messages to send)
time_to_finish=5

init_time=2

# configure lossy network simulation
sudo tc qdisc add dev lo root netem 2>/dev/null
sudo tc qdisc change dev lo root netem delay 50ms 200ms loss 10% 25% reorder 25% 50%

# create default Makefile if not existing
if [ ! -f Makefile ]; then
  echo "WARNING: Makefile not found! using default Makefile"
  if [ "$2" = "C" ]; then
    cp Makefile_c_example Makefile
  else
    cp Makefile_java_example Makefile
  fi
fi

# compile (should output: da_proc or Da_proc.class)
make clean
make

# prepare input
if [ "$1" = "FIFO" ]; then
echo "writing FIFO input..."
 
echo "10
1 127.0.0.1 12001
2 127.0.0.1 12002
3 127.0.0.1 12003
4 127.0.0.1 12004
5 127.0.0.1 12005
6 127.0.0.1 12006
7 127.0.0.1 12007
8 127.0.0.1 12008
9 127.0.0.1 12009
10 127.0.0.1 12010" > membership

else 
echo "writing LCausal input..."
    
echo "5
1 127.0.0.1 12001
2 127.0.0.1 12002
3 127.0.0.1 12003
4 127.0.0.1 12004
5 127.0.0.1 12005
1 4 5
2 1
3 1 2
4
5 3 4" > membership
fi

# start 10 processes, each broadcasting 100 messages
for i in `seq 1 10`
do
    if [ "$2" = "C" ]; then
      ./da_proc $i membership 100 &
    else
      java Da_proc $i membership 1000 &
    fi
    da_proc_id[$i]=$!
done

# leave some time for process initialization
sleep $init_time

# do some nasty stuff like process crashes and delays
# example:
kill -STOP "${da_proc_id[3]}" # pause process 3
sleep 1
kill -STOP "${da_proc_id[5]}" # pause process 5
sleep 1
kill -STOP "${da_proc_id[9]}" # pause process 9
sleep 1

kill -TERM "${da_proc_id[2]}" # crash process 2
da_proc_id[2]=""
kill -TERM "${da_proc_id[6]}" # crash process 6
da_proc_id[6]=""
kill -TERM "${da_proc_id[8]}" # crash process 8
da_proc_id[8]=""

kill -CONT "${da_proc_id[3]}" # resume process 3
kill -CONT "${da_proc_id[5]}" # resume process 5
kill -CONT "${da_proc_id[9]}" # resume process 9

# start broadcasting
for i in `seq 1 10`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill -USR2 "${da_proc_id[$i]}"
    fi
done

# leave some time for the correct processes to broadcast all messages
sleep $time_to_finish

# stop all processes
for i in `seq 1 10`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	kill -TERM "${da_proc_id[$i]}"
    fi
done

# wait until all processes stop
for i in `seq 1 10`
do
    if [ -n "${da_proc_id[$i]}" ]; then
	    wait "${da_proc_id[$i]}"
    fi
done

# check logs for correctness
./check_output.sh 1 3 5 7 9

echo "Correctness test done."
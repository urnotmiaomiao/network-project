#!/bin/bash


# Change this to your netid
netid=txl180004

#
# Root directory of your project
PROJDIR=/home/012/t/tx/txl180004/acn/proj/FogNode

#
# This assumes your config file is named "config.txt"
# and is located in your project directory
#
CONFIG=$PROJDIR/config.txt

#
# Directory your java classes are in
#
BINDIR=$PROJDIR/bin

#
# Your main project class
#
PROG=Main

nodeID=1

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read firstLine
	nodes=$( echo $firstLine | awk '{print $1}')
    echo "TOTAL NODE# : "$nodes
    while read line 
    do
        host=$( echo $line | awk '{ print $2 }' )

        if (($nodeID <= $nodes)) 
		then
			echo $host
			ssh $netid@$host killall -u $netid &
			sleep 1
		fi

        nodeID=$(( nodeID + 1 ))
    done
   
)


echo "Cleanup complete"


function usage {
	echo "Usage: <Number of Peers> <Version>"
	exit
}

function compile {
	mkdir bin
	javac $(find ./src/* | grep .java) -d bin
}

function startRMI {
	
	killall rmiregistry
    cd bin
	xterm -e "rmiregistry" &
    cd ..
}

function launchPeers {

    count=1
	while [ "$count" -le $1 ]
	do	
    	xterm -e "java -cp bin dbs.peer.InitPeer $2 peer$count $count $count 200$count $3 $4 $5 $6 $7 $8" & $SHELL &
		count=$(( $count + 1 ))
	done
}

if (( $# != 8 )); then
    usage
fi

MCip=$3
MCport=$4
MDBip=$5
MDBport=$6
MDRip=$7
MDRport=$8
compile
startRMI
launchPeers $1 $2 $MCip $MCport $MDBip $MDBport $MDRip $MDRport
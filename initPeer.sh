usage() {
	echo "Usage: <Number of Peers> <Version> <MC_IP> <MC_PORT> <MCB_IP> <MCB_PORT> <MCR_IP> <MCR_PORT>"
	echo "Example: sh initPeer.sh 4 1.0 224.0.0.0 4445 224.0.0.1 4446 224.0.0.2 4447"
}

compile() {
	mkdir bin
	javac -cp src $(find ./src/* | grep .java) -d bin
}

startRMI() {
	
	killall rmiregistry
    cd bin
	xterm -e "rmiregistry" &
    cd ..
}

launchPeers() {

    count=1
	while [ "$count" -le $1 ]
	do	
    	xterm -e "java -cp bin dbs.peer.InitPeer $2 $count peer$count $3 $4 $5 $6 $7 $8" & $SHELL &
		count=$(( $count + 1 ))
	done
}

if [ "$#" -ne 8 ]; then
    usage
else
	MCip=$3
	MCport=$4
	MDBip=$5
	MDBport=$6
	MDRip=$7
	MDRport=$8
	compile
	startRMI
	launchPeers $1 $2 $MCip $MCport $MDBip $MDBport $MDRip $MDRport
fi
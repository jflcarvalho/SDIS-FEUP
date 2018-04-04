usage() {
    echo "Usage: <Access Point> <Protocol> [ <Number of Bytes> | <File> | <File>, <Replication Degree>]"
    echo "Example: sh testApp.sh pee0 BACKUP testFiles/testPdf.pdf 3"
}

if [ "$#" -eq 2 ]; then
    xterm -e "java -cp bin dbs.client.TestApp $1 $2"
elif [ "$#" -eq 3 ]; then
    xterm -e "java -cp bin dbs.client.TestApp $1 $2 $3"
elif [ "$#" -eq 4 ]; then
    xterm -e "java -cp bin dbs.client.TestApp $1 $2 $3 $4"
else
    usage 
fi
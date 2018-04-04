# SDIS-FEUP
[![BCH compliance](https://bettercodehub.com/edge/badge/jflcarvalho/SDIS-FEUP?branch=project&token=2d8467f22312dd57f5d76ab38592445ef673d7c4)](https://bettercodehub.com/)
[![CodeFactor](https://www.codefactor.io/repository/github/jflcarvalho/sdis-feup/badge)](https://www.codefactor.io/repository/github/jflcarvalho/sdis-feup)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/322adeafbb664a7995776a14bf2f8c39)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jflcarvalho/SDIS-FEUP&amp;utm_campaign=Badge_Grade)

To compile and launch peers run:
```sh
sh initPeers.sh <Number of Peers> <Version> <MCip> <MCport> <MDBip> <MDBport> <MDRip> <MDRport>
sh initPeer.sh 4 1.0 224.0.0.0 4445 224.0.0.1 4446 224.0.0.2 4447
```
To start the testApp run:
```sh
sh testApp.sh <Access Point> <Protocol> [ <Number of Bytes> | <File> | <File>, <Replication Degree>]
sh testApp.sh pee0 BACKUP testFiles/testPdf.pdf 3
```
OR RUN MANUALLY:

To start rmi run the following command:
```bash
#WINDOWS:
cd out\production\SDIS-FEUP
start rmiregistry

#LINUX/UNIX:
cd out/production/SDIS-FEUP
rmiregistry
```
To start the peer run the following command(advised to run at least 3 peers):
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP dbs.peer.InitPeer [1.0 | 1.11] <peerID> <accessPoint> 224.0.0.0 4445 224.0.0.1 4446 224.0.0.2 4447

#LINUX/UNIX:
consolejava -cp out/production/SDIS-FEUP dbs.peer.InitPeer [1.0 | 1.11] <peerID> <accessPoint> 224.0.0.0 4445 224.0.0.1 4446 224.0.0.2 4447
```
To test the DELETE protocol run the following command:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP dbs.client.testAPP <Access Point> DELETE <File>

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP dbs.client.testAPP <Access Point> DELETE <File>
```
To test the BACKUP protocol run the following command:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP dbs.client.testAPP <Access Point> BACKUP <File> <Replication Degree>

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP dbs.client.testAPP <Access Point> BACKUP <File> <Replication Degree>
```
To test the RESTORE protocol run the following command:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP dbs.client.testAPP <Access Point> RESTORE <File>

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP dbs.client.testAPP <Access Point> RESTORE <File>
```
To test the STATE protocol run the following command:WINDOWS:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP dbs.client.testAPP <Access Point> STATE

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP dbs.client.testAPP <Access Point> STATE
```

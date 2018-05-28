# SDIS-FEUP
[![BCH compliance](https://bettercodehub.com/edge/badge/jflcarvalho/SDIS-FEUP?branch=develop)](https://bettercodehub.com/)


##### Start the initial Database Peer run the following command:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP Main Database 1024

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP Main Database 1024
```
##### To add more Database Peer
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP Main Database 1024 <contact ip (127.0.0.1:1024)>

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP Main Database 1024 <contact ip (127.0.0.1:1024)>
```

##### Start the initial Woker Peer run the following command:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP Main Worker 1025 127.0.0.1:1024

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP Main Worker 1025 127.0.0.1:1024
```
##### To add more Workers Peers
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP Main Worker 1025 127.0.0.1:1024 <contact ip (127.0.0.1:1025)>

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP Main Worker 1025 127.0.0.1:1024 <contact ip (127.0.0.1:1025)>
```

##### Start the Server Peer run the following command:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP Server 2000 127.0.0.1:1024 127.0.0.1:1025

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP Server 2000 127.0.0.1:1024 127.0.0.1:1025
```
##### Start the Client Peer run the following command:
```bash
#WINDOWS:
java -cp out\production\SDIS-FEUP Client 127.0.0.1 2000 

#LINUX/UNIX:
java -cp out/production/SDIS-FEUP Client 127.0.0.1 2000
```

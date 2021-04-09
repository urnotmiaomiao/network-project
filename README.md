# ADVANCED COMPUTIER NETWORKS

## Fog Computing

2019 Fall, CS6390

### Implementation Details

#### Implementation Details

Program language: Java

Platform: Eclipse



##### 3 Types of Nodes

1. IoT Node: Periodically generates request including request sequence number, forward limit, process time, its own IP address and its UDP port number that is used to receive response packets. It has two main functionalities:

   1. periodically sends request packets to fog nodes: UDP
   2. receives responses from Fog nodes or Cloud nodes: UDP

2. Fog Node: receives packets and has three options:

   1. process and reply to IoT node: UDP

   2. send to neighbors: TCP

   3. send to cloud: TCP   

      update messages among the fog nodes: TCP

3. Cloud Node: Processes request from Fog nodes and sends responses to the original IoT node. It has three main functionalities:

   1. receive request packets from fog nodes and save them to a queue: TCP
   2. pick a request from the queue and process the request
   3. after processing a request, sends response packets to the original fog node: UDP



##### Message Format

1. Request message:
   1. sequence number
   2. process time
   3. forward limit
   4. IoT node’s IP address
   5. IoT node’s port number
2. State message:
   1. node ID
   2. queueing time



##### Methodology

1. FogNode:
   1. Class Node and SysInfo are structures.
   2. Class Parse: read config.txt.
   3. Class FogProtocol: build connection to neighbors and cloud.    
      1. Send state messages
      2. Receive, process and forward request messages from IoT
2. IoT Node: Class IoTNode: defines several parameters to store IoT node’s information and defines functions to implement IoT node’s functionalities. Implements Runnable to run generateRequest function parallely.
   1. function getConfig(): abstracts IoT node’s main information (IP address, UDP port number, interval time, fog nodes’ IP addresses and UDP port numbers) from config.properties file, and storing them in to proper data structure. 
   2. function createRandomCons(): creating random numbers as forward limit and process time.
   3. function generateRequest(): construct a request packet and send it to a random Fog node. 
   4. function receiveRes(): listening at node’s UDP port for receiving responses from Fog nodes and Cloud nodes, and print out the response message.
3. Cloud Node
   1. Class CloudNode: abstracting cloud node’s main information (IP address, TCP port number) from config.properties file, and storing them in to proper data structure. Also defining listening, request processing and responde sending functions.
   2. Class CloudListening, CloudProcess, CloudAddQueue: used to create Threads to operate listening, request processing and queue adding functions parallely. 

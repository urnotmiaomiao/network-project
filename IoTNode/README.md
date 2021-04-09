Language: JAVA 1.8
Platform: Eclipse

How to run: 
	1. Make sure the iotnode.jar is placed in the same folder with /config/config.properties
	2. cd the folder of the folder where you placed jar file and config folder, run the command "java -jar iotnode.jar"

In config.properties:
	[could be omitted]
	1. [my_ip]: this cloud node's ip address
	2. my_port: this cloud node's TCP port number that is used to receive Fog Node requests
	3. interval: the interval time(ms) between sending message.
	3. fog_nodes: fog node1's IP address, fog node1's UDP port number, [fog node2's IP address, fog node2's UDP port number, ...] 
	
Group: Tianyi liu, Miao Miao
Class: CS6390.001 Advanced Computer Networks
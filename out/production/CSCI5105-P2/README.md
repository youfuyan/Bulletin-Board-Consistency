# CSCI5105-P2

Project 2: Bulletin Board Consistency

## Tema Member:

Youfu Yan, Bin Hu

## Project Description

This project is to implement a simple Bulletin Board (BB) system in which clients can post, reply, and read articles stored in the BB. The BB is maintained by a group of replicated servers that offer sequential consistency, quorum consistency, and Read-your-Write consistency. There are three basic classes in our BB system: the coordinator, the server, and the client. The coordinator is responsible for maintaining the server list and the client list. The server is responsible for storing the articles and providing the services to the clients. The client is responsible for posting, replying, and reading articles. Our BB system is implemented in Java and uses TCP sockets for communication between clients, servers, and the coordinator.

## Project Structure

### Client

The client.java is responsible for posting, replying, and reading articles. The client is implemented in the class Client.java. The client is running based on the simple graphical user interface (GUI) implemented using Java Swing. The client can connect to the server by selecting the server from the server list and clicking the connect button. The client can post, select an article to reply, and read an selected articles by clicking the corresponding buttons. The client can disconnect from the server by clicking the disconnect button. The client can exit the system by clicking the exit button.
![Client](client.png)

### Server

The server.java is responsible for storing the articles and providing the services to the clients. The server is implemented in the class Server.java. The server will handle the requests from the clients and send the responses to the clients. The server can handle the following requests from the clients: postArticle, replyToArticle, readArticle and fetchArticle. The server can be set primary server to synchronize the data with other server based on the consistency. Other non-primary servers can back up the data from the primary server.

### Coordinator

The coordinator.java is responsible for maintaining the server list. The coordinator is implemented in the class Coordinator.java. The coordinator can set the consistency. Once the consistency been set, the coordinator will send the consistency to the servers. The coordinator can add or remove the server from the server list.

### RunServer

The runServer.java will set up the coordinator and server from the `server_addresses.txt`. The primary server is default to be the first server in the `server_addresses.txt`. All servers will run in different threads. The coordinator will send the consistency to the servers. The server will start to listen to the client request.

## How to run

1. Open the project in IntelliJ IDEA(default installed in CSE lab)
2. Set up the JDK to `JDK 11` by `File -> Project Structure -> Project -> Project SDK`. (default installed in CSE lab)
3. Build the project using opertion `Build -> Build Project` on the top menu
4. Go to `runServer.java` and run the main function

- (Optional) If you want to change the server list, you can change the `server_addresses.txt` file before run the `runServer.java` main function

1. Go to `Client.java` and run the main function

## Cost Analysis for different consistency

### Sequential Consistency

### Quorum Consistency

### Read-your-Write Consistency

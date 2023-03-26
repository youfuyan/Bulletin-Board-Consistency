package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GroupServer extends Remote {
    // Define the methods that the client can call on the server
    boolean join (String IP, int Port) throws RemoteException;
    boolean leave (String IP, int Port) throws RemoteException;
    boolean subscribe(String  IP,  int  Port,  String  Article)  throws
            RemoteException;
    boolean unsubscribe  (String  IP,  int  Port,  String  Article)  throws
            RemoteException;
    boolean publish (String  Article,  String  IP,  int  Port)  throws
            RemoteException;
    boolean ping () throws RemoteException;

    boolean unsubscribeAll(String IP, int Port) throws RemoteException;

    // public void greeting() throws RemoteException;
    String greeting() throws RemoteException;

    boolean post(String ip, int port, String article_content) throws RemoteException;
    
    boolean read(String ip, int port) throws RemoteException;

    boolean readline(String ip, int port, int article_id) throws RemoteException;

    boolean readArticle(String ip, int port, int article_id) throws RemoteException;
    boolean reply(String ip, int port,int article_id, String replyContent) throws RemoteException;

}

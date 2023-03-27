import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;

public class Server {
    private final int serverPort;
    private ConcurrentHashMap<Integer, Article> articles;
    private boolean isCoordinator;
    private InetSocketAddress coordinatorSocketAddress;
    private Coordinator coordinator;
    static String Policy = "Sequential";
    static int Nr = 3;
    static int Nw = 4;

    public Server(int serverPort, boolean isCoordinator, Coordinator coordinator) {
        this.serverPort = serverPort;
        this.isCoordinator = isCoordinator;
        this.articles = new ConcurrentHashMap<>();
        this.coordinator = coordinator;
        this.coordinatorSocketAddress = coordinator.getCoordinatorSocketAddress();
        startServer();
    }


    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

//            ServerSocket socket2 = new ServerSocket(serverPort);
//            SocketforServerThread(socket2);

            // Redirect standard output to a file based on server port number
            String logFileName = "server_" + serverPort + ".log";
            PrintStream fileOut = new PrintStream(new FileOutputStream(logFileName));
            log(fileOut, "Server started on port " + serverPort);
            ExecutorService executor = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClientConnection(clientSocket, fileOut));
                //write to log file
                log(fileOut, "Client connected from " + clientSocket.getInetAddress().getHostAddress());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SocketforServerThread(ServerSocket socket){

    }

    public void startSynchronizationThread() {
        Thread synchronizationThread = new Thread(() -> {
            while (true) {
                coordinator.synchronizeReplicas();

                try {
                    Thread.sleep(5000); // Sleep for 5 seconds before the next synchronization attempt
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        synchronizationThread.start();
    }


    private void handleClientConnection(Socket clientSocket, PrintStream logOutput) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String command = in.readLine();
            if (command == null || command.isEmpty()) {
                log(logOutput, "Error: Received empty or null command");
                return;
            }
            String[] commandParts = command.split(" ");
            String action = commandParts[0];

            switch (action) {
                case "GENERATE_ARTICLE_ID":
                    if (isCoordinator) {
                        out.println(coordinator.generateArticleId());
                    } else {
                        log(logOutput, "Error: Only the coordinator can generate article IDs");
                        out.println("ERROR");
                    }
                    break;
                case "FETCH_ARTICLES":
                    log(logOutput, "Received command: " + command);
                    int startIndex = Integer.parseInt(commandParts[1]);
                    int count = Integer.parseInt(commandParts[2]);
                    fetchArticles(out, startIndex, count);
                    log(logOutput, "Sent " + count + " articles starting from index " + startIndex);
                    break;
                case "POST_ARTICLE":
                    log(logOutput, "Received command: " + command);
                    String title = in.readLine();
                    String content = in.readLine();
                    postArticle(out, title, content);
                    log(logOutput, "Posted article with title: " + title);
                    break;
                // Add more commands here
                case "REPLY_ARTICLE":
                    log(logOutput, "Received command: " + command);
                    if (commandParts.length < 2) {
                        log(logOutput, "Error: Invalid command format for REPLY_ARTICLE");
                        return;
                    }
                    int parentId = Integer.parseInt(commandParts[1]);
                    String replyTitle = in.readLine();
                    String replyContent = in.readLine();
                    replyArticle(out, parentId, replyTitle, replyContent);
                    log(logOutput, "Parent ID: " + parentId + ", Title: " + replyTitle + ", Content: " + replyContent);
                    break;
                // coordinator accept new update
                case "PRIMARY_UPDATE":
                    if (isCoordinator) {
                        int id_primary = Integer.parseInt(in.readLine());
                        String title_primary = in.readLine();
                        String content_primary = in.readLine();
                        int parentid_primary = Integer.parseInt(in.readLine());
                        int indentationLevel_primary = Integer.parseInt(in.readLine());
                        acceptArticleForBackup(id_primary,title_primary,content_primary,parentid_primary,indentationLevel_primary);
                        sendToBackup(id_primary,title_primary,content_primary,parentid_primary,indentationLevel_primary);
                    } else {
                        log(logOutput, "Error: Only the coordinator can achieve primary update");
                        out.println("ERROR");
                    }
                    break;
                // coordinator send update to other servers
                case "BACK_UP":
                    if(!isCoordinator){
                        int id_backup = Integer.parseInt(in.readLine());
                        String titile_backup = in.readLine();
                        String content_backup = in.readLine();
                        int parentid_backup = Integer.parseInt(in.readLine());
                        int indentationLevel_backup = Integer.parseInt(in.readLine());
                        acceptArticleForBackup(id_backup,titile_backup,content_backup,parentid_backup,indentationLevel_backup);
                    }else {
                        log(logOutput, "Error: Only the non-coordinator can accept update for back up");
                        out.println("ERROR");
                    }
                    break;
                case "COORD_QUORUM":
                    if(isCoordinator){
                        int id_backup = Integer.parseInt(in.readLine());
                        String mode = in.readLine();
                        if(mode == "Write_Request") {
                            String titile_backup = in.readLine();
                            String content_backup = in.readLine();
                            int parentid_backup = Integer.parseInt(in.readLine());
                            int indentationLevel_backup = Integer.parseInt(in.readLine());
                            requestRandomQuorumW(id_backup, titile_backup, content_backup, parentid_backup, indentationLevel_backup);
                        }else{
                            int id = Integer.parseInt(in.readLine());
                            requestRandomQuorumR(id);
                        }

                    }else {
                        log(logOutput, "Error: Only the coordinator can look for random quorum");
                        out.println("ERROR");
                    }
                    break;
                case "WRITE_QUORUM":
                    int id_backup = Integer.parseInt(in.readLine());
                    String titile_backup = in.readLine();
                    String content_backup = in.readLine();
                    int parentid_backup = Integer.parseInt(in.readLine());
                    int indentationLevel_backup = Integer.parseInt(in.readLine());
                    acceptArticleForBackup(id_backup,titile_backup,content_backup,parentid_backup,indentationLevel_backup);
                    break;
                case "READ_QUORUM":
                    int id =Integer.parseInt(in.readLine());
                    if(id!=0){
                        sendChooseToClient();
                    }
                    else{
                        acknowledegeArticlecount();
                    }
                    break;
                case "SYN_QUORUM":
                        sendRedToclient();
                    break;
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(PrintStream logOutput, String message) {
        logOutput.println(message);
    }

    private int requestArticleIdFromCoordinator() {
        try (Socket socket = new Socket(coordinatorSocketAddress.getAddress(), coordinatorSocketAddress.getPort());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GENERATE_ARTICLE_ID");
            String response = in.readLine();

            return Integer.parseInt(response);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void synReadData(){
        switch (Policy){
            case "Quorum":
                try{
                    Socket serverSocket = new Socket(coordinatorSocketAddress.getAddress(),coordinatorSocketAddress.getPort());
                    PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
                    out.println("COORD_QUORUM");
                    out.println("Read_Request");
                } catch (IOException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void synWriteData(Article newArticle){
        switch(Policy){
            case "Sequential":
                if(!isCoordinator){
                    try{
                        Socket serverSocket = new Socket(coordinatorSocketAddress.getAddress(),coordinatorSocketAddress.getPort());
                        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
                        out.println("PRIMARY_UPDATE");
                        out.println(Integer.toString(newArticle.getId()));
                        out.println(newArticle.getTitle());
                        out.println(newArticle.getContent());
                        out.println(Integer.toString(newArticle.getParentId()));
                        out.println(Integer.toString(newArticle.getIndentationLevel()));
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    sendToBackup(newArticle.getId(),newArticle.getTitle(),newArticle.getContent(),newArticle.getParentId(),newArticle.getIndentationLevel());
                }
                break;
            case "Quorum":
                if(!isCoordinator){
                    try{
                        Socket serverSocket = new Socket(coordinatorSocketAddress.getAddress(),coordinatorSocketAddress.getPort());
                        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
                        out.println("COORD_QUORUM");
                        out.println("Write_Request");
                        out.println(Integer.toString(newArticle.getId()));
                        out.println(newArticle.getTitle());
                        out.println(newArticle.getContent());
                        out.println(Integer.toString(newArticle.getParentId()));
                        out.println(Integer.toString(newArticle.getIndentationLevel()));

                    } catch (IOException e){
                        e.printStackTrace();
                    }

                }
                else{
                    requestRandomQuorumW(newArticle.getId(),newArticle.getTitle(),newArticle.getContent(),newArticle.getParentId(),newArticle.getIndentationLevel());
                }
                break;
            case "Read-your-Write":
                break;
        }
    }

    private void acceptArticleForBackup(int id, String title, String content,int parentid,int indentationLevel){
        Article newArticle = new Article(id, title, content, parentid, indentationLevel);
        articles.put(id, newArticle);
    }
    
    private void sendToBackup(int id, String title, String content,int parentid,int indentationLevel){
        try{
            for(InetSocketAddress serveraddress:coordinator.getServerSocketAddressList()){
                Socket serverSocket = new Socket(serveraddress.getAddress(),serveraddress.getPort());
                PrintWriter serverout = new PrintWriter(serverSocket.getOutputStream(),true);
                serverout.println("BACK_UP");
                serverout.println(Integer.toString(id));
                serverout.println(title);
                serverout.println(content);
                serverout.println(Integer.toString(parentid));
                serverout.println(Integer.toString(indentationLevel));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void requestRandomQuorumW(int id, String title, String content,int parentid,int indentationLevel){
        try{
            Random rand = new Random();
            Set<Integer> randSet = new HashSet<Integer>();
            while (randSet.size() < Nw) {
                int randInt = rand.nextInt(6);
                randSet.add(randInt);
            }
            for(int index:randSet){
                InetSocketAddress serveraddress = coordinator.getServerSocketAddressList().get(index);
                Socket serverSocket = new Socket(serveraddress.getAddress(),serveraddress.getPort());
                PrintWriter serverout = new PrintWriter(serverSocket.getOutputStream(),true);
                serverout.println("WRITE_QUORUM");
                serverout.println(Integer.toString(id));
                serverout.println(title);
                serverout.println(content);
                serverout.println(Integer.toString(parentid));
                serverout.println(Integer.toString(indentationLevel));
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestRandomQuorumR(int id){
        try{
            Random rand = new Random();
            Set<Integer> randSet = new HashSet<Integer>();
            while (randSet.size() < Nr) {
                int randInt = rand.nextInt(6);
                randSet.add(randInt);
            }
            for(int index:randSet){
                InetSocketAddress serveraddress = coordinator.getServerSocketAddressList().get(index);
                Socket serverSocket = new Socket(serveraddress.getAddress(),serveraddress.getPort());
                PrintWriter serverout = new PrintWriter(serverSocket.getOutputStream(),true);
                serverout.println("READ_QUORUM");
                serverout.println(id);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acknowledegeArticlecount(){
        try{
            coordinator.addReadQuorum(serverPort,getArticlesCount());
            if(coordinator.getReadQuorum().size()==2*Nr){
                int maxcount = coordinator.getReadQuorum().get(1);
                int maxport = coordinator.getReadQuorum().get(0);
                for(int i =0;i<Nr;i++){
                    if(coordinator.getReadQuorum().get(i*2+1)>maxcount)
                        maxcount = coordinator.getReadQuorum().get(i*2+1);
                        maxport = coordinator.getReadQuorum().get(i*2);
                }
                Socket serverSocket = new Socket(coordinatorSocketAddress.getAddress(),maxport);
                PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),true);
                out.println("SYN_QUORUM");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private synchronized void postArticle(PrintWriter out, String title, String content) {
        int nextId = !isCoordinator ? requestArticleIdFromCoordinator() : coordinator.generateArticleId();
        int parentId = -1;
        int indentationLevel = 0;
        Article newArticle = new Article(nextId, title, content, parentId, indentationLevel);
        articles.put(nextId, newArticle);
        synWriteData(newArticle);
        out.println("SUCCESS");
        // Implement logic to propagate the new article to other replicas based on the chosen consistency policy
        // coordinator.propagateArticle(newArticle);
    }

    private synchronized void fetchArticles(PrintWriter out, int startIndex, int count) {
        if(Policy == "Quorum")
            synReadData();
        else {
            int endIndex = Math.min(startIndex + count, getArticlesCount());
            for (int i = startIndex; i < endIndex + 1; i++) {
                Article article = articles.get(i);
                if (article != null) {
                    int indentationLevel = getIndentationLevel(article.getParentId());
                    out.println(article.getId() + "|" + article.getTitle() + "|" + article.getContent() + "|" + article.getParentId() + "|" + indentationLevel);
                }
            }
            out.println("END");
        }
    }

    private synchronized void replyArticle(PrintWriter out, int parentId, String title, String content) {
        int nextId =  requestArticleIdFromCoordinator();
        int indentationLevel = getIndentationLevel(parentId) + 1;
        Article newArticle = new Article(nextId, title, content, parentId, indentationLevel);
        articles.put(nextId, newArticle);
        synWriteData(newArticle);
        out.println("SUCCESS");

        // Implement logic to propagate the new reply to other replicas based on the chosen consistency policy
        // coordinator.propagateArticle(newArticle);
    }

    public int getArticlesCount() {
        return articles.size();
    }

    public int getServerPort() {
        return serverPort;
    }

    private int getIndentationLevel(int parentId) {
        if (parentId == -1) {
            return 0;
        } else {
            Article parentArticle = articles.get(parentId);
            if (parentArticle != null) {
                return getIndentationLevel(parentArticle.getParentId()) + 1;
            } else {
                return 0;
            }
        }
    }


}

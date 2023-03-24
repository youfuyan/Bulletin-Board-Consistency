import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int serverPort;
    private List<Article> articles;

    public Server(int serverPort) {
        this.serverPort = serverPort;
        this.articles = new ArrayList<>();
        startServer();
    }

    public static void main(String[] args) {
        new Server(8000); // Replace 8000 with your desired server port number
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server started on port " + serverPort);
            ExecutorService executor = Executors.newCachedThreadPool();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void handleClientConnection(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String command = in.readLine();
            String[] commandParts = command.split(" ");
            String action = commandParts[0];

            switch (action) {
                case "FETCH_ARTICLES":
                    int startIndex = Integer.parseInt(commandParts[1]);
                    int count = Integer.parseInt(commandParts[2]);
                    fetchArticles(out, startIndex, count);
                    break;
                case "POST_ARTICLE":
                    String title = in.readLine();
                    String content = in.readLine();
                    postArticle(out, title, content);
                    break;
                // Add more commands here
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void postArticle(PrintWriter out, String title, String content) {
        int nextId = articles.size() + 1;
        Article newArticle = new Article(nextId, title, content, -1);
        articles.add(newArticle);
        out.println("SUCCESS");
    }

    private synchronized void fetchArticles(PrintWriter out, int startIndex, int count) {
        int endIndex = Math.min(startIndex + count, articles.size());
        for (int i = startIndex; i < endIndex; i++) {
            Article article = articles.get(i);
            out.println(article.getId() + "|" + article.getTitle() + "|" + article.getContent() + "|" + article.getParentId());
        }
        out.println("END");
    }

    // Add more methods for handling other commands, like posting and replying to articles
}

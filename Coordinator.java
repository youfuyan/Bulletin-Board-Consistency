import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Coordinator {

    private List<String> serverAddresses;
    private InetSocketAddress coordinatorSocketAddress;

    private int articleId;

    public Coordinator() {
        this.serverAddresses = new ArrayList<>();
        try {
            loadServerAddresses("server_addresses.txt");
            if (!serverAddresses.isEmpty()) {
                String[] parts = serverAddresses.get(0).split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                coordinatorSocketAddress = new InetSocketAddress(host, port);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        articleId = 0;
    }

    private void loadServerAddresses(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                serverAddresses.add(line);
            }
        }
    }

    public InetSocketAddress getCoordinatorSocketAddress() {
        return coordinatorSocketAddress;
    }


    public List<InetSocketAddress> getServerSocketAddressList() {
        List<InetSocketAddress> socketAddresses = new ArrayList<>();
        for (String address : serverAddresses) {
            String[] parts = address.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            socketAddresses.add(new InetSocketAddress(host, port));
        }
        return socketAddresses;
    }

    public synchronized int generateArticleId() {
        return articleId++;
    }

    public List<String> getServerAddresses() {
        return serverAddresses;
    }


    public void propagateArticle(Article article) {
        // Implement logic to propagate articles based on the chosen consistency policy
        // For example, for sequential consistency, you can use the primary-backup protocol
    }
}

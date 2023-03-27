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

    private String consistencyPolicy;

    private final String SEQUENTIAL = "sequential";
    private final String QUORUM = "quorum";
    private final String READ_YOUR_WRITES = "read_your_writes";

    private ArrayList<Integer> port_articlecount = new ArrayList<Integer>();

    public Coordinator(String consistencyPolicy) {
        this.serverAddresses = new ArrayList<>();
        this.consistencyPolicy = consistencyPolicy;
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
        articleId = 1;
    }

    public Coordinator() {
        this.serverAddresses = new ArrayList<>();
        this.consistencyPolicy = SEQUENTIAL;
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
        articleId = 1;
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

    public void synchronizeReplicas() {
        // Iterate through all server addresses and synchronize the state
        for (InetSocketAddress address : getServerSocketAddressList()) {
            // Create a ServerAPI instance for the target server
            ServerAPI serverAPI = new ServerAPI(address);

            // Retrieve the latest state from the target server
            try {
                Article latestArticle = serverAPI.receiveArticle();

                // Update the local state if the received state is more recent
                if (latestArticle.getId() > articleId) {
                    articleId = latestArticle.getId();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void addReadQuorum(int port,int article_count){
        this.port_articlecount.add(port);
        this.port_articlecount.add(article_count);
    }

    public ArrayList<Integer> getReadQuorum(){
        return this.port_articlecount;
    }

    public void propagateArticle(Article article) {
        // Implement logic to propagate articles based on the chosen consistency policy
        // For example, for sequential consistency, you can use the primary-backup protocol
        
        // sequential consistency
        // Anytime a generating ID is requested, artcile need transmit to coordinator and then back-up to other servers
        

        //quorum consistency

        //Read-your-Write consistency
    }
}

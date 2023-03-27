import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class runServer {
    public static void main(String[] args) {
        // create a coordinator instance
        Coordinator coordinator = new Coordinator("sequential");

        // Create an ExecutorService to run servers in separate threads
        ExecutorService executor = Executors.newFixedThreadPool(6);

        // Get server socket addresses from the coordinator
        List<InetSocketAddress> serverSocketAddresses = coordinator.getServerSocketAddressList();

        // Start the primary server and additional server instances
        for (int i = 0; i < serverSocketAddresses.size(); i++) {
            InetSocketAddress socketAddress = serverSocketAddresses.get(i);
            int port = socketAddress.getPort();
            boolean isCoordinator = (i == 0);
            executor.submit(() -> new Server(port, isCoordinator, coordinator));
        }
    }
}




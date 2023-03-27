import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class runServer {
    public static void main(String[] args) {
        // create a coordinator instance
        Coordinator coordinator = new Coordinator();
        // ask the user input for the consistency policy
        while (true) {
            System.out.println("Please enter the consistency policy: ");
            System.out.println("1. Sequential");
            System.out.println("2. Quorum");
            System.out.println("3. Read-your-Write");
            System.out.println("4. Exit");
            Scanner sc = new Scanner(System.in);
            int input = sc.nextInt();
            if (input == 1) {
                coordinator.setConsistencyPolicy("Sequential");
                System.out.println("Consistency Policy: " + coordinator.getConsistencyPolicy());
                break;
            } else if (input == 2) {
                coordinator.setConsistencyPolicy("Quorum");
                System.out.println("Consistency Policy: " + coordinator.getConsistencyPolicy());
                break;
            } else if (input == 3) {
                coordinator.setConsistencyPolicy("Read-your-Write");
                System.out.println("Consistency Policy: " + coordinator.getConsistencyPolicy());
                break;
            } else if (input == 4) {
                System.exit(0);
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }

        // Create an ExecutorService to run servers in separate threads
        ExecutorService executor = Executors.newFixedThreadPool(6);

        // Get server socket addresses from the coordinator
        List<InetSocketAddress> serverSocketAddresses = coordinator.getServerSocketAddressList();

        // Start the primary server and additional server instances
        for (int i = 0; i < serverSocketAddresses.size(); i++) {
            InetSocketAddress socketAddress = serverSocketAddresses.get(i);
            int port = socketAddress.getPort();
            boolean isCoordinator = (i == 0); // first server is the coordinator
            executor.submit(() -> new Server(port, isCoordinator, coordinator));
        }
    }
}




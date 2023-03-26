package Server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class GroupServerMain {

    public static void main(String[] args) {

        try {
            // Start the RMI registry on the server
            LocateRegistry.createRegistry(1099);

            // Bind the remote object to the registry
            GroupServer groupServer = new GroupServerImpl();
            Naming.rebind("GroupServer", groupServer);

            System.out.println("Group Server ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

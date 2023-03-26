package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Client.Client;

public class UDPReceiver implements Runnable {
    private Client client;
    private int port;
    private InetAddress address;
    private DatagramSocket socket;

    private String currentMessage;

    public int count = 0;
    private boolean running;

    // public String getCurrentMessage() {
    //     return currentMessage;
    // }

    // private void setCurrentMessage(String currentMessage) {
    //     this.currentMessage = currentMessage;
    // }
    public UDPReceiver (int port, InetAddress address, Client client){
        this.port = port;
        this.address = address;
        this.currentMessage = "No message received yet.";
        this.client = client;
    }
    public void start() throws IOException {
        socket = new DatagramSocket(port);
        new Thread(this).start();
    }

    public void run() {
        running = true;
        while (running == true) {
            try {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                // process the received message
                client.setCurrentMessage(message);
                //client.setUdpCount(client.getUdpCount() + 1);
                client.addoneUDPCount();
                System.out.println("Current UDP count: " + client.getUDPCount());
                System.out.println("Client " + port + " received message: " + message);
                stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        socket.close();
    }
}


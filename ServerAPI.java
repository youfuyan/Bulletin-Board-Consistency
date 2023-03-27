import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class ServerAPI {
    private SocketAddress address;

    public ServerAPI(SocketAddress address) {
        this.address = address;
    }

    public void sendArticle(Article article) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(address);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(article);
            oos.flush();
        }
    }

    public Article receiveArticle() throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket()) {
            socket.connect(address);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (Article) ois.readObject();
        }
    }
}

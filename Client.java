import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Client {
    private JFrame frame;
    private JTextField postTextField;
    private JTextArea articlesTextArea;
    private JComboBox<String> serverList;
    private List<String> serverAddresses;
    private JList<Article> articleList;
    private DefaultListModel<Article> listModel;
    private JButton btnReply;
    private Article selectedArticle;
    private JButton btnPrevPage;
    private JButton btnNextPage;

    private JLabel connectionStatusLabel;
    private Socket connectedSocket;
    private int currentPage;
    private int articlesPerPage = 5;

    private long operationTime;

    private Coordinator coordinator = new Coordinator();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Client client = new Client();
                client.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Client() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 1000, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JLabel lblServer = new JLabel("Server:");
        panel.add(lblServer);

        serverList = new JComboBox<>();
        panel.add(serverList);

        serverAddresses = coordinator.getServerAddresses();
        // Add your server addresses and ports here

        for (String address : serverAddresses) {
            serverList.addItem(address);
        }

        // Add a JLabel to display the connection status
        connectionStatusLabel = new JLabel("Not connected");
        panel.add(connectionStatusLabel);
        // Add a "Connect" button
        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(e -> {
            if (connectedSocket != null && !connectedSocket.isClosed()) {
                disconnectFromServer();
                btnConnect.setText("Connect");
            } else {
                connectAndUpdate();
                btnConnect.setText("Disconnect");
            }
        });
        panel.add(btnConnect);

        JButton btnPost = new JButton("Post");
        btnPost.addActionListener(e -> postArticle());
        panel.add(btnPost);


        btnReply = new JButton("Reply");
        btnReply.addActionListener(e -> replyToArticle());
        btnReply.setEnabled(false);
        panel.add(btnReply);

        JButton btnRead = new JButton("Read");
        btnRead.addActionListener(e -> readArticle());
        btnRead.setEnabled(false);
        panel.add(btnRead);

        JSplitPane splitPane = new JSplitPane();
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        splitPane.setLeftComponent(leftPanel);

        JScrollPane scrollPane = new JScrollPane();
        frame.getContentPane().add(scrollPane);

        listModel = new DefaultListModel<>();
        articleList = new JList<>(listModel);
        articleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        articleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedArticle = articleList.getSelectedValue();
                btnReply.setEnabled(selectedArticle != null);
                btnRead.setEnabled(selectedArticle != null);
                displayArticleContent(selectedArticle);
            }
        });
        scrollPane.setViewportView(articleList);

        // Create a JPanel to hold the next and previous buttons
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Create the previous and next buttons
        btnPrevPage = new JButton("Previous");
        btnNextPage  = new JButton("Next");

        // Add action listeners for the buttons
        btnPrevPage.addActionListener(e -> prevPage());
        btnNextPage.addActionListener(e -> nextPage());

        // Add the buttons to the navigation panel
        navigationPanel.add(btnPrevPage);
        navigationPanel.add(btnNextPage);

        // Add the navigation panel to the frame
        frame.getContentPane().add(navigationPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane2 = new JScrollPane();
        splitPane.setRightComponent(scrollPane2);

        articlesTextArea = new JTextArea();
        articlesTextArea.setEditable(false);
        scrollPane2.setViewportView(articlesTextArea);

        currentPage = 0;
//        refreshArticleList();

    }


    private void postArticle() {
        // Create a JPanel with a GridLayout to hold both the title and content fields
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JLabel titleLabel = new JLabel("Enter the title of the article:");
        JTextField titleField = new JTextField(10);
        JLabel contentLabel = new JLabel("Enter the content of the article:");
        JTextArea contentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);

        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(contentLabel);
        panel.add(contentScrollPane);

        // Show the input dialog with the panel
        int result = JOptionPane.showConfirmDialog(null, panel, "Post an article", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // If the user clicks OK, proceed with posting the article
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Both title and content must be provided.");
                return;
            }
            // Wrap the code block with a timer to measure the time it takes to post an article
            long startTime = System.nanoTime();
            try {
                Socket socket = connectToServer();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("POST_ARTICLE");
                out.println(title);
                out.println(content);

                String response = in.readLine();
                if (response.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(null, "Article posted successfully!");
                    refreshArticleList();

                } else {
                    JOptionPane.showMessageDialog(null, "Failed to post article. Please try again.");
                }

                in.close();
                out.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long endTime = System.nanoTime();
            operationTime = endTime - startTime;
            saveOperationTime(connectedSocket.getLocalPort(), "post",operationTime);
        }
    }


    private void replyToArticle() {
        if (selectedArticle == null) {
            JOptionPane.showMessageDialog(null, "No article selected to reply to.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JLabel titleLabel = new JLabel("Enter the title of the reply:");
        JTextField titleField = new JTextField(10);
        JLabel contentLabel = new JLabel("Enter the content of the reply:");
        JTextArea contentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);

        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(contentLabel);
        panel.add(contentScrollPane);

        int result = JOptionPane.showConfirmDialog(null, panel, "Reply to an article", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Both title and content must be provided.");
                return;
            }
            // Wrap the code block with a timer to measure the time it takes to post a reply
            long startTime = System.nanoTime();
            try {
                Socket socket = connectToServer();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("REPLY_ARTICLE " + selectedArticle.getId());
                out.println(title);
                out.println(content);

                String response = in.readLine();
                if (response.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(null, "Reply posted successfully!");
                    refreshArticleList();

                } else {
                    JOptionPane.showMessageDialog(null, "Failed to post reply. Please try again.");
                }

                in.close();
                out.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            long endTime = System.nanoTime();
            operationTime = endTime - startTime;
            saveOperationTime(connectedSocket.getLocalPort(), "reply",operationTime);
        }

    }

    private void readArticle() {
        if (selectedArticle == null) {
            return;
        }

        // Create a new JPanel for displaying the article title and content
        JPanel readPanel = new JPanel();
        readPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Title: " + selectedArticle.getTitle());
        readPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea contentArea = new JTextArea(selectedArticle.getContent());
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setPreferredSize(new Dimension(400, 300));
        JScrollPane scrollPane = new JScrollPane(contentArea);
        readPanel.add(scrollPane, BorderLayout.CENTER);

        // Show the readPanel in a new JFrame or JDialog
        JFrame readFrame = new JFrame("Read Article");
        readFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        readFrame.setContentPane(readPanel);
        readFrame.pack();
        readFrame.setLocationRelativeTo(null); // Center the frame
        readFrame.setVisible(true);
    }


    private void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            refreshArticleList();
        }
    }

    private void nextPage() {
        currentPage++;
        refreshArticleList();
    }

    private void refreshArticleList() {
        // Wrap the code block with a timer to measure the time it takes to fetch articles
        long startTime = System.nanoTime();
        try {
            Socket socket = connectToServer();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send a command to the server to fetch articles for the current page
            out.println("FETCH_ARTICLES " + currentPage * articlesPerPage + " " + articlesPerPage);
            listModel.clear();
            List<Article> articles = new ArrayList<>();
            String line;
            while (!(line = in.readLine()).equals("END")) {
                String[] parts = line.split("\\|");
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String content = parts[2];
                int parentId = Integer.parseInt(parts[3]);
                int indentLevel = Integer.parseInt(parts[4]);
                Article article = new Article(id, title, content, parentId,indentLevel);
                articles.add(article);
            }
            displayArticlesWithIndentation(articles);
            // Debugging information
            System.out.println("Current Page: " + currentPage);
            System.out.println("Articles fetched: " + articles.size());
            for (Article article : articles) {
                System.out.println(article.getId() + " | " + article.getTitle());
            }

            // Update page navigation buttons
            btnPrevPage.setEnabled(currentPage > 0);
            btnNextPage.setEnabled(listModel.size() == articlesPerPage);

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        operationTime = endTime - startTime;
        saveOperationTime(connectedSocket.getLocalPort(), "fetch",operationTime);
    }

    private void displayArticleContent(Article article) {
        if (article == null) {
            articlesTextArea.setText("");
        } else {
            articlesTextArea.setText(article.getContent());
        }
    }

    private Socket connectToServer() throws Exception {

        String selectedServer = (String) serverList.getSelectedItem();
        String[] parts = selectedServer.split(":");
        String serverAddress = parts[0];
        int serverPort = Integer.parseInt(parts[1]);

        // Add random delay to emulate propagation delay
        Random random = new Random();
        int delay = random.nextInt(30);
        Thread.sleep(delay);

        return new Socket(serverAddress, serverPort);
    }
    private void disconnectFromServer() {
        if (connectedSocket != null && !connectedSocket.isClosed()) {
            try {
                connectedSocket.close();
                connectedSocket = null;
                connectionStatusLabel.setText("Disconnected");
            } catch (IOException e) {
                connectionStatusLabel.setText("Disconnection failed");
                e.printStackTrace();
            }
        }
    }

    private void displayArticlesWithIndentation(List<Article> articles) {
        for (Article article : articles) {
            int indentationLevel = 0;
            Article currentArticle = article;
            while (currentArticle != null && currentArticle.isReply()) {
                indentationLevel++;
                currentArticle = findArticleById(articles, currentArticle.getParentId());
            }
            listModel.addElement(article);
        }
    }

    private void connectAndUpdate() {
        try {
            connectedSocket = connectToServer();
            refreshArticleList();
            connectionStatusLabel.setText("Connected to: " + serverList.getSelectedItem());
        } catch (Exception ex) {
            connectionStatusLabel.setText("Connection failed");
            ex.printStackTrace();
        }
    }

    private Article findArticleById(List<Article> articles, int id) {
        for (Article article : articles) {
            if (article.getId() == id) {
                return article;
            }
        }
        return null;
    }

    private void saveOperationTime(int serverPort, String operation, long operationTime) {
        String filename = "OperationTime_" + "Server" + serverPort + ".txt";
        try (FileWriter fw = new FileWriter(filename, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(operation + ", " + operationTime/1000000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

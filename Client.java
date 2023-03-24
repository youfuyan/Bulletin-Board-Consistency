import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private int currentPage;
    private int articlesPerPage = 10;

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
        frame.setBounds(100, 100, 700, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JLabel lblServer = new JLabel("Server:");
        panel.add(lblServer);

        serverList = new JComboBox<>();
        panel.add(serverList);
        serverAddresses = new ArrayList<>();
        // Add your server addresses and ports here
        serverAddresses.add("127.0.0.1:8000");
        serverAddresses.add("127.0.0.1:8001");
        for (String address : serverAddresses) {
            serverList.addItem(address);
        }

//        postTextField = new JTextField();
//        panel.add(postTextField);
//        postTextField.setColumns(20);

        JButton btnPost = new JButton("Post");
        btnPost.addActionListener(e -> postArticle());
        panel.add(btnPost);


        btnReply = new JButton("Reply");
        btnReply.addActionListener(e -> replyToArticle());
        btnReply.setEnabled(false);
        panel.add(btnReply);

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
                displayArticleContent(selectedArticle);
            }
        });
        scrollPane.setViewportView(articleList);

        // Create a JPanel to hold the next and previous buttons
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Create the previous and next buttons
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        // Add action listeners for the buttons
        prevButton.addActionListener(e -> prevPage());
        nextButton.addActionListener(e -> nextPage());

        // Add the buttons to the navigation panel
        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);

        // Add the navigation panel to the frame
        frame.getContentPane().add(navigationPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane2 = new JScrollPane();
        splitPane.setRightComponent(scrollPane2);

        articlesTextArea = new JTextArea();
        articlesTextArea.setEditable(false);
        scrollPane2.setViewportView(articlesTextArea);

        currentPage = 0;
        refreshArticleList();

    }

//    private void postArticle() {
//        String title = JOptionPane.showInputDialog("Enter the title of the article:");
//        if (title == null || title.trim().isEmpty()) {
//            return;
//        }
//
//        String content = JOptionPane.showInputDialog("Enter the content of the article:");
//        if (content == null || content.trim().isEmpty()) {
//            return;
//        }
//
//        try {
//            Socket socket = connectToServer();
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            out.println("POST_ARTICLE");
//            out.println(title);
//            out.println(content);
//
//            String response = in.readLine();
//            if (response.equals("SUCCESS")) {
//                JOptionPane.showMessageDialog(null, "Article posted successfully!");
//                refreshArticleList();
//            } else {
//                JOptionPane.showMessageDialog(null, "Failed to post article. Please try again.");
//            }
//
//            in.close();
//            out.close();
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
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
    }
}


    private void replyToArticle() {
        // Implement the "Reply to an existing article" feature
        // Get the selected server

        //Todo: Implement the "Reply to an existing article" feature


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
        try {
            Socket socket = connectToServer();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send a command to the server to fetch articles for the current page
            out.println("FETCH_ARTICLES " + currentPage * articlesPerPage + " " + articlesPerPage);
            listModel.clear();
            String line;
            while (!(line = in.readLine()).equals("END")) {
                String[] parts = line.split("\\|");
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String content = parts[2];
                int parentId = Integer.parseInt(parts[3]);
                Article article = new Article(id, title, content, parentId);
                listModel.addElement(article);
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
    }

    private Socket connectToServer() throws Exception {
        String selectedServer = (String) serverList.getSelectedItem();
        String[] parts = selectedServer.split(":");
        String serverAddress = parts[0];
        int serverPort = Integer.parseInt(parts[1]);

        // Add random delay to emulate propagation delay
        Random random = new Random();
        int delay = random.nextInt(3000);
        Thread.sleep(delay);

        return new Socket(serverAddress, serverPort);
    }
    private void displayArticleContent(Article article) {
        if (article == null) {
            articlesTextArea.setText("");
        } else {
            articlesTextArea.setText(article.getContent());
        }
    }
}

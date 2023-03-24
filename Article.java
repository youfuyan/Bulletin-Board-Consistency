/***
 * Article.java
 * The Article class contains the fields id, title, content, and parentId.
 * The parentId field is used to represent the relationship between articles and their replies.
 * If parentId is greater than 0, it means the current article is a reply to another article with the given parent ID.
 * The isReply() method checks whether the current article is a reply or not.
 * The toString() method returns a formatted string representing the article,
 * with a prefix that indicates if it's a reply or not.
 ***/
public class Article {
    private int id;
    private String title;
    private String content;
    private int parentId;

    public Article(int id, String title, String content, int parentId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isReply() {
        return parentId > 0;
    }

    @Override
    public String toString() {
        String prefix = isReply() ? "Reply to Article " + parentId : "Article";
        return String.format("%d %s: %s", id, prefix, title);
    }
}

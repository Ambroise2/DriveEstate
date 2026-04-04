package ke.driveestate.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    private String title;
    @Column(columnDefinition = "TEXT") private String message;
    private String type = "info";
    private String link;
    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Notification n = new Notification();
        public Builder user(User v)    { n.user = v; return this; }
        public Builder title(String v) { n.title = v; return this; }
        public Builder message(String v){ n.message = v; return this; }
        public Builder type(String v)  { n.type = v; return this; }
        public Builder link(String v)  { n.link = v; return this; }
        public Notification build() { return n; }
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User v) { user = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { message = v; }
    public String getType() { return type; }
    public void setType(String v) { type = v; }
    public String getLink() { return link; }
    public void setLink(String v) { link = v; }
    public boolean isRead() { return read; }
    public void setRead(boolean v) { read = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

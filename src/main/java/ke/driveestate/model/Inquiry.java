package ke.driveestate.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "inquiries")
public class Inquiry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "listing_id") private Listing listing;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sender_id") private User sender;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "receiver_id") private User receiver;
    private String subject;
    @Column(columnDefinition = "TEXT") private String message;
    @Column(columnDefinition = "TEXT") private String reply;
    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime repliedAt;

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Inquiry i = new Inquiry();
        public Builder listing(Listing v)  { i.listing = v; return this; }
        public Builder sender(User v)      { i.sender = v; return this; }
        public Builder receiver(User v)    { i.receiver = v; return this; }
        public Builder subject(String v)   { i.subject = v; return this; }
        public Builder message(String v)   { i.message = v; return this; }
        public Inquiry build() { return i; }
    }
    public Long getId() { return id; }
    public Listing getListing() { return listing; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getReply() { return reply; }
    public void setReply(String v) { reply = v; }
    public boolean isRead() { return read; }
    public void setRead(boolean v) { read = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime v) { repliedAt = v; }
}

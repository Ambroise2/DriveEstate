package ke.driveestate.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "activity_logs")
public class ActivityLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long userId;
    private String userName;
    private String action;
    @Column(columnDefinition = "TEXT") private String details;
    private String ipAddress;
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final ActivityLog a = new ActivityLog();
        public Builder userId(Long v)    { a.userId = v; return this; }
        public Builder userName(String v){ a.userName = v; return this; }
        public Builder action(String v)  { a.action = v; return this; }
        public Builder details(String v) { a.details = v; return this; }
        public Builder ipAddress(String v){ a.ipAddress = v; return this; }
        public ActivityLog build() { return a; }
    }
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

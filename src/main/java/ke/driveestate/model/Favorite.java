package ke.driveestate.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","listing_id"}))
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "listing_id") private Listing listing;
    private LocalDateTime savedAt = LocalDateTime.now();

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Favorite f = new Favorite();
        public Builder user(User v)      { f.user = v; return this; }
        public Builder listing(Listing v){ f.listing = v; return this; }
        public Favorite build() { return f; }
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Listing getListing() { return listing; }
    public LocalDateTime getSavedAt() { return savedAt; }
}

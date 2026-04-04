package ke.driveestate.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(unique = true, nullable = false) private String email;
    @Column(nullable = false) private String phone;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) private Role role = Role.CLIENT;
    private boolean active = true;
    private boolean verified = false;
    private String county;
    private String bio;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Listing> listings = new ArrayList<>();
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inquiry> sentInquiries = new ArrayList<>();
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inquiry> receivedInquiries = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    // ── UserDetails ──────────────────────────────────────────────────────────
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return active; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }

    // ── Helpers ──────────────────────────────────────────────────────────────
    public boolean isAdmin() { return role == Role.ADMIN; }
    public long getActiveListingCount() {
        return listings.stream().filter(l -> l.getStatus() == ListingStatus.ACTIVE).count();
    }
    public long getUnreadNotificationCount() {
        return notifications.stream().filter(n -> !n.isRead()).count();
    }
    public char getInitial() { return (name != null && !name.isEmpty()) ? name.charAt(0) : '?'; }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final User u = new User();
        public Builder name(String v)      { u.name = v; return this; }
        public Builder email(String v)     { u.email = v; return this; }
        public Builder phone(String v)     { u.phone = v; return this; }
        public Builder password(String v)  { u.password = v; return this; }
        public Builder role(Role v)        { u.role = v; return this; }
        public Builder active(boolean v)   { u.active = v; return this; }
        public Builder verified(boolean v) { u.verified = v; return this; }
        public Builder county(String v)    { u.county = v; return this; }
        public Builder bio(String v)       { u.bio = v; return this; }
        public User build() { return u; }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime v) { this.lastLogin = v; }
    public List<Listing> getListings() { return listings; }
    public List<Inquiry> getSentInquiries() { return sentInquiries; }
    public List<Inquiry> getReceivedInquiries() { return receivedInquiries; }
    public List<Favorite> getFavorites() { return favorites; }
    public List<Notification> getNotifications() { return notifications; }
}

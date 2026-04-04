package ke.driveestate.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "listings")
public class Listing {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String title;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ListingType listingType;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id") private Category category;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal price;
    @Column(columnDefinition = "TEXT") private String description;
    private String location;
    private String county;
    @Enumerated(EnumType.STRING) private ListingStatus status = ListingStatus.PENDING;
    private boolean featured = false;
    private boolean verified = false;
    private boolean negotiable = false;
    private boolean installmentAvailable = false;
    private int views = 0;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "seller_id", nullable = false) private User seller;
    @Column(columnDefinition = "TEXT") private String adminNotes;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    // Car fields
    private String make;
    private String model;
    @Column(name = "manufacture_year") private Integer year;
    private Integer mileage;
    private String fuelType;
    private String transmission;
    private Integer engineCc;
    private String color;
    private String condition;
    private String driveType;
    private Integer doors;
    private Integer seats;

    // Land fields
    private Double sizeAcres;
    private Double sizeSqft;
    private String zoning;
    private String roadAccess;
    private Boolean waterAvailable;
    private Boolean electricityAvailable;
    private Boolean fenced;
    private String titleType;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inquiry> inquiries = new ArrayList<>();
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites = new ArrayList<>();

    // ── Helpers ──────────────────────────────────────────────────────────────
    public String getFormattedPrice() {
        if (price == null) return "KES 0";
        return "KES " + NumberFormat.getNumberInstance(Locale.US).format(price.longValue());
    }
    public String getTypeIcon() { return listingType == ListingType.CAR ? "🚗" : "🌍"; }
    public int getTotalInquiries() { return inquiries.size(); }
    public int getTotalFavorites() { return favorites.size(); }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Listing l = new Listing();
        public Builder title(String v)                  { l.title = v; return this; }
        public Builder listingType(ListingType v)        { l.listingType = v; return this; }
        public Builder category(Category v)              { l.category = v; return this; }
        public Builder price(BigDecimal v)               { l.price = v; return this; }
        public Builder description(String v)             { l.description = v; return this; }
        public Builder location(String v)                { l.location = v; return this; }
        public Builder county(String v)                  { l.county = v; return this; }
        public Builder status(ListingStatus v)           { l.status = v; return this; }
        public Builder featured(boolean v)               { l.featured = v; return this; }
        public Builder verified(boolean v)               { l.verified = v; return this; }
        public Builder negotiable(boolean v)             { l.negotiable = v; return this; }
        public Builder installmentAvailable(boolean v)   { l.installmentAvailable = v; return this; }
        public Builder views(int v)                      { l.views = v; return this; }
        public Builder seller(User v)                    { l.seller = v; return this; }
        public Builder make(String v)                    { l.make = v; return this; }
        public Builder model(String v)                   { l.model = v; return this; }
        public Builder year(Integer v)                   { l.year = v; return this; }
        public Builder mileage(Integer v)                { l.mileage = v; return this; }
        public Builder fuelType(String v)                { l.fuelType = v; return this; }
        public Builder transmission(String v)            { l.transmission = v; return this; }
        public Builder engineCc(Integer v)               { l.engineCc = v; return this; }
        public Builder color(String v)                   { l.color = v; return this; }
        public Builder condition(String v)               { l.condition = v; return this; }
        public Builder driveType(String v)               { l.driveType = v; return this; }
        public Builder doors(Integer v)                  { l.doors = v; return this; }
        public Builder seats(Integer v)                  { l.seats = v; return this; }
        public Builder sizeAcres(Double v)               { l.sizeAcres = v; return this; }
        public Builder sizeSqft(Double v)                { l.sizeSqft = v; return this; }
        public Builder zoning(String v)                  { l.zoning = v; return this; }
        public Builder roadAccess(String v)              { l.roadAccess = v; return this; }
        public Builder waterAvailable(Boolean v)         { l.waterAvailable = v; return this; }
        public Builder electricityAvailable(Boolean v)   { l.electricityAvailable = v; return this; }
        public Builder fenced(Boolean v)                 { l.fenced = v; return this; }
        public Builder titleType(String v)               { l.titleType = v; return this; }
        public Listing build() { return l; }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType v) { listingType = v; }
    public Category getCategory() { return category; }
    public void setCategory(Category v) { category = v; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal v) { price = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public String getLocation() { return location; }
    public void setLocation(String v) { location = v; }
    public String getCounty() { return county; }
    public void setCounty(String v) { county = v; }
    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus v) { status = v; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean v) { featured = v; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean v) { verified = v; }
    public boolean isNegotiable() { return negotiable; }
    public void setNegotiable(boolean v) { negotiable = v; }
    public boolean isInstallmentAvailable() { return installmentAvailable; }
    public void setInstallmentAvailable(boolean v) { installmentAvailable = v; }
    public int getViews() { return views; }
    public void setViews(int v) { views = v; }
    public User getSeller() { return seller; }
    public void setSeller(User v) { seller = v; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String v) { adminNotes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { updatedAt = v; }
    public String getMake() { return make; }
    public void setMake(String v) { make = v; }
    public String getModel() { return model; }
    public void setModel(String v) { model = v; }
    public Integer getYear() { return year; }
    public void setYear(Integer v) { year = v; }
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer v) { mileage = v; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String v) { fuelType = v; }
    public String getTransmission() { return transmission; }
    public void setTransmission(String v) { transmission = v; }
    public Integer getEngineCc() { return engineCc; }
    public void setEngineCc(Integer v) { engineCc = v; }
    public String getColor() { return color; }
    public void setColor(String v) { color = v; }
    public String getCondition() { return condition; }
    public void setCondition(String v) { condition = v; }
    public String getDriveType() { return driveType; }
    public void setDriveType(String v) { driveType = v; }
    public Integer getDoors() { return doors; }
    public void setDoors(Integer v) { doors = v; }
    public Integer getSeats() { return seats; }
    public void setSeats(Integer v) { seats = v; }
    public Double getSizeAcres() { return sizeAcres; }
    public void setSizeAcres(Double v) { sizeAcres = v; }
    public Double getSizeSqft() { return sizeSqft; }
    public void setSizeSqft(Double v) { sizeSqft = v; }
    public String getZoning() { return zoning; }
    public void setZoning(String v) { zoning = v; }
    public String getRoadAccess() { return roadAccess; }
    public void setRoadAccess(String v) { roadAccess = v; }
    public Boolean getWaterAvailable() { return waterAvailable; }
    public void setWaterAvailable(Boolean v) { waterAvailable = v; }
    public Boolean getElectricityAvailable() { return electricityAvailable; }
    public void setElectricityAvailable(Boolean v) { electricityAvailable = v; }
    public Boolean getFenced() { return fenced; }
    public void setFenced(Boolean v) { fenced = v; }
    public String getTitleType() { return titleType; }
    public void setTitleType(String v) { titleType = v; }
    public List<Inquiry> getInquiries() { return inquiries; }
    public List<Favorite> getFavorites() { return favorites; }
}

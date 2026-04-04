package ke.driveestate.model;
import jakarta.persistence.*;

@Entity @Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String listingType;
    private String icon = "📋";

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final Category c = new Category();
        public Builder name(String v)        { c.name = v; return this; }
        public Builder listingType(String v) { c.listingType = v; return this; }
        public Builder icon(String v)        { c.icon = v; return this; }
        public Category build() { return c; }
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String v) { name = v; }
    public String getListingType() { return listingType; }
    public void setListingType(String v) { listingType = v; }
    public String getIcon() { return icon; }
    public void setIcon(String v) { icon = v; }
}

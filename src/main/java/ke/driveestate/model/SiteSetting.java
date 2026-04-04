package ke.driveestate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "site_settings")
public class SiteSetting {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setting_key", unique = true, nullable = false)
    private String key;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "setting_label")
    private String label;

    @Column(name = "setting_category")
    private String category;

    @Column(name = "input_type")
    private String inputType;

    @Column(name = "sort_order")
    private int sortOrder = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public SiteSetting() {}
    public SiteSetting(String key, String value, String label, String category, String inputType, int sortOrder) {
        this.key = key; this.value = value; this.label = label;
        this.category = category; this.inputType = inputType; this.sortOrder = sortOrder;
    }

    public Long getId() { return id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; this.updatedAt = LocalDateTime.now(); }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getInputType() { return inputType; }
    public void setInputType(String inputType) { this.inputType = inputType; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

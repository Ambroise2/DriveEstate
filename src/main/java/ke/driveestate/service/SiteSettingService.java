package ke.driveestate.service;

import ke.driveestate.model.SiteSetting;
import ke.driveestate.repository.SiteSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SiteSettingService {

    private final SiteSettingRepository repo;

    public SiteSettingService(SiteSettingRepository repo) {
        this.repo = repo;
    }

    /** Get a single value, with fallback default */
    public String get(String key, String defaultValue) {
        return repo.findByKey(key).map(SiteSetting::getValue).orElse(defaultValue);
    }

    /** Get all settings as a flat key→value map for easy template use */
    public Map<String, String> getAll() {
        Map<String, String> map = new LinkedHashMap<>();
        repo.findAllByOrderByCategoryAscSortOrderAsc()
            .forEach(s -> map.put(s.getKey(), s.getValue()));
        return map;
    }

    /** Get all full SiteSetting objects ordered for admin display */
    public List<SiteSetting> getAllSettings() {
        return repo.findAllByOrderByCategoryAscSortOrderAsc();
    }

    /** Get settings for one category */
    public List<SiteSetting> getByCategory(String category) {
        return repo.findByCategoryOrderBySortOrder(category);
    }

    /** Save a single setting by key */
    public void set(String key, String value) {
        SiteSetting s = repo.findByKey(key).orElseThrow(
            () -> new IllegalArgumentException("Unknown setting key: " + key));
        s.setValue(value);
        repo.save(s);
    }

    /** Bulk save from the admin form (Map of key→value) */
    public void saveAll(Map<String, String> values) {
        values.forEach((key, value) -> repo.findByKey(key).ifPresent(s -> {
            s.setValue(value != null ? value.trim() : "");
            repo.save(s);
        }));
    }
}

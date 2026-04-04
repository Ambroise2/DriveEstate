package ke.driveestate.repository;

import ke.driveestate.model.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {

    Optional<SiteSetting> findByKey(String key);

    @Query("SELECT s FROM SiteSetting s WHERE s.category = :category ORDER BY s.sortOrder ASC")
    List<SiteSetting> findByCategoryOrderBySortOrder(@Param("category") String category);

    @Query("SELECT s FROM SiteSetting s ORDER BY s.category ASC, s.sortOrder ASC")
    List<SiteSetting> findAllByOrderByCategoryAscSortOrderAsc();
}

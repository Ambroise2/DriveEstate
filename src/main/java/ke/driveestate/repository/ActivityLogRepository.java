package ke.driveestate.repository;
import ke.driveestate.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}

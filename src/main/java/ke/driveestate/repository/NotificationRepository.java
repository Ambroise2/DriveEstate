package ke.driveestate.repository;
import ke.driveestate.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    long countByUserAndReadFalse(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user")
    void markAllReadForUser(User user);
}

package ke.driveestate.repository;
import ke.driveestate.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByReceiverOrderByCreatedAtDesc(User receiver);
    List<Inquiry> findBySenderOrderByCreatedAtDesc(User sender);
    Page<Inquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByReadFalse();
}

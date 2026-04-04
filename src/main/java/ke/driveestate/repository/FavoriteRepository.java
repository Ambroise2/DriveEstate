package ke.driveestate.repository;
import ke.driveestate.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndListing(User user, Listing listing);
    List<Favorite> findByUserOrderBySavedAtDesc(User user);
    boolean existsByUserAndListing(User user, Listing listing);
    void deleteByUserAndListing(User user, Listing listing);
    long countByListing(Listing listing);
}

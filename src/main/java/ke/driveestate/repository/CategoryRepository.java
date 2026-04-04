package ke.driveestate.repository;
import ke.driveestate.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByListingType(String listingType);
}

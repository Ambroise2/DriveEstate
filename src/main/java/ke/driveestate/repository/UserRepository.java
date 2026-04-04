package ke.driveestate.repository;

import ke.driveestate.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleAndActiveTrue(Role role);
    Page<User> findByRole(Role role, Pageable pageable);
    long countByRole(Role role);
    long countByVerifiedTrue();
}

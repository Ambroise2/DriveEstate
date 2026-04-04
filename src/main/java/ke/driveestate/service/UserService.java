package ke.driveestate.service;

import ke.driveestate.model.*;
import ke.driveestate.repository.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notifRepo;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder, NotificationRepository notifRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.notifRepo = notifRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public ke.driveestate.model.User register(String name, String email, String phone, String county, String rawPassword) {
        if (userRepo.existsByEmail(email)) throw new IllegalArgumentException("An account with this email already exists.");
        ke.driveestate.model.User user = ke.driveestate.model.User.builder()
            .name(name).email(email).phone(phone).county(county)
            .password(passwordEncoder.encode(rawPassword)).role(Role.CLIENT).active(true).build();
        return userRepo.save(user);
    }

    public Optional<ke.driveestate.model.User> findById(Long id) { return userRepo.findById(id); }
    public Optional<ke.driveestate.model.User> findByEmail(String e) { return userRepo.findByEmail(e); }
    public List<ke.driveestate.model.User> findAllClients() { return userRepo.findByRoleAndActiveTrue(Role.CLIENT); }
    public List<ke.driveestate.model.User> findAll() { return userRepo.findAll(); }
    public ke.driveestate.model.User save(ke.driveestate.model.User user) { return userRepo.save(user); }

    public void recordLogin(ke.driveestate.model.User user) { user.setLastLogin(LocalDateTime.now()); userRepo.save(user); }
    public void toggleActive(ke.driveestate.model.User user) { user.setActive(!user.isActive()); userRepo.save(user); }

    public void toggleVerified(ke.driveestate.model.User user) {
        user.setVerified(!user.isVerified());
        if (user.isVerified()) {
            Notification n = Notification.builder().user(user).title("Account Verified ✅")
                .message("Your DriveEstate account has been officially verified!").type("success").build();
            notifRepo.save(n);
        }
        userRepo.save(user);
    }

    public void changePassword(ke.driveestate.model.User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepo.save(user);
    }

    public void delete(ke.driveestate.model.User user) { userRepo.delete(user); }
    public long countClients() { return userRepo.countByRole(Role.CLIENT); }
    public long countVerified() { return userRepo.countByVerifiedTrue(); }
}

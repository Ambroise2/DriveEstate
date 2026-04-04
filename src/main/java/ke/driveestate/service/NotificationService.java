package ke.driveestate.service;

import ke.driveestate.model.*;
import ke.driveestate.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @Transactional
public class NotificationService {
    private final NotificationRepository notifRepo;
    public NotificationService(NotificationRepository notifRepo) { this.notifRepo = notifRepo; }
    public List<Notification> getUnread(User user) { return notifRepo.findByUserAndReadFalseOrderByCreatedAtDesc(user); }
    public void markAllRead(User user) { notifRepo.markAllReadForUser(user); }
    public long countUnread(User user) { return notifRepo.countByUserAndReadFalse(user); }
    public void broadcast(List<User> users, String title, String message, String type) {
        users.forEach(u -> notifRepo.save(Notification.builder().user(u).title(title).message(message).type(type).build()));
    }
}

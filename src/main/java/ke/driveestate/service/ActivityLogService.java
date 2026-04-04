package ke.driveestate.service;

import ke.driveestate.model.*;
import ke.driveestate.repository.ActivityLogRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
public class ActivityLogService {
    private final ActivityLogRepository logRepo;
    public ActivityLogService(ActivityLogRepository logRepo) { this.logRepo = logRepo; }
    public void log(User user, String action, String details, String ip) {
        logRepo.save(ActivityLog.builder()
            .userId(user != null ? user.getId() : null)
            .userName(user != null ? user.getName() : "anonymous")
            .action(action).details(details).ipAddress(ip).build());
    }
    public Page<ActivityLog> getAll(int page) { return logRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 30)); }
    public Page<ActivityLog> getByUser(Long userId, int page) { return logRepo.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, 20)); }
}

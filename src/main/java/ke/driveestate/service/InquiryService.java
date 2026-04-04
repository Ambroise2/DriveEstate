package ke.driveestate.service;

import ke.driveestate.model.*;
import ke.driveestate.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @Transactional
public class InquiryService {
    private final InquiryRepository inquiryRepo;
    private final NotificationRepository notifRepo;
    public InquiryService(InquiryRepository inquiryRepo, NotificationRepository notifRepo) {
        this.inquiryRepo = inquiryRepo; this.notifRepo = notifRepo;
    }
    public Inquiry send(Listing listing, User sender, User receiver, String message) {
        Inquiry inq = Inquiry.builder().listing(listing).sender(sender).receiver(receiver)
            .subject("Inquiry about: " + listing.getTitle()).message(message).build();
        inq = inquiryRepo.save(inq);
        notifRepo.save(Notification.builder().user(receiver).title("New Inquiry 💬")
            .message(sender.getName() + " sent an inquiry about '" + listing.getTitle() + "'")
            .type("info").link("/messages").build());
        return inq;
    }
    public void reply(Inquiry inquiry, String reply) {
        inquiry.setReply(reply);
        inquiry.setRepliedAt(LocalDateTime.now());
        inquiryRepo.save(inquiry);
        notifRepo.save(Notification.builder().user(inquiry.getSender()).title("Reply to your inquiry")
            .message(inquiry.getReceiver().getName() + " replied to your inquiry about '" + inquiry.getListing().getTitle() + "'")
            .type("success").build());
    }
    public List<Inquiry> receivedBy(User user) {
        List<Inquiry> list = inquiryRepo.findByReceiverOrderByCreatedAtDesc(user);
        list.forEach(i -> { if (!i.isRead()) { i.setRead(true); inquiryRepo.save(i); } });
        return list;
    }
    public List<Inquiry> sentBy(User user) { return inquiryRepo.findBySenderOrderByCreatedAtDesc(user); }
    public Page<Inquiry> all(int page) { return inquiryRepo.findAllByOrderByCreatedAtDesc(PageRequest.of(page, 20)); }
    public Optional<Inquiry> findById(Long id) { return inquiryRepo.findById(id); }
    public long countUnread() { return inquiryRepo.countByReadFalse(); }
}

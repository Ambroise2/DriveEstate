package ke.driveestate.controller;

import ke.driveestate.model.*;
import ke.driveestate.repository.CategoryRepository;
import ke.driveestate.service.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ListingService listingService;
    private final InquiryService inquiryService;
    private final NotificationService notifService;
    private final ActivityLogService logService;
    private final SiteSettingService settingService;
    private final CategoryRepository catRepo;

    public AdminController(UserService userService, ListingService listingService,
                           InquiryService inquiryService, NotificationService notifService,
                           ActivityLogService logService, SiteSettingService settingService,
                           CategoryRepository catRepo) {
        this.userService = userService; this.listingService = listingService;
        this.inquiryService = inquiryService; this.notifService = notifService;
        this.logService = logService; this.settingService = settingService;
        this.catRepo = catRepo;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────
    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsers",      userService.countClients());
        model.addAttribute("activeListings",  listingService.countActive());
        model.addAttribute("pendingCount",    listingService.countPending());
        model.addAttribute("totalInquiries",  inquiryService.countUnread());
        model.addAttribute("totalCars",       listingService.countCars());
        model.addAttribute("totalLand",       listingService.countLand());
        model.addAttribute("featuredCount",   listingService.countFeatured());
        model.addAttribute("verifiedUsers",   userService.countVerified());
        model.addAttribute("pendingItems",    listingService.adminSearch(ListingStatus.PENDING, null, null, 0).getContent());
        model.addAttribute("recentLogs",      logService.getAll(0).getContent());
        LocalDate today = LocalDate.now();
        List<String> labels = new ArrayList<>();
        List<Integer> cars = new ArrayList<>(), land = new ArrayList<>();
        for (int i = 13; i >= 0; i--) {
            labels.add(today.minusDays(i).getMonth().name().substring(0,3) + " " + today.minusDays(i).getDayOfMonth());
            cars.add((int)(listingService.countCars() / 14));
            land.add((int)(listingService.countLand() / 14));
        }
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartCars", cars);
        model.addAttribute("chartLand", land);
        model.addAttribute("recentUsers", userService.findAll().stream().filter(u -> !u.isAdmin())
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).limit(8).toList());
        return "admin/dashboard";
    }

    // ── Users ─────────────────────────────────────────────────────────────────
    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String q, Model model) {
        List<User> users = userService.findAll().stream().filter(u -> !u.isAdmin())
            .filter(u -> q == null || u.getName().toLowerCase().contains(q.toLowerCase()) || u.getEmail().toLowerCase().contains(q.toLowerCase()))
            .toList();
        model.addAttribute("users", users); model.addAttribute("q", q);
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("listings", listingService.bySellerAll(user));
        model.addAttribute("logs", logService.getByUser(id, 0).getContent());
        return "admin/user_detail";
    }

    @PostMapping("/users/{id}/toggle-active")
    public String toggleUserActive(@PathVariable Long id, RedirectAttributes ra) {
        User user = userService.findById(id).orElseThrow();
        if (user.isAdmin()) { ra.addFlashAttribute("error", "Cannot modify admin."); return "redirect:/admin/users"; }
        userService.toggleActive(user); ra.addFlashAttribute("success", "User status updated.");
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/users/{id}/toggle-verify")
    public String toggleUserVerify(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleVerified(userService.findById(id).orElseThrow());
        ra.addFlashAttribute("success", "Verification status updated."); return "redirect:/admin/users/" + id;
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        User user = userService.findById(id).orElseThrow();
        if (user.isAdmin()) { ra.addFlashAttribute("error", "Cannot delete admin."); return "redirect:/admin/users"; }
        userService.delete(user); ra.addFlashAttribute("success", "User deleted."); return "redirect:/admin/users";
    }

    // ── Listings ──────────────────────────────────────────────────────────────
    @GetMapping("/listings")
    public String listings(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String status,
                           @RequestParam(required = false) String type, @RequestParam(required = false) String q, Model model) {
        ListingStatus ls = null;
        try { if (status != null && !status.isBlank()) ls = ListingStatus.valueOf(status.toUpperCase()); } catch (Exception ignored) {}
        ListingType lt = "car".equalsIgnoreCase(type) ? ListingType.CAR : "land".equalsIgnoreCase(type) ? ListingType.LAND : null;
        Page<Listing> results = listingService.adminSearch(ls, lt, q, page);
        model.addAttribute("listings", results.getContent());
        model.addAttribute("currentPage", page); model.addAttribute("totalPages", results.getTotalPages());
        model.addAttribute("totalItems", results.getTotalElements());
        model.addAttribute("statusFilter", status); model.addAttribute("typeFilter", type); model.addAttribute("q", q);
        return "admin/listings";
    }

    @GetMapping("/listings/{id}")
    public String listingDetail(@PathVariable Long id, Model model) {
        Listing l = listingService.findById(id).orElseThrow();
        model.addAttribute("listing", l);
        model.addAttribute("inquiries", inquiryService.receivedBy(l.getSeller()).stream()
            .filter(i -> i.getListing().getId().equals(id)).toList());
        return "admin/listing_detail";
    }

    @PostMapping("/listings/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam(required = false, defaultValue = "") String notes, RedirectAttributes ra) {
        Listing l = listingService.findById(id).orElseThrow(); listingService.approve(l, notes);
        ra.addFlashAttribute("success", "Listing approved."); return "redirect:/admin/listings?status=PENDING";
    }

    @PostMapping("/listings/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(required = false, defaultValue = "Did not meet standards.") String reason, RedirectAttributes ra) {
        Listing l = listingService.findById(id).orElseThrow(); listingService.reject(l, reason);
        ra.addFlashAttribute("success", "Listing rejected."); return "redirect:/admin/listings?status=PENDING";
    }

    @PostMapping("/listings/{id}/toggle-feature")
    public String toggleFeature(@PathVariable Long id, RedirectAttributes ra) {
        listingService.toggleFeatured(listingService.findById(id).orElseThrow());
        ra.addFlashAttribute("success", "Featured status updated."); return "redirect:/admin/listings/" + id;
    }

    @PostMapping("/listings/{id}/toggle-verify")
    public String toggleVerifyListing(@PathVariable Long id, RedirectAttributes ra) {
        listingService.toggleVerified(listingService.findById(id).orElseThrow());
        ra.addFlashAttribute("success", "Verification updated."); return "redirect:/admin/listings/" + id;
    }

    @PostMapping("/listings/{id}/mark-sold")
    public String markSold(@PathVariable Long id, RedirectAttributes ra) {
        listingService.markSold(listingService.findById(id).orElseThrow());
        ra.addFlashAttribute("success", "Marked as sold."); return "redirect:/admin/listings/" + id;
    }

    @PostMapping("/listings/{id}/delete")
    public String deleteListing(@PathVariable Long id, RedirectAttributes ra) {
        Listing l = listingService.findById(id).orElseThrow(); String title = l.getTitle();
        listingService.delete(l); ra.addFlashAttribute("success", "'" + title + "' deleted."); return "redirect:/admin/listings";
    }

    @PostMapping("/quick-approve/{id}") @ResponseBody
    public ResponseEntity<Map<String,Object>> quickApprove(@PathVariable Long id) {
        listingService.approve(listingService.findById(id).orElseThrow(), "Quick approved.");
        return ResponseEntity.ok(Map.of("status","approved","id",id));
    }

    @PostMapping("/quick-reject/{id}") @ResponseBody
    public ResponseEntity<Map<String,Object>> quickReject(@PathVariable Long id) {
        listingService.reject(listingService.findById(id).orElseThrow(), "Rejected by admin.");
        return ResponseEntity.ok(Map.of("status","rejected","id",id));
    }

    // ── Inquiries ─────────────────────────────────────────────────────────────
    @GetMapping("/inquiries")
    public String inquiries(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Inquiry> results = inquiryService.all(page);
        model.addAttribute("inquiries", results.getContent());
        model.addAttribute("currentPage", page); model.addAttribute("totalPages", results.getTotalPages());
        return "admin/inquiries";
    }

    // ── Reports ───────────────────────────────────────────────────────────────
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("countyStats", listingService.countyStats());
        model.addAttribute("priceRanges", listingService.priceRangeStats());
        model.addAttribute("fuelStats", listingService.fuelStats());
        model.addAttribute("topSellers", userService.findAllClients().stream()
            .sorted(Comparator.comparingLong(User::getActiveListingCount).reversed()).limit(10).toList());
        return "admin/reports";
    }

    // ── Logs ──────────────────────────────────────────────────────────────────
    @GetMapping("/logs")
    public String logs(@RequestParam(defaultValue = "0") int page, Model model) {
        var results = logService.getAll(page);
        model.addAttribute("logs", results.getContent());
        model.addAttribute("currentPage", page); model.addAttribute("totalPages", results.getTotalPages());
        return "admin/logs";
    }

    // ── Settings ──────────────────────────────────────────────────────────────
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("allSettings", settingService.getAllSettings());
        model.addAttribute("generalSettings",  settingService.getByCategory("general"));
        model.addAttribute("contactSettings",  settingService.getByCategory("contact"));
        model.addAttribute("socialSettings",   settingService.getByCategory("social"));
        model.addAttribute("seoSettings",      settingService.getByCategory("seo"));
        model.addAttribute("featureSettings",  settingService.getByCategory("features"));
        model.addAttribute("financeSettings",  settingService.getByCategory("finance"));
        return "admin/settings";
    }

    @PostMapping("/settings")
    public String saveSettings(@RequestParam Map<String, String> allParams, RedirectAttributes ra) {
        // Remove Spring/CSRF params that aren't settings
        allParams.remove("_csrf");
        settingService.saveAll(allParams);
        ra.addFlashAttribute("success", "✅ Settings saved successfully!");
        return "redirect:/admin/settings";
    }

    @PostMapping("/broadcast")
    public String broadcast(@RequestParam String title, @RequestParam String message,
                            @RequestParam(defaultValue = "info") String type, RedirectAttributes ra) {
        List<User> users = userService.findAllClients();
        notifService.broadcast(users, title, message, type);
        ra.addFlashAttribute("success", "Broadcast sent to " + users.size() + " users.");
        return "redirect:/admin/settings";
    }
}

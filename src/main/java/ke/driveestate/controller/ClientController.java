package ke.driveestate.controller;

import ke.driveestate.model.*;
import ke.driveestate.repository.CategoryRepository;
import ke.driveestate.service.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class ClientController {

    private final ListingService listingService;
    private final UserService userService;
    private final FavoriteService favoriteService;
    private final InquiryService inquiryService;
    private final NotificationService notifService;
    private final ActivityLogService logService;
    private final CategoryRepository catRepo;

    private static final List<String> COUNTIES = List.of(
        "Baringo","Bomet","Bungoma","Busia","Elgeyo Marakwet","Embu","Garissa","Homa Bay","Isiolo",
        "Kajiado","Kakamega","Kericho","Kiambu","Kilifi","Kirinyaga","Kisii","Kisumu","Kitui","Kwale",
        "Laikipia","Lamu","Machakos","Makueni","Mandera","Marsabit","Meru","Migori","Mombasa",
        "Murang'a","Nairobi","Nakuru","Nandi","Narok","Nyamira","Nyandarua","Nyeri","Samburu","Siaya",
        "Taita Taveta","Tana River","Tharaka Nithi","Trans Nzoia","Turkana","Uasin Gishu","Vihiga","Wajir","West Pokot"
    );

    public ClientController(ListingService listingService, UserService userService, FavoriteService favoriteService,
                            InquiryService inquiryService, NotificationService notifService,
                            ActivityLogService logService, CategoryRepository catRepo) {
        this.listingService = listingService; this.userService = userService;
        this.favoriteService = favoriteService; this.inquiryService = inquiryService;
        this.notifService = notifService; this.logService = logService; this.catRepo = catRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("featuredCars", listingService.featuredCars());
        model.addAttribute("featuredLand", listingService.featuredLand());
        model.addAttribute("newArrivals", listingService.newArrivals());
        model.addAttribute("totalCars", listingService.countCars());
        model.addAttribute("totalLand", listingService.countLand());
        model.addAttribute("totalVerified", listingService.countVerified());
        model.addAttribute("totalSellers", userService.countClients());
        model.addAttribute("carCategories", catRepo.findByListingType("car"));
        return "client/index";
    }

    @GetMapping("/listings")
    public String listings(@RequestParam(required = false) String type,
                           @RequestParam(required = false) String q,
                           @RequestParam(required = false) String county,
                           @RequestParam(required = false) BigDecimal minPrice,
                           @RequestParam(required = false) BigDecimal maxPrice,
                           @RequestParam(required = false) String condition,
                           @RequestParam(required = false) String fuel,
                           @RequestParam(required = false) String transmission,
                           @RequestParam(required = false) String zoning,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "newest") String sort,
                           @RequestParam(defaultValue = "grid") String view,
                           Model model) {
        ListingType lt = "car".equalsIgnoreCase(type) ? ListingType.CAR : "land".equalsIgnoreCase(type) ? ListingType.LAND : null;
        Page<Listing> results = listingService.search(lt, county, minPrice, maxPrice, condition, fuel, transmission, zoning, q, page, sort);
        model.addAttribute("listings", results.getContent());
        model.addAttribute("currentPage", page); model.addAttribute("totalPages", results.getTotalPages());
        model.addAttribute("totalItems", results.getTotalElements());
        model.addAttribute("categories", catRepo.findAll()); model.addAttribute("counties", COUNTIES);
        model.addAttribute("view", view); model.addAttribute("type", type);
        model.addAttribute("q", q); model.addAttribute("county", county); model.addAttribute("sort", sort);
        return "client/listings";
    }

    @GetMapping("/listing/{id}")
    public String listingDetail(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        Listing listing = listingService.findById(id).orElseThrow();
        listingService.incrementViews(id);
        boolean isFav = user != null && favoriteService.isFavorited(user, listing);
        Page<Listing> related = listingService.search(listing.getListingType(), null, null, null, null, null, null, null, null, 0, "views");
        model.addAttribute("listing", listing); model.addAttribute("isFavorited", isFav);
        model.addAttribute("related", related.getContent().stream().filter(l -> !l.getId().equals(id)).limit(4).toList());
        return "client/listing_detail";
    }

    @GetMapping("/post-listing")
    public String postListingForm(Model model) {
        model.addAttribute("categories", catRepo.findAll());
        model.addAttribute("counties", COUNTIES); model.addAttribute("edit", false);
        return "client/post_listing";
    }

    @PostMapping("/post-listing")
    public String postListing(@AuthenticationPrincipal User user,
                              @RequestParam String listingType, @RequestParam String title,
                              @RequestParam BigDecimal price, @RequestParam String description,
                              @RequestParam String location, @RequestParam String county,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false, defaultValue = "false") boolean negotiable,
                              @RequestParam(required = false, defaultValue = "false") boolean installmentAvailable,
                              @RequestParam Map<String, String> all, RedirectAttributes ra) {
        User fresh = userService.findById(user.getId()).orElseThrow();
        Category cat = categoryId != null ? catRepo.findById(categoryId).orElse(null) : null;
        Listing.Builder b = Listing.builder().title(title).price(price).description(description)
            .location(location).county(county).seller(fresh).category(cat)
            .status(ListingStatus.PENDING).negotiable(negotiable).installmentAvailable(installmentAvailable);
        if ("car".equals(listingType)) {
            b.listingType(ListingType.CAR).make(all.get("make")).model(all.get("model"))
             .year(pi(all.get("year"))).mileage(pi(all.get("mileage"))).fuelType(all.get("fuelType"))
             .transmission(all.get("transmission")).engineCc(pi(all.get("engineCc")))
             .color(all.get("color")).condition(all.get("condition")).driveType(all.get("driveType"))
             .doors(pi(all.get("doors"))).seats(pi(all.get("seats")));
        } else {
            b.listingType(ListingType.LAND).sizeAcres(pd(all.get("sizeAcres"))).zoning(all.get("zoning"))
             .roadAccess(all.get("roadAccess")).waterAvailable("on".equals(all.get("waterAvailable")))
             .electricityAvailable("on".equals(all.get("electricityAvailable")))
             .fenced("on".equals(all.get("fenced"))).titleType(all.get("titleType"));
        }
        listingService.save(b.build());
        ra.addFlashAttribute("success", "Listing submitted! Our team will review it within 24 hours.");
        return "redirect:/my-listings";
    }

    @GetMapping("/listing/{id}/edit")
    public String editForm(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        Listing l = listingService.findById(id).orElseThrow();
        if (!l.getSeller().getId().equals(user.getId()) && !user.isAdmin()) return "redirect:/listing/" + id;
        model.addAttribute("listing", l); model.addAttribute("categories", catRepo.findAll());
        model.addAttribute("counties", COUNTIES); model.addAttribute("edit", true);
        return "client/post_listing";
    }

    @PostMapping("/listing/{id}/delete")
    public String deleteListing(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes ra) {
        Listing l = listingService.findById(id).orElseThrow();
        if (!l.getSeller().getId().equals(user.getId()) && !user.isAdmin()) return "redirect:/listing/" + id;
        listingService.delete(l); ra.addFlashAttribute("success", "Listing deleted.");
        return "redirect:/my-listings";
    }

    @GetMapping("/my-listings")
    public String myListings(@AuthenticationPrincipal User user, @RequestParam(required = false) String status, Model model) {
        User fresh = userService.findById(user.getId()).orElseThrow();
        List<Listing> listings = status != null && !status.isBlank()
            ? listingService.bySellerAndStatus(fresh, ListingStatus.valueOf(status.toUpperCase()))
            : listingService.bySellerAll(fresh);
        model.addAttribute("listings", listings); model.addAttribute("statusFilter", status);
        return "client/my_listings";
    }

    @GetMapping("/favorites")
    public String favorites(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("favorites", favoriteService.getFavorites(userService.findById(user.getId()).orElseThrow()));
        return "client/favorites";
    }

    @PostMapping("/favorite/{id}") @ResponseBody
    public ResponseEntity<Map<String,Object>> toggleFavorite(@PathVariable Long id, @AuthenticationPrincipal User user) {
        User fresh = userService.findById(user.getId()).orElseThrow();
        Listing listing = listingService.findById(id).orElseThrow();
        boolean saved = favoriteService.toggle(fresh, listing);
        return ResponseEntity.ok(Map.of("status", saved ? "saved" : "removed"));
    }

    @PostMapping("/listing/{id}/inquire")
    public String sendInquiry(@PathVariable Long id, @AuthenticationPrincipal User user,
                              @RequestParam String message, RedirectAttributes ra) {
        Listing listing = listingService.findById(id).orElseThrow();
        User fresh = userService.findById(user.getId()).orElseThrow();
        if (listing.getSeller().getId().equals(fresh.getId())) {
            ra.addFlashAttribute("error", "You cannot inquire on your own listing."); return "redirect:/listing/" + id;
        }
        inquiryService.send(listing, fresh, userService.findById(listing.getSeller().getId()).orElseThrow(), message);
        ra.addFlashAttribute("success", "Inquiry sent to the seller!");
        return "redirect:/listing/" + id;
    }

    @GetMapping("/messages")
    public String messages(@AuthenticationPrincipal User user, Model model) {
        User fresh = userService.findById(user.getId()).orElseThrow();
        model.addAttribute("received", inquiryService.receivedBy(fresh));
        model.addAttribute("sent", inquiryService.sentBy(fresh));
        return "client/messages";
    }

    @PostMapping("/inquiry/{id}/reply")
    public String replyInquiry(@PathVariable Long id, @AuthenticationPrincipal User user,
                               @RequestParam String reply, RedirectAttributes ra) {
        Inquiry inq = inquiryService.findById(id).orElseThrow();
        if (!inq.getReceiver().getId().equals(user.getId())) return "redirect:/messages";
        inquiryService.reply(inq, reply); ra.addFlashAttribute("success", "Reply sent.");
        return "redirect:/messages";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        User fresh = userService.findById(user.getId()).orElseThrow();
        List<Listing> myListings = listingService.bySellerAll(fresh);
        long totalViews = myListings.stream().mapToLong(Listing::getViews).sum();
        long totalInquiries = myListings.stream().mapToLong(Listing::getTotalInquiries).sum();
        List<Listing> topListings = myListings.stream().sorted(Comparator.comparingInt(Listing::getViews).reversed()).limit(5).toList();
        model.addAttribute("myListings", myListings); model.addAttribute("totalViews", totalViews);
        model.addAttribute("totalInquiries", totalInquiries); model.addAttribute("favCount", favoriteService.countFavorites(fresh));
        model.addAttribute("recentInquiries", inquiryService.receivedBy(fresh).stream().limit(5).toList());
        model.addAttribute("topListings", topListings); model.addAttribute("notifications", notifService.getUnread(fresh));
        return "client/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) { model.addAttribute("counties", COUNTIES); return "client/profile"; }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal User user, @RequestParam String name,
                                @RequestParam String phone, @RequestParam(required = false) String county,
                                @RequestParam(required = false) String bio,
                                @RequestParam(required = false) String newPassword,
                                @RequestParam(required = false) String confirmPassword, RedirectAttributes ra) {
        User fresh = userService.findById(user.getId()).orElseThrow();
        fresh.setName(name); fresh.setPhone(phone); fresh.setCounty(county); fresh.setBio(bio);
        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) { ra.addFlashAttribute("error", "Passwords do not match."); return "redirect:/profile"; }
            userService.changePassword(fresh, newPassword);
        } else { userService.save(fresh); }
        ra.addFlashAttribute("success", "Profile updated."); return "redirect:/profile";
    }

    @GetMapping("/seller/{id}")
    public String sellerProfile(@PathVariable Long id, Model model) {
        User seller = userService.findById(id).orElseThrow();
        model.addAttribute("seller", seller);
        model.addAttribute("listings", listingService.bySellerAndStatus(seller, ListingStatus.ACTIVE));
        return "client/seller_profile";
    }

    @PostMapping("/notifications/mark-read") @ResponseBody
    public ResponseEntity<Map<String,String>> markNotificationsRead(@AuthenticationPrincipal User user) {
        notifService.markAllRead(userService.findById(user.getId()).orElseThrow());
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/api/search") @ResponseBody
    public List<Map<String,Object>> apiSearch(@RequestParam String q) {
        return listingService.quickSearch(q).stream().map(l -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", l.getId()); m.put("title", l.getTitle()); m.put("price", l.getFormattedPrice());
            m.put("type", l.getListingType().name().toLowerCase()); m.put("location", l.getLocation());
            m.put("url", "/listing/" + l.getId()); return m;
        }).toList();
    }

    private Integer pi(String s) { try { return s==null||s.isBlank()?null:Integer.parseInt(s.trim()); } catch(Exception e){return null;} }
    private Double  pd(String s) { try { return s==null||s.isBlank()?null:Double.parseDouble(s.trim()); } catch(Exception e){return null;} }
}

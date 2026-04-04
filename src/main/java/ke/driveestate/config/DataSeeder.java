package ke.driveestate.config;

import ke.driveestate.model.*;
import ke.driveestate.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepo;
    private final ListingRepository listingRepo;
    private final CategoryRepository catRepo;
    private final SiteSettingRepository settingRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(UserRepository userRepo, ListingRepository listingRepo,
                      CategoryRepository catRepo, SiteSettingRepository settingRepo,
                      PasswordEncoder encoder) {
        this.userRepo = userRepo; this.listingRepo = listingRepo;
        this.catRepo = catRepo; this.settingRepo = settingRepo; this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        seedSettings();
        if (userRepo.count() > 0) { log.info("Database already seeded."); return; }
        log.info("Seeding database...");

        Category sedan   = catRepo.save(Category.builder().name("Sedan").listingType("car").icon("🚗").build());
        Category suv     = catRepo.save(Category.builder().name("SUV").listingType("car").icon("🚙").build());
        catRepo.save(Category.builder().name("Pickup").listingType("car").icon("🛻").build());
        catRepo.save(Category.builder().name("Van / Minibus").listingType("car").icon("🚐").build());
        Category resLand = catRepo.save(Category.builder().name("Residential Land").listingType("land").icon("🏡").build());
        Category comLand = catRepo.save(Category.builder().name("Commercial Land").listingType("land").icon("🏢").build());
        Category agrLand = catRepo.save(Category.builder().name("Agricultural Land").listingType("land").icon("🌾").build());
        Category indLand = catRepo.save(Category.builder().name("Industrial Land").listingType("land").icon("🏭").build());
        Category beach   = catRepo.save(Category.builder().name("Beach Plot").listingType("land").icon("🏖️").build());

        User admin = userRepo.save(User.builder().name("DriveEstate Admin").email("admin@driveestate.co.ke")
            .phone("+254700000001").password(encoder.encode("Admin@1234"))
            .role(Role.ADMIN).active(true).verified(true).county("Nairobi").build());

        User james = userRepo.save(User.builder().name("James Mwangi").email("james@example.com")
            .phone("+254712345678").password(encoder.encode("Password@123"))
            .role(Role.CLIENT).active(true).verified(true).county("Nairobi").build());
        User amina = userRepo.save(User.builder().name("Amina Hassan").email("amina@example.com")
            .phone("+254723456789").password(encoder.encode("Password@123"))
            .role(Role.CLIENT).active(true).verified(true).county("Mombasa").build());
        User peter = userRepo.save(User.builder().name("Peter Kamau").email("peter@example.com")
            .phone("+254734567890").password(encoder.encode("Password@123"))
            .role(Role.CLIENT).active(true).verified(false).county("Nakuru").build());
        User grace = userRepo.save(User.builder().name("Grace Wanjiku").email("grace@example.com")
            .phone("+254745678901").password(encoder.encode("Password@123"))
            .role(Role.CLIENT).active(true).verified(true).county("Eldoret").build());
        User david = userRepo.save(User.builder().name("David Ochieng").email("david@example.com")
            .phone("+254756789012").password(encoder.encode("Password@123"))
            .role(Role.CLIENT).active(true).verified(false).county("Kisumu").build());
        User faith = userRepo.save(User.builder().name("Faith Ndungu").email("faith@example.com")
            .phone("+254767890123").password(encoder.encode("Password@123"))
            .role(Role.CLIENT).active(true).verified(true).county("Thika").build());

        listingRepo.save(Listing.builder().title("2022 Mercedes-Benz GLE 400d 4MATIC AMG").listingType(ListingType.CAR).category(suv)
            .price(new BigDecimal("9800000")).location("Westlands, Nairobi").county("Nairobi")
            .description("Pristine condition Mercedes GLE 400d AMG. Full options — panoramic sunroof, 360-camera, heated seats, Burmester audio. One owner, full CMC service history. Accident-free.")
            .make("Mercedes-Benz").model("GLE 400d").year(2022).mileage(18000).fuelType("Diesel")
            .transmission("Automatic").engineCc(2925).color("Obsidian Black").condition("used").driveType("4WD").doors(5).seats(7)
            .featured(true).verified(true).status(ListingStatus.ACTIVE).views(847).seller(james)
            .negotiable(false).installmentAvailable(true).build());

        listingRepo.save(Listing.builder().title("2020 Toyota Land Cruiser V8 GX.R").listingType(ListingType.CAR).category(suv)
            .price(new BigDecimal("7500000")).location("Karen, Nairobi").county("Nairobi")
            .description("Land Cruiser V8 GX.R. Full Toyota Kenya service history. Sunroof, leather seats, reverse camera, dual AC. Both keys, all accessories.")
            .make("Toyota").model("Land Cruiser V8").year(2020).mileage(62000).fuelType("Diesel")
            .transmission("Automatic").engineCc(4461).color("Pearl White").condition("used").driveType("4WD").doors(5).seats(8)
            .featured(true).verified(true).status(ListingStatus.ACTIVE).views(1203).seller(amina)
            .negotiable(true).installmentAvailable(true).build());

        listingRepo.save(Listing.builder().title("2023 Range Rover Sport HST P400").listingType(ListingType.CAR).category(suv)
            .price(new BigDecimal("14500000")).location("Kilimani, Nairobi").county("Nairobi")
            .description("Brand new Range Rover Sport HST. Adaptive air suspension, Pivi Pro infotainment. Full Land Rover Kenya warranty.")
            .make("Land Rover").model("Range Rover Sport P400").year(2023).mileage(0).fuelType("Petrol")
            .transmission("Automatic").engineCc(2996).color("Firenze Red").condition("new").driveType("AWD").doors(5).seats(5)
            .featured(true).verified(true).status(ListingStatus.ACTIVE).views(654).seller(james)
            .negotiable(false).installmentAvailable(true).build());

        listingRepo.save(Listing.builder().title("2022 Tesla Model 3 Long Range AWD").listingType(ListingType.CAR).category(sedan)
            .price(new BigDecimal("4900000")).location("Gigiri, Nairobi").county("Nairobi")
            .description("Kenya's cleanest EV! 567km range, Autopilot, 15.4-inch touchscreen. Charging cable and adapters included.")
            .make("Tesla").model("Model 3 Long Range").year(2022).mileage(22000).fuelType("Electric")
            .transmission("Automatic").engineCc(0).color("Midnight Silver").condition("used").driveType("AWD").doors(4).seats(5)
            .featured(true).verified(true).status(ListingStatus.ACTIVE).views(912).seller(david)
            .negotiable(false).installmentAvailable(false).build());

        listingRepo.save(Listing.builder().title("2021 Toyota Prado TX-L 2.8 Diesel").listingType(ListingType.CAR).category(suv)
            .price(new BigDecimal("5800000")).location("Runda, Nairobi").county("Nairobi")
            .description("Toyota Prado TX-L. Sunroof, leather seats, 7-seater. Full Toyota Kenya service history.")
            .make("Toyota").model("Prado TX-L").year(2021).mileage(31000).fuelType("Diesel")
            .transmission("Automatic").engineCc(2755).color("Attitude Black").condition("used").driveType("4WD").doors(5).seats(7)
            .featured(true).verified(false).status(ListingStatus.ACTIVE).views(389).seller(grace)
            .negotiable(false).installmentAvailable(true).build());

        listingRepo.save(Listing.builder().title("2019 BMW X5 xDrive30d M-Sport").listingType(ListingType.CAR).category(suv)
            .price(new BigDecimal("6200000")).location("Parklands, Nairobi").county("Nairobi")
            .description("BMW X5 M-Sport full options. Panoramic sunroof, Harman Kardon audio. Full BMW Kenya service history.")
            .make("BMW").model("X5 xDrive30d").year(2019).mileage(74000).fuelType("Diesel")
            .transmission("Automatic").engineCc(2993).color("Carbon Black").condition("used").driveType("AWD").doors(5).seats(5)
            .featured(false).verified(true).status(ListingStatus.ACTIVE).views(521).seller(peter)
            .negotiable(true).installmentAvailable(false).build());

        listingRepo.save(Listing.builder().title("2020 Subaru Outback 2.5i Eyesight AWD").listingType(ListingType.CAR).category(sedan)
            .price(new BigDecimal("2750000")).location("Nakuru Town").county("Nakuru")
            .description("Subaru Outback with Eyesight driver-assist, adaptive cruise, lane-keep assist. Symmetrical AWD.")
            .make("Subaru").model("Outback 2.5i Eyesight").year(2020).mileage(48000).fuelType("Petrol")
            .transmission("CVT").engineCc(2498).color("Autumn Green").condition("used").driveType("AWD").doors(5).seats(5)
            .featured(false).verified(true).status(ListingStatus.ACTIVE).views(276).seller(faith)
            .negotiable(true).installmentAvailable(false).build());

        listingRepo.save(Listing.builder().title("2021 Volkswagen Tiguan 2.0 TSI R-Line").listingType(ListingType.CAR).category(suv)
            .price(new BigDecimal("3850000")).location("Mombasa Road, Nairobi").county("Nairobi")
            .description("VW Tiguan R-Line with DSG 7-speed. Digital cockpit, LED matrix headlights. Kenya duty paid.")
            .make("Volkswagen").model("Tiguan 2.0 TSI R-Line").year(2021).mileage(39000).fuelType("Petrol")
            .transmission("Automatic").engineCc(1984).color("Reflex Silver").condition("used").driveType("4WD").doors(5).seats(5)
            .featured(false).verified(false).status(ListingStatus.ACTIVE).views(193).seller(peter)
            .negotiable(false).installmentAvailable(true).build());

        listingRepo.save(Listing.builder().title("0.5 Acre Commercial Plot — Westlands CBD").listingType(ListingType.LAND).category(comLand)
            .price(new BigDecimal("85000000")).location("Westlands, Nairobi").county("Nairobi")
            .description("Prime commercial plot in Westlands CBD. Corner plot, all utilities. Title deed ready, no disputes.")
            .sizeAcres(0.5).sizeSqft(21780.0).zoning("Commercial").roadAccess("Tarmac")
            .waterAvailable(true).electricityAvailable(true).fenced(false).titleType("Freehold")
            .featured(true).verified(true).status(ListingStatus.ACTIVE).views(624).seller(james)
            .negotiable(true).installmentAvailable(false).build());

        listingRepo.save(Listing.builder().title("5 Acres Agricultural Land — Molo, Nakuru").listingType(ListingType.LAND).category(agrLand)
            .price(new BigDecimal("2500000")).location("Molo, Nakuru").county("Nakuru")
            .description("Fertile agricultural land in Molo highlands. River frontage, perennial stream. Title deed available.")
            .sizeAcres(5.0).sizeSqft(217800.0).zoning("Agricultural").roadAccess("Murram")
            .waterAvailable(true).electricityAvailable(false).fenced(true).titleType("Freehold")
            .featured(false).verified(true).status(ListingStatus.ACTIVE).views(312).seller(faith)
            .negotiable(true).installmentAvailable(true).build());

        listingRepo.save(Listing.builder().title("1/8 Acre Residential Plot — Runda Estate").listingType(ListingType.LAND).category(resLand)
            .price(new BigDecimal("18500000")).location("Runda, Nairobi").county("Nairobi")
            .description("Exclusive residential plot in Runda. Gated community, 24/7 security. All services connected.")
            .sizeAcres(0.125).sizeSqft(5445.0).zoning("Residential").roadAccess("Tarmac")
            .waterAvailable(true).electricityAvailable(true).fenced(true).titleType("Leasehold 99yrs")
            .featured(true).verified(true).status(ListingStatus.ACTIVE).views(489).seller(amina)
            .negotiable(false).installmentAvailable(false).build());

        listingRepo.save(Listing.builder().title("Beachfront 2 Acres — Diani Beach, Kwale").listingType(ListingType.LAND).category(beach)
            .price(new BigDecimal("45000000")).location("Diani Beach, Kwale").county("Kwale")
            .description("Breathtaking beachfront land on Kenya's most coveted coastline. Title deed available.")
            .sizeAcres(2.0).sizeSqft(87120.0).zoning("Mixed Use").roadAccess("Tarmac")
            .waterAvailable(true).electricityAvailable(true).fenced(false).titleType("Freehold")
            .featured(true).verified(false).status(ListingStatus.ACTIVE).views(741).seller(grace)
            .negotiable(true).installmentAvailable(false).build());

        listingRepo.save(Listing.builder().title("2 Acres Industrial Plot — Athi River EPZ").listingType(ListingType.LAND).category(indLand)
            .price(new BigDecimal("28000000")).location("Athi River, Machakos").county("Machakos")
            .description("Strategic industrial plot adjacent to Athi River EPZ. Three-phase power connected.")
            .sizeAcres(2.0).sizeSqft(87120.0).zoning("Industrial").roadAccess("Tarmac")
            .waterAvailable(true).electricityAvailable(true).fenced(true).titleType("Freehold")
            .featured(false).verified(true).status(ListingStatus.ACTIVE).views(233).seller(david)
            .negotiable(false).installmentAvailable(false).build());

        listingRepo.save(Listing.builder().title("50x100 Residential Plot — Kitengela, Kajiado").listingType(ListingType.LAND).category(resLand)
            .price(new BigDecimal("850000")).location("Kitengela, Kajiado").county("Kajiado")
            .description("Affordable 50x100 plot in fast-growing Kitengela. Clean title deed. Installment plan available.")
            .sizeAcres(0.115).sizeSqft(5000.0).zoning("Residential").roadAccess("Gravel")
            .waterAvailable(false).electricityAvailable(true).fenced(false).titleType("Freehold")
            .featured(false).verified(false).status(ListingStatus.ACTIVE).views(567).seller(peter)
            .negotiable(true).installmentAvailable(true).build());

        log.info("✅ Seeded: 6 sellers + admin, 8 cars, 6 land plots");
        log.info("🔑 Admin: admin@driveestate.co.ke / Admin@1234");
    }

    private void seedSettings() {
        if (settingRepo.count() > 0) return;
        log.info("Seeding site settings...");

        // ── General ─────────────────────────────────────────────────────────
        save("site_name",       "DriveEstate",                         "Site Name",          "general",  "text",     1);
        save("site_tagline",    "Kenya's Premier Cars & Land Marketplace", "Site Tagline",    "general",  "text",     2);
        save("site_description","Buy and sell cars and land across all 47 counties of Kenya. Verified listings, trusted sellers.", "Site Description", "general", "textarea", 3);
        save("mpesa_paybill",   "522522",                              "M-Pesa Paybill",     "general",  "text",     4);
        save("listing_fee",     "0",                                   "Listing Fee (KES)",  "general",  "text",     5);
        save("max_images",      "10",                                  "Max Images per Listing", "general","text",   6);

        // ── Contact ──────────────────────────────────────────────────────────
        save("contact_phone",   "+254 700 000 000",                    "Phone Number",       "contact",  "tel",      1);
        save("contact_email",   "support@driveestate.co.ke",           "Support Email",      "contact",  "email",    2);
        save("contact_whatsapp","+254700000000",                       "WhatsApp Number",    "contact",  "tel",      3);
        save("contact_address", "Westlands, Nairobi, Kenya",           "Physical Address",   "contact",  "text",     4);
        save("working_hours",   "Mon–Fri 8am–6pm · Sat 9am–3pm",      "Working Hours",      "contact",  "text",     5);

        // ── Social Media ─────────────────────────────────────────────────────
        save("social_facebook", "https://facebook.com/driveestate",    "Facebook URL",       "social",   "url",      1);
        save("social_twitter",  "https://twitter.com/driveestate",     "Twitter / X URL",    "social",   "url",      2);
        save("social_instagram","https://instagram.com/driveestate",   "Instagram URL",      "social",   "url",      3);
        save("social_youtube",  "",                                    "YouTube URL",        "social",   "url",      4);
        save("social_linkedin", "",                                    "LinkedIn URL",       "social",   "url",      5);
        save("social_tiktok",   "",                                    "TikTok URL",         "social",   "url",      6);

        // ── SEO ──────────────────────────────────────────────────────────────
        save("seo_title",       "DriveEstate — Buy & Sell Cars and Land in Kenya", "SEO Page Title", "seo", "text",  1);
        save("seo_keywords",    "cars for sale Kenya, land for sale Kenya, buy car Nairobi, real estate Kenya", "SEO Keywords", "seo", "textarea", 2);
        save("seo_author",      "DriveEstate Kenya",                   "SEO Author",         "seo",      "text",     3);
        save("google_analytics","",                                    "Google Analytics ID (G-XXXXXXX)", "seo", "text", 4);

        // ── Features / Toggles ───────────────────────────────────────────────
        save("feature_registration", "true",  "Allow New User Registrations", "features", "toggle", 1);
        save("feature_listings",     "true",  "Allow New Listing Posts",      "features", "toggle", 2);
        save("feature_inquiries",    "true",  "Allow Buyer Inquiries",        "features", "toggle", 3);
        save("feature_maintenance",  "false", "Maintenance Mode (closes site)", "features","toggle", 4);
        save("feature_auto_approve", "false", "Auto-Approve Listings (skip review)", "features", "toggle", 5);

        // ── Finance / Loan Calculator ────────────────────────────────────────
        save("loan_default_rate",   "14",     "Default Loan Interest Rate (%)", "finance",  "text",  1);
        save("loan_default_term",   "36",     "Default Loan Term (months)",     "finance",  "text",  2);
        save("loan_default_down",   "30",     "Default Down Payment (%)",       "finance",  "text",  3);

        log.info("✅ Site settings seeded (30 settings across 6 categories)");
    }

    private void save(String key, String value, String label, String category, String inputType, int order) {
        settingRepo.save(new SiteSetting(key, value, label, category, inputType, order));
    }
}

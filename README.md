# 🚗🌍 DriveEstate — Kenya's Premier Cars & Land Marketplace

**Full-Stack Enterprise Web Application**  
Built with **Java 21 · Spring Boot 3.2 · Spring Security · Spring Data JPA · Thymeleaf · H2 Database**

---

## 📋 What's Inside

A complete, production-grade marketplace platform with:

### 🧑 Client Portal (`/`)
- **Homepage** — Hero search, featured listings, stats bar, new arrivals, Why Us section
- **Browse Listings** — Full-featured search with sidebar filters (type, price, county, fuel, mileage, year, zoning, road access, etc.)
- **Listing Detail** — Full specs grid, description, **loan/financing calculator**, seller contact (Call / WhatsApp / Email), inquiry form
- **Post a Listing** — Dynamic car/land form with **AI description generator** (calls Anthropic API)
- **My Listings** — Manage your own listings with status badges
- **Dashboard** — Stats (views, inquiries, favorites), top listings chart, recent messages, notifications
- **Messages** — Inbox/outbox system with threaded replies
- **Favorites** — Saved listings
- **Profile** — Edit profile and change password
- **Seller Profile** — Public page showing seller info and all their listings

### 🔐 Admin Control Panel (`/admin`)
- **Dashboard** — 8 KPI cards, 14-day activity chart, pending listings quick-approve panel, recent users, activity feed
- **Users** — List, search, view detail, verify, suspend/activate, delete
- **Listings** — Search by status/type, manage all listings with full detail view
- **Listing Management** — Approve ✅ / Reject ✗ / Feature ⭐ / Verify ✓ / Mark Sold / Delete
- **Inquiries** — View all platform inquiries with reply status
- **Reports** — County analytics, price range distribution, fuel type charts, top sellers leaderboard
- **Activity Logs** — Full audit trail of all platform actions
- **Settings & Broadcast** — Send notifications to all users, view system info

### 🗄️ Database
- **H2 Embedded** (zero config, auto-creates on first run, saves to file `driveestate-db.mv.db`)
- **14 pre-seeded listings** (8 premium cars + 6 land plots) with realistic Kenya data
- **6 demo sellers** across Nairobi, Mombasa, Nakuru, Eldoret, Kisumu, Thika
- **1 admin account**

---

## 🚀 Quick Start (3 Steps)

### Prerequisites
- **Java 21+** — [Download from Adoptium](https://adoptium.net/temurin/releases/?version=21)
- **Maven 3.8+** — [Download Maven](https://maven.apache.org/download.cgi)

Verify:
```bash
java -version   # Should show 21+
mvn -version    # Should show 3.8+
```

### Step 1 — Build
```bash
cd DriveEstate
mvn package -DskipTests
```

### Step 2 — Run

**Linux / macOS:**
```bash
chmod +x start.sh && ./start.sh
# OR directly:
java -jar target/driveestate-1.0.0.jar
```

**Windows:**
```cmd
start.bat
REM Or directly:
java -jar target\driveestate-1.0.0.jar
```

### Step 3 — Open Browser
| URL | Description |
|-----|-------------|
| http://localhost:8080 | Client marketplace |
| http://localhost:8080/admin | Admin control panel |
| http://localhost:8080/h2-console | Database browser |

---

## 🔑 Login Credentials

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@driveestate.co.ke | Admin@1234 |
| Seller | james@example.com | Password@123 |
| Seller | amina@example.com | Password@123 |
| Seller | peter@example.com | Password@123 |
| Seller | grace@example.com | Password@123 |
| Seller | david@example.com | Password@123 |
| Seller | faith@example.com | Password@123 |

---

## 🏗️ Project Architecture

```
DriveEstate/
├── pom.xml                          ← Maven build (Spring Boot 3.2)
├── start.sh / start.bat             ← One-click startup scripts
├── README.md
└── src/main/
    ├── java/ke/driveestate/
    │   ├── DriveEstateApplication.java    ← @SpringBootApplication entry point
    │   ├── config/
    │   │   ├── SecurityConfig.java        ← Spring Security (BCrypt, roles, CSRF)
    │   │   └── DataSeeder.java            ← Auto-seeds 14 listings on first run
    │   ├── model/                         ← JPA Entities
    │   │   ├── User.java                  ← Implements UserDetails (Spring Security)
    │   │   ├── Listing.java               ← Car + Land fields (30+ columns)
    │   │   ├── Category.java
    │   │   ├── Inquiry.java
    │   │   ├── Favorite.java
    │   │   ├── Notification.java
    │   │   ├── ActivityLog.java
    │   │   ├── Role.java                  ← CLIENT | ADMIN
    │   │   ├── ListingStatus.java         ← PENDING | ACTIVE | REJECTED | SOLD
    │   │   └── ListingType.java           ← CAR | LAND
    │   ├── repository/                    ← Spring Data JPA Repositories
    │   │   ├── UserRepository.java
    │   │   ├── ListingRepository.java     ← Custom JPQL search queries
    │   │   ├── CategoryRepository.java
    │   │   ├── InquiryRepository.java
    │   │   ├── FavoriteRepository.java
    │   │   ├── NotificationRepository.java
    │   │   └── ActivityLogRepository.java
    │   ├── service/                       ← Business Logic Layer
    │   │   ├── UserService.java           ← Implements UserDetailsService
    │   │   ├── ListingService.java        ← Search, CRUD, approve/reject/feature
    │   │   ├── InquiryService.java
    │   │   ├── FavoriteService.java
    │   │   ├── NotificationService.java   ← Broadcast to all users
    │   │   └── ActivityLogService.java
    │   └── controller/                    ← Spring MVC Controllers
    │       ├── AuthController.java        ← /auth/login, /auth/register
    │       ├── ClientController.java      ← Full marketplace (16 endpoints)
    │       └── AdminController.java       ← Full admin panel (20+ endpoints)
    └── resources/
        ├── application.properties         ← H2 DB, Thymeleaf, server config
        └── templates/                     ← Thymeleaf HTML Templates
            ├── auth/
            │   ├── login.html
            │   └── register.html
            ├── client/
            │   ├── layout.html            ← Shared navbar, footer, live search
            │   ├── index.html             ← Homepage with hero + search
            │   ├── listings.html          ← Browse with sidebar filters
            │   ├── listing_detail.html    ← Detail + loan calc + inquiry form
            │   ├── dashboard.html         ← Client dashboard + Chart.js
            │   ├── post_listing.html      ← Dynamic car/land form + AI desc
            │   ├── my_listings.html
            │   ├── favorites.html
            │   ├── messages.html
            │   ├── profile.html
            │   └── seller_profile.html
            └── admin/
                ├── layout.html            ← Admin sidebar + topbar layout
                ├── dashboard.html         ← KPIs + charts + quick approve
                ├── users.html
                ├── user_detail.html       ← Verify / suspend / delete
                ├── listings.html
                ├── listing_detail.html    ← Approve / reject / feature
                ├── inquiries.html
                ├── reports.html           ← Analytics charts
                ├── logs.html
                └── settings.html          ← System info + broadcast
```

---

## 🛠️ Key Technologies

| Layer | Technology |
|-------|-----------|
| Language | **Java 21** (LTS) |
| Framework | **Spring Boot 3.2.3** |
| Security | **Spring Security 6** (BCrypt, form login, roles) |
| ORM | **Spring Data JPA + Hibernate 6** |
| Database | **H2 Embedded** (auto-persists to file) |
| Templates | **Thymeleaf 3.1** (with Spring Security dialect) |
| Build | **Maven 3.8+** |
| CSS | **Bootstrap 5.3** (CDN) |
| Charts | **Chart.js 4.4** (CDN) |
| Icons | **Font Awesome 6** (CDN) |
| Fonts | **Google Fonts** (Cormorant Garamond + Outfit) |
| AI Desc | **Anthropic Claude API** (optional, in-browser JS) |

---

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Change port
server.port=8080

# Use PostgreSQL instead of H2 (for production)
spring.datasource.url=jdbc:postgresql://localhost:5432/driveestate
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Disable H2 console in production
spring.h2.console.enabled=false
```

---

## 📦 Build as Executable JAR

```bash
mvn package -DskipTests
# Creates: target/driveestate-1.0.0.jar (self-contained, ~50MB)

# Run anywhere Java 21 is installed:
java -jar target/driveestate-1.0.0.jar
```

---

## 🇰🇪 Features Specific to Kenya Market

- **47 counties** — All Kenya counties in dropdown selects
- **M-Pesa** — Referenced in payment/installment features
- **WhatsApp integration** — Direct WhatsApp link on every listing
- **KES formatting** — All prices in Kenyan Shillings
- **NTSA safety tip** — On listing detail pages
- **Loan calculator** — Configured with typical Kenya bank rates (14% p.a.)
- **Kenyan cities/towns** — Pre-seeded listings across Nairobi, Mombasa, Nakuru, Eldoret, Kisumu, Diani, Molo, Athi River, Kitengela

---

*Built with ❤️ for Kenya | DriveEstate © 2024*

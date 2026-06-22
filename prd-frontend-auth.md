# Kanban: Race Entry React Frontend + JWT Authentication

## Reference

**Stack:** React 19 · Vite 6 (`/frontend`) · React Router v6 · fetch API · Spring Security + JJWT · Vite proxies `/api` → `http://localhost:8080`

**Auth:** JWT Bearer token stored in `localStorage`. Spring `JwtFilter` validates on every request. Login/register return `{token}`.

**Build order:** JWT backend → Auth API → React auth shell → Auth pages → Events → My Boats → Entries → Driver mgmt → Admin

**Error contract**

| Status | Behaviour |
|--------|-----------|
| `401` | Clear token, redirect `/login` |
| `403` | Show "Access denied" |
| `404` | Show inline "Not found" |
| `409` | Show inline conflict error |
| `422` | Show inline business rule message |

**Roles**

| Role | Permissions |
|------|-------------|
| `ROLE_USER` | View events · manage own boats · create/submit entries · manage own entry's drivers |
| `ROLE_ADMIN` | All above + manage boat classes + manage events |

**Auth API endpoints**

| Method | Path | Returns |
|--------|------|---------|
| `POST` | `/api/auth/register` | `{token}` |
| `POST` | `/api/auth/login` | `{token}` |

**React route map**

| Path | Role | Component |
|------|------|-----------|
| `/login` | Public | `LoginPage` |
| `/register` | Public | `RegisterPage` |
| `/events` | USER | `EventListPage` |
| `/events/:id` | USER | `EventDetailPage` |
| `/my-boats` | USER | `MyBoatsPage` |
| `/entries/new` | USER | `NewEntryPage` |
| `/entries/:id` | USER | `EntryDetailPage` |
| `/admin/boat-classes` | ADMIN | `AdminBoatClassesPage` |
| `/admin/events` | ADMIN | `AdminEventsPage` |

---

## Board

### Done

#### [7] Events list + detail pages
**React — Events**
- [x] `EventListPage`: `GET /api/events` on mount · table: name, status badge (OPEN=green, CLOSED=grey), "View" → `/events/:id`
- [x] `EventDetailPage`: `GET /api/events/:id` + `GET /api/entries/event/:id` · render name+status · entries table (boat name via `GET /api/boats/:id`, entry status) · "Enter this event" → `/entries/new?eventId=:id` (visible only when OPEN)

#### [6] Login + Register pages + NavBar
**React — Foundation**
- [x] `LoginPage`: username/password form → `POST /api/auth/login` · success: `login(token)` + navigate `/events` · failure: inline "Invalid username or password" · link to `/register`
- [x] `RegisterPage`: username/password/confirm form · validate passwords match · `POST /api/auth/register` · success: `login(token)` + navigate `/events` · 409: "Username already taken" · link to `/login`
- [x] `NavBar`: Events · My Boats links; if admin also Boat Classes + Admin Events; Logout button

#### [5] React foundation — project structure + routing + auth context
**React — Foundation**
- [x] Install `react-router-dom` (added to `package.json`; run `npm install` in `/frontend` to apply)
- [x] `src/api/client.js` — fetch wrapper: reads token from `localStorage`, sets `Authorization` header, on `401` clear token + redirect `/login`
- [x] `src/context/AuthContext.jsx` — `{user, token, login, logout}`: `login` stores token + decodes username+role; `logout` clears localStorage
- [x] `src/router.jsx` — `<BrowserRouter>` + routes + `<ProtectedRoute>` (→ `/login`) + `<AdminRoute>` (checks `ROLE_ADMIN` from token)
- [x] `src/App.jsx` — wraps router in `<AuthContext.Provider>`
- [x] `JwtUtil.generateToken(username, role)` — adds `role` claim to JWT so frontend can decode it from token
- [x] `AuthService` updated to pass role to `generateToken`

#### [4] AuthService + AuthController
**Auth — Backend**
- [x] `AuthService.register`: validate username not taken · BCrypt password · save `ROLE_USER` · auto-create `Person` (firstName=username) · return JWT
- [x] `AuthService.login`: authenticate → JWT; throw `401` on bad credentials
- [x] `AuthController` (`/api/auth`): `POST /register` → `200 {token}` · `POST /login` → `200 {token}` or `401`
- [x] Test: `AuthServiceTest` — register success · duplicate → `ConflictException` · login success · bad password → exception
- [x] Test: `AuthControllerIT` — register 200+token · duplicate 409 · login 200+token · bad password 401

#### [3] UserDetailsServiceImpl + SecurityConfig
**Auth — Backend**
- [x] `UserDetailsServiceImpl implements UserDetailsService` — loads `User` by username
- [x] `SecurityConfig`: `BCryptPasswordEncoder` bean · stateless sessions · add `JwtFilter` before `UsernamePasswordAuthenticationFilter`
- [x] CORS: allow `http://localhost:5173`, GET/POST/PUT/DELETE, Authorization/Content-Type headers
- [x] Permit: `POST /api/auth/register`, `POST /api/auth/login`; `ROLE_ADMIN` for `/api/admin/**`; auth required for all other `/api/**`; CSRF disabled

#### [1] User entity + UserRepository
**Auth — Backend**
- [x] `User`: `id`, `username` (unique), `password` (BCrypt), `role` (`ROLE_USER`/`ROLE_ADMIN`), `personId` (FK → `person.id`, nullable)
- [x] `UserRepository extends JpaRepository<User, Long>`
- [x] `Optional<User> findByUsername(String username)`

#### [2] JWT utility + JwtFilter
**Auth — Backend**
- [x] Add `io.jsonwebtoken:jjwt-api/impl/jackson` (0.11.x) to `pom.xml`
- [x] `JwtUtil`: `generateToken`, `extractUsername`, `isTokenValid`
- [x] `JwtFilter extends OncePerRequestFilter`: reads `Authorization: Bearer <token>`, validates, sets `SecurityContextHolder`
- [x] Test: `JwtUtilTest` — generate, extract username, reject expired, reject tampered

---

### In Progress

_(none yet)_

---

### To Do

---

---

#### [8] My Boats CRUD page
**React — My Boats**
- [ ] Decode `personId` claim from JWT; add `personId` claim in `JwtUtil.generateToken`
- [ ] Backend: add `?ownerId=` query param to `GET /api/boats` (`findByOwnerId`)
- [ ] `MyBoatsPage`: load own boats · table: name, sail number, boat class; Edit + Delete buttons · inline "Add boat" form: name, sail number, boat class `<select>` (from `GET /api/boat-classes`)
- [ ] Add: `POST /api/boats` with `ownerId` from token · Edit: `PUT /api/boats/:id` · Delete: `DELETE /api/boats/:id` with confirmation

---

#### [9] New entry + entry detail pages
**React — Entries**
- [ ] `NewEntryPage`: load user's boats + open events · form: boat `<select>` + event `<select>` (pre-select `?eventId=`) · `POST /api/entries` · success → `/entries/:id` · 409: "Entry already exists for this boat in this event"
- [ ] `EntryDetailPage`: `GET /api/entries/:id` + `GET /api/entries/:id/drivers` · resolve boat + event names · entry summary: boat, event, status badge
- [ ] Driver table: person name (via `GET /api/persons/:id`), role, Remove (`DELETE /api/entry-drivers/:driverId`)
- [ ] "Add driver" form: person `<select>` + role input → `POST /api/entries/:id/drivers` · 409: "Person already a driver on this entry"
- [ ] "Submit entry" → `POST /api/entries/:id/submit` · 422: show business rule · hide button if SUBMITTED

---

#### [10] Admin: Boat Classes page
**React — Admin**
- [ ] `AdminBoatClassesPage` (wrapped in `<AdminRoute>`): `GET /api/boat-classes` on mount · table with Delete (`DELETE /api/boat-classes/:id`) · inline "Add boat class" form → `POST /api/boat-classes`

---

#### [11] Admin: Events page
**React — Admin**
- [ ] `AdminEventsPage` (wrapped in `<AdminRoute>`): `GET /api/events` on mount · table: name, status badge, "Close" (`PUT /api/events/:id/close`, only if OPEN), Delete (`DELETE /api/events/:id`) · inline "Create event" form → `POST /api/events`

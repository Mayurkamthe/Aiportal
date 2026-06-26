# 🎯 CareerPath — AI-Powered Career Guidance Portal

A full-stack Spring Boot web application that helps students discover their ideal career paths through AI-driven assessments, personalized reports, and guidance from counselors and parents.

---

## 📸 Portal Overview

| Role | Access |
|------|--------|
| **Student** | Take assessments, view AI career report, explore careers/courses/jobs, chat with AI |
| **Parent** | Monitor child's progress, view counselor notes, download PDF report |
| **Counselor** | Manage assigned students, add notes, download student reports |
| **Admin** | Full platform control — users, questions, careers, courses, jobs, internships, settings |

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2.0, Spring MVC |
| ORM | Spring Data JPA + Hibernate |
| Database | SQLite (file-based, zero setup) |
| Template Engine | Thymeleaf |
| AI Integration | OpenRouter API (Gemini 2.0 Flash) |
| PDF Generation | iTextPDF |
| Frontend | Tailwind CSS (CDN), Vanilla JS |
| Fonts | Inter + Syne (Google Fonts) |
| Session | HttpSession (role-based) |

---

## 📁 Project Structure

```
src/main/
├── java/com/careerportal/
│   ├── CareerPortalApplication.java
│   ├── config/
│   │   ├── DataInitializer.java        # Seeds default users & questions
│   │   └── WebConfig.java              # Static resource mapping
│   ├── controller/
│   │   ├── AuthController.java         # Login / Logout
│   │   ├── StudentController.java      # All student routes
│   │   ├── CounselorController.java    # Counselor routes
│   │   ├── ParentController.java       # Parent routes
│   │   └── AdminController.java        # Admin panel routes
│   ├── entity/
│   │   ├── User.java, Student.java
│   │   ├── Assessment.java, AIReport.java
│   │   ├── Career.java, Course.java
│   │   ├── Job.java, Internship.java
│   │   ├── Question.java, ChatHistory.java
│   │   ├── CounselorNote.java, SystemSettings.java
│   ├── repository/          # Spring Data JPA repositories
│   ├── service/
│   │   ├── AssessmentService.java      # Assessment logic + AI report generation
│   │   ├── ChatService.java            # AI chat history
│   │   ├── OpenRouterService.java      # OpenRouter/Gemini API client
│   │   ├── PDFService.java             # PDF report generation
│   │   ├── StudentService.java
│   │   └── UserService.java
│   └── util/
│       └── SessionUtil.java            # Session helpers
└── resources/
    ├── application.properties
    ├── static/css/portal.css
    └── templates/
        ├── fragments/
        │   ├── head.html               # Shared CSS, design tokens
        │   └── sidebar.html            # Role-based sidebars + mobile nav
        ├── auth/login.html
        ├── student/
        │   ├── dashboard.html
        │   ├── profile.html
        │   ├── assessment.html
        │   ├── report.html
        │   ├── chat.html
        │   ├── careers.html
        │   ├── courses.html
        │   ├── jobs.html
        │   └── internships.html
        ├── counselor/
        │   ├── dashboard.html
        │   ├── students.html
        │   └── student-detail.html
        ├── parent/
        │   ├── dashboard.html
        │   └── student-progress.html
        └── admin/
            ├── dashboard.html
            ├── users.html, user-form.html
            ├── questions.html, question-form.html
            ├── careers.html, courses.html
            ├── jobs.html, internships.html
            ├── reports.html
            └── settings.html
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- No database setup needed (SQLite auto-creates `career_portal.db`)

### 1. Clone the repository
```bash
git clone https://github.com/Mayurkamthe/Aiportal.git
cd Aiportal
```

### 2. Configure OpenRouter API key
Edit `src/main/resources/application.properties`:
```properties
app.openrouter.api-url=https://openrouter.ai/api/v1/chat/completions
app.openrouter.model=google/gemini-2.0-flash-exp:free
# Add your API key in OpenRouterService.java or as an env variable
```

### 3. Build & Run
```bash
mvn spring-boot:run
```

### 4. Open in browser
```
http://localhost:8080
```

---

## 🔐 Default Login Credentials

Seeded automatically by `DataInitializer.java` on first run:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Student | `student1` | `student123` |
| Parent | `parent1` | `parent123` |
| Counselor | `counselor1` | `counselor123` |

> ⚠️ Change these credentials after first login in production.

---

## 🌟 Features

### Student Portal
- **3 Assessments** — Aptitude, Interest, Personality (MCQ with live progress bar)
- **AI Career Report** — Generated via OpenRouter/Gemini after all assessments complete
- **AI Chat** — Real-time career advisor chatbot (AJAX, no page reload)
- **PDF Download** — Full career report as downloadable PDF
- **Explore** — Browse careers, courses, jobs, internships
- **Profile** — Photo upload, academic marks, interests, career goals

### Counselor Portal
- View all assigned students with academic performance
- Add timestamped counselor notes per student
- Download any student's PDF report

### Parent Portal
- View child's assessment completion status
- Read AI career report and counselor notes
- Download child's PDF report

### Admin Panel
- **Users** — Create/edit/delete users for all roles
- **Questions** — Manage MCQ questions per assessment type with filter tabs
- **Careers / Courses / Jobs / Internships** — Full CRUD with modal forms
- **AI Reports** — View all generated reports with match score progress bars
- **Settings** — Key-value system settings editor

---

## 🗄️ Database

Uses **SQLite** — no installation needed. The database file `career_portal.db` is auto-created in the project root on first run.

Tables auto-generated by Hibernate (`ddl-auto=update`):
`users`, `students`, `assessments`, `ai_reports`, `chat_history`, `counselor_notes`, `careers`, `courses`, `jobs`, `internships`, `questions`, `system_settings`

---

## 🤖 AI Integration

**OpenRouter API** with `google/gemini-2.0-flash-exp:free` model:

- **Career Report** — Triggered when a student completes all 3 assessments. Sends assessment answers + student profile to Gemini, which returns structured career recommendations.
- **AI Chat** — Each student message sent to Gemini with context about the student's profile and past conversation.

---

## 📱 Responsive Design

- **Desktop** — Fixed 240px dark sidebar with collapsible sections
- **Mobile** — Sidebar hidden by default, toggled via hamburger menu with overlay
- All pages use CSS Grid / Flexbox with `auto-fit` / `minmax` for fluid layouts

---

## 🌿 Git Branches

| Branch | Description |
|--------|-------------|
| `main` | Production-ready merged code (backend + all templates) |
| `thymeleaf-templates` | Feature branch — all 28 Thymeleaf UI templates |

---

## 📄 License

This project is developed for educational and career guidance purposes.

---

## 👤 Author

**Mayur Kamthe**  
📧 kamthemayur35@gmail.com  
🔗 [github.com/Mayurkamthe](https://github.com/Mayurkamthe)

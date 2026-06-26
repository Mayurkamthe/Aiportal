package com.careerportal.config;

import com.careerportal.entity.*;
import com.careerportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - Seeds the database with default data on first run
 * Creates default users, questions, careers, courses, jobs, internships
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepo;
    @Autowired private StudentRepository studentRepo;
    @Autowired private QuestionRepository questionRepo;
    @Autowired private CareerRepository careerRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private JobRepository jobRepo;
    @Autowired private InternshipRepository internshipRepo;
    @Autowired private SystemSettingsRepository settingsRepo;

    @Override
    public void run(String... args) {
        createDefaultUsers();
        createDefaultQuestions();
        createDefaultCareers();
        createDefaultCourses();
        createDefaultJobs();
        createDefaultInternships();
        createDefaultSettings();
    }

    private void createDefaultUsers() {
        if (userRepo.count() > 0) return;

        // Admin
        userRepo.save(new User("admin", "admin123", "ADMIN", "Admin User", "admin@portal.com"));

        // Counselor
        User counselor = new User("counselor1", "pass123", "COUNSELOR", "Dr. Priya Sharma", "priya@portal.com");
        counselor = userRepo.save(counselor); // capture generated ID

        // Student
        User student = new User("student1", "pass123", "STUDENT", "Rahul Kumar", "rahul@student.com");
        userRepo.save(student);
        Student s = new Student();
        s.setUser(student);
        s.setCurrentClass("12th Grade");
        s.setSchool("Delhi Public School");
        s.setBoard("CBSE");
        s.setOverallPercentage(85.0);
        s.setSkills("Mathematics, Programming");
        s.setInterests("Technology, Science");
        s.setHobbies("Reading, Coding");
        s.setCareerGoal("Software Engineer");
        s.setProfileCompletion(60);
        s.setCounselorId(counselor.getId());
        studentRepo.save(s);

        // Parent
        User parent = new User("parent1", "pass123", "PARENT", "Mr. Suresh Kumar", "suresh@parent.com");
        userRepo.save(parent);

        System.out.println("✅ Default users created.");
        System.out.println("   Admin: admin / admin123");
        System.out.println("   Counselor: counselor1 / pass123");
        System.out.println("   Student: student1 / pass123");
        System.out.println("   Parent: parent1 / pass123");
    }

    private void createDefaultQuestions() {
        if (questionRepo.count() > 0) return;

        // Personality Questions
        String[] pq = {
            "When you are in a group, you usually:",
            "You prefer tasks that are:",
            "When making decisions, you rely more on:",
            "You find it easier to work:",
            "You are more comfortable with:",
            "In your free time, you prefer to:"
        };
        String[][] po = {
            {"Lead and coordinate", "Support others", "Work independently", "Observe and analyze"},
            {"Creative and open-ended", "Structured and defined", "Collaborative", "Solo with clear steps"},
            {"Facts and logic", "Feelings and values", "Both equally", "Intuition"},
            {"With others in a team", "Alone on your own", "In small groups", "With close friends only"},
            {"New changes and challenges", "Familiar routines and stability", "Mix of both", "Predictable patterns"},
            {"Social activities", "Reading or learning", "Creative pursuits", "Physical activities"}
        };
        for (int i = 0; i < pq.length; i++) {
            Question q = new Question();
            q.setQuestionType("PERSONALITY");
            q.setQuestionText(pq[i]);
            q.setOptionA(po[i][0]);
            q.setOptionB(po[i][1]);
            q.setOptionC(po[i][2]);
            q.setOptionD(po[i][3]);
            q.setOrderNum(i + 1);
            questionRepo.save(q);
        }

        // Interest Questions
        String[] iq = {
            "Which activity excites you the most?",
            "What type of problems do you enjoy solving?",
            "Which subject do you find most interesting?",
            "What kind of work environment do you prefer?",
            "Which of these careers sounds most appealing?",
            "What motivates you most in work?"
        };
        String[][] io = {
            {"Building or designing things", "Helping people with problems", "Analyzing data or research", "Creating art or music"},
            {"Technical and engineering problems", "People and social issues", "Business and financial challenges", "Creative and design challenges"},
            {"Mathematics and Science", "Literature and Languages", "Social Studies and History", "Arts and Music"},
            {"Laboratory or research setting", "Office with team collaboration", "Outdoors or fieldwork", "Studio or creative space"},
            {"Software Engineer", "Doctor or Nurse", "Entrepreneur", "Artist or Designer"},
            {"Money and financial security", "Making a difference in society", "Learning and growing", "Recognition and achievement"}
        };
        for (int i = 0; i < iq.length; i++) {
            Question q = new Question();
            q.setQuestionType("INTEREST");
            q.setQuestionText(iq[i]);
            q.setOptionA(io[i][0]);
            q.setOptionB(io[i][1]);
            q.setOptionC(io[i][2]);
            q.setOptionD(io[i][3]);
            q.setOrderNum(i + 1);
            questionRepo.save(q);
        }

        // Aptitude Questions
        String[][] aq = {
            {"If 2x + 5 = 15, then x = ?", "5", "8", "10", "6", "A"},
            {"A train travels 60 km in 1 hour. How far will it go in 2.5 hours?", "120 km", "150 km", "180 km", "90 km", "B"},
            {"Which word is opposite of 'Benevolent'?", "Kind", "Generous", "Malevolent", "Helpful", "C"},
            {"Find the odd one out: Apple, Mango, Carrot, Banana", "Apple", "Mango", "Carrot", "Banana", "C"},
            {"If today is Monday, what day will it be after 100 days?", "Wednesday", "Thursday", "Friday", "Saturday", "A"},
            {"Rearrange: TNSEDIT = ?", "STUDENT", "DENTIST", "DISTANT", "SIDENT", "B"}
        };
        for (String[] qa : aq) {
            Question q = new Question();
            q.setQuestionType("APTITUDE");
            q.setQuestionText(qa[0]);
            q.setOptionA(qa[1]);
            q.setOptionB(qa[2]);
            q.setOptionC(qa[3]);
            q.setOptionD(qa[4]);
            q.setCorrectAnswer(qa[5]);
            q.setMarks(1);
            questionRepo.save(q);
        }
        System.out.println("✅ Default questions created.");
    }

    private void createDefaultCareers() {
        if (careerRepo.count() > 0) return;
        String[][] careers = {
            {"Software Engineer", "Technology", "Design, build, and maintain software systems", "₹6L–₹25L/yr", "Java, Python, JavaScript", "B.Tech/B.E. Computer Science"},
            {"Data Scientist", "Technology", "Analyze data and build predictive models", "₹8L–₹30L/yr", "Python, R, Machine Learning", "B.Tech + Statistics/Math"},
            {"Doctor (MBBS)", "Healthcare", "Diagnose and treat patients", "₹6L–₹20L/yr", "Biology, Medical Science", "MBBS + PG"},
            {"Chartered Accountant", "Finance", "Manage accounts, taxation, and audits", "₹7L–₹25L/yr", "Accounting, Finance, Law", "CA Certification"},
            {"Civil Engineer", "Engineering", "Design and oversee construction projects", "₹4L–₹15L/yr", "Maths, Physics, AutoCAD", "B.Tech Civil Engineering"},
            {"Graphic Designer", "Creative", "Create visual content for brands", "₹3L–₹12L/yr", "Photoshop, Illustrator, Figma", "BFA or Design Degree"},
            {"Lawyer", "Legal", "Represent clients in legal matters", "₹5L–₹20L/yr", "Communication, Research, Law", "LLB/LLM"},
            {"Teacher / Professor", "Education", "Educate students in academic subjects", "₹4L–₹12L/yr", "Subject Expertise, Communication", "B.Ed / M.Tech + PhD"}
        };
        for (String[] c : careers) {
            Career career = new Career();
            career.setName(c[0]); career.setCategory(c[1]); career.setDescription(c[2]);
            career.setSalaryRange(c[3]); career.setRequiredSkills(c[4]); career.setEducationRequired(c[5]);
            careerRepo.save(career);
        }
        System.out.println("✅ Default careers created.");
    }

    private void createDefaultCourses() {
        if (courseRepo.count() > 0) return;
        String[][] courses = {
            {"Python for Beginners", "Coursera", "Technology", "Learn Python from scratch", "30 hours", "Free", "https://coursera.org", "Beginner"},
            {"Web Development Bootcamp", "Udemy", "Technology", "HTML, CSS, JavaScript, React", "60 hours", "₹499", "https://udemy.com", "Beginner"},
            {"Data Science with Python", "edX", "Data Science", "Statistics, ML, Data Visualization", "40 hours", "Free", "https://edx.org", "Intermediate"},
            {"Digital Marketing", "Google", "Marketing", "SEO, SEM, Social Media Marketing", "20 hours", "Free", "https://skillshop.google.com", "Beginner"},
            {"Financial Modeling", "CFI", "Finance", "Excel-based financial analysis", "25 hours", "₹1999", "https://corporatefinanceinstitute.com", "Intermediate"},
            {"UX/UI Design", "Coursera", "Design", "User research, prototyping, Figma", "35 hours", "Free", "https://coursera.org", "Beginner"}
        };
        for (String[] c : courses) {
            Course course = new Course();
            course.setTitle(c[0]); course.setProvider(c[1]); course.setCategory(c[2]);
            course.setDescription(c[3]); course.setDuration(c[4]); course.setPrice(c[5]);
            course.setUrl(c[6]); course.setLevel(c[7]);
            courseRepo.save(course);
        }
        System.out.println("✅ Default courses created.");
    }

    private void createDefaultJobs() {
        if (jobRepo.count() > 0) return;
        String[][] jobs = {
            {"Junior Software Developer", "TCS", "Mumbai / Remote", "Full-time", "Build web applications using Java", "₹4L–₹8L/yr", "Java, Spring Boot", "Fresher"},
            {"Data Analyst", "Infosys", "Bangalore", "Full-time", "Analyze business data and prepare reports", "₹5L–₹10L/yr", "Excel, SQL, Python", "0–2 years"},
            {"Frontend Developer", "Wipro", "Hyderabad / Remote", "Full-time", "Build responsive web interfaces", "₹4L–₹9L/yr", "HTML, CSS, React", "0–2 years"},
            {"Content Writer", "Startup", "Remote", "Part-time", "Write blogs, articles and social content", "₹2L–₹5L/yr", "Writing, SEO", "Fresher"},
            {"Graphic Designer", "Agency", "Delhi / Remote", "Full-time", "Create marketing materials and UI designs", "₹3L–₹7L/yr", "Photoshop, Figma", "0–1 year"}
        };
        for (String[] j : jobs) {
            Job job = new Job();
            job.setTitle(j[0]); job.setCompany(j[1]); job.setLocation(j[2]); job.setType(j[3]);
            job.setDescription(j[4]); job.setSalaryRange(j[5]); job.setRequiredSkills(j[6]); job.setExperience(j[7]);
            jobRepo.save(job);
        }
        System.out.println("✅ Default jobs created.");
    }

    private void createDefaultInternships() {
        if (internshipRepo.count() > 0) return;
        String[][] internships = {
            {"Web Development Intern", "Startup Hub", "Remote", "2 months", "₹5,000/month", "Build projects using HTML/CSS/JS", "JavaScript, HTML", "Technology"},
            {"Data Science Intern", "Analytics Co.", "Bangalore", "3 months", "₹8,000/month", "Work on real data projects", "Python, pandas", "Data Science"},
            {"Marketing Intern", "Brand Agency", "Mumbai / Remote", "2 months", "₹4,000/month", "Social media and digital marketing", "Communication, Canva", "Marketing"},
            {"Design Intern", "Creative Studio", "Remote", "3 months", "₹6,000/month", "UI/UX design projects", "Figma, Photoshop", "Design"},
            {"Finance Intern", "Finance Firm", "Delhi", "2 months", "Unpaid", "Learn financial analysis", "Excel, Finance basics", "Finance"}
        };
        for (String[] i : internships) {
            Internship intern = new Internship();
            intern.setTitle(i[0]); intern.setCompany(i[1]); intern.setLocation(i[2]);
            intern.setDuration(i[3]); intern.setStipend(i[4]); intern.setDescription(i[5]);
            intern.setRequiredSkills(i[6]); intern.setDomain(i[7]);
            internshipRepo.save(intern);
        }
        System.out.println("✅ Default internships created.");
    }

    private void createDefaultSettings() {
        if (settingsRepo.count() > 0) return;
        settingsRepo.save(new SystemSettings("openrouter_api_key", "YOUR_API_KEY_HERE", "OpenRouter API Key for AI features"));
        settingsRepo.save(new SystemSettings("openrouter_model", "google/gemini-2.0-flash-exp:free", "AI model to use"));
        settingsRepo.save(new SystemSettings("portal_name", "AI Career Portal", "Name of the portal"));
        settingsRepo.save(new SystemSettings("max_chat_history", "50", "Max chat messages to store per student"));
        System.out.println("✅ Default settings created.");
    }
}

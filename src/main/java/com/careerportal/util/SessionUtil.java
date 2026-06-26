package com.careerportal.util;

import com.careerportal.entity.User;
import jakarta.servlet.http.HttpSession;

/**
 * SessionUtil - Helper class for session management
 * Use this class to get/set session values consistently
 */
public class SessionUtil {

    public static final String SESSION_USER = "loggedInUser";
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USER_ROLE = "userRole";
    public static final String SESSION_USER_NAME = "userName";

    /** Save user to session after login */
    public static void setUser(HttpSession session, User user) {
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_USER_ID, user.getId());
        session.setAttribute(SESSION_USER_ROLE, user.getRole());
        session.setAttribute(SESSION_USER_NAME, user.getFullName());
    }

    /** Get logged-in user from session */
    public static User getUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER);
    }

    /** Get user ID from session */
    public static Long getUserId(HttpSession session) {
        return (Long) session.getAttribute(SESSION_USER_ID);
    }

    /** Get user role from session */
    public static String getRole(HttpSession session) {
        return (String) session.getAttribute(SESSION_USER_ROLE);
    }

    /** Check if user is logged in */
    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_USER) != null;
    }

    /** Check if user has specific role */
    public static boolean hasRole(HttpSession session, String role) {
        String userRole = getRole(session);
        return role.equals(userRole);
    }

    /** Clear session on logout */
    public static void clearSession(HttpSession session) {
        session.invalidate();
    }
}

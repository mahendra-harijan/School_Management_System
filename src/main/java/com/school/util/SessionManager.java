package com.school.util;

import com.school.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser() { return currentUser; }
    public void logout() { currentUser = null; }
    public boolean isLoggedIn() { return currentUser != null; }
    public boolean isAdmin() { return currentUser != null && "ADMIN".equals(currentUser.getRole()); }
    public boolean isTeacher() { return currentUser != null && "TEACHER".equals(currentUser.getRole()); }
    public boolean isStudent() { return currentUser != null && "STUDENT".equals(currentUser.getRole()); }
}

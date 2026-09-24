package com.pims.util;

/** Holds details about the currently logged-in user for the lifetime of the app run. */
public class Session {
    private static int userId;
    private static String username;
    private static String fullName;
    private static String role;

    public static void set(int id, String uname, String fname, String r) {
        userId = id;
        username = uname;
        fullName = fname;
        role = r;
    }

    public static int getUserId() { return userId; }
    public static String getUsername() { return username; }
    public static String getFullName() { return fullName; }
    public static String getRole() { return role; }

    public static void clear() {
        userId = 0;
        username = null;
        fullName = null;
        role = null;
    }
}

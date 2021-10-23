package com.techai.shiftme.utils;

import org.jetbrains.annotations.NotNull;

public final class Constants {
    @NotNull
    public static final String USERS = "users";
    @NotNull
    public static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    @NotNull
    public static final String USER_ID = "USER_ID";
    @NotNull
    public static final String SIGN_UP_MODEL = "SIGN_UP_MODEL";
    @NotNull
    public static final String IS_FORGOT_PASSWORD = "IS_FORGOT_PASSWORD";
    @NotNull
    public static final String FIREBASE_ID = "FIREBASE_ID";
    @NotNull
    public static final String FROM_HOME = "FROM_HOME";
    @NotNull
    public static final Constants INSTANCE;
    @NotNull
    public static final String USER_ROLE = "USER_ROLE";
    @NotNull
    public static final String CUSTOMER_USER_ROLE = "Customer";
    @NotNull
    public static final String AGENCY_USER_ROLE = "Agency";
    @NotNull
    public static final String MAP_ADDRESS = "MAP_ADDRESS";
    @NotNull
    public static final String REQUESTS = "requests";
    @NotNull
    public static final String PENDING_REQUEST = "Pending";
    @NotNull
    public static final String APPROVED_REQUEST = "Approved";
    @NotNull
    public static final String REJECTED_REQUEST = "Rejected";
    @NotNull
    public static final String DATE_TIME_SEPARATOR = ", ";


    private Constants() {
    }

    static {
        Constants var0 = new Constants();
        INSTANCE = var0;
    }
}

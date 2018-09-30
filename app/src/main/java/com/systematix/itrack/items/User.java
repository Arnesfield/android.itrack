package com.systematix.itrack.items;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String number;
    private String firstName;
    private String middleName;
    private String lastName;
    private String picture;
    private String course;
    private String access;

    public User(JSONObject json) throws JSONException {
        this.id = json.getInt("user_id");
        this.firstName = json.getString("user_firstname");
        this.middleName = json.getString("user_middlename");
        this.lastName = json.getString("user_lastname");
        this.number = json.getString("user_number");
        this.picture = json.getString("user_picture");
        this.access = json.getString("user_access");
        this.course = json.getString("user_course");
    }

    // getters
    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        String name = firstName + " ";
        name += middleName != null && middleName.length() > 0 ? middleName + " " : "";
        name += lastName;
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getCourse() {
        return course;
    }

    public String getAccess() {
        return access;
    }
}

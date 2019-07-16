package com.example.fbu_res.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String KEY_DESCRIPTION = "";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_TIMESTAMP = "createdAt";
    public static final String KEY_OBJECT_ID = "objectId";

    public String getKeyDescription() {
        return getString(KEY_DESCRIPTION);
    }


    public String getKeyObjectId() {
        return getObjectId();
    }

    public void setKeyDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getKeyImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setKeyImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getKeyUser() {
        return getParseUser(KEY_USER);
    }

    public void setKeyUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getKeyTimestamp() {
        return getString(KEY_TIMESTAMP);
    }

    public void setKeyTimestamp(String timestamp) {
        put(KEY_TIMESTAMP, timestamp);
    }

    private static int lastPostId = 0;



}

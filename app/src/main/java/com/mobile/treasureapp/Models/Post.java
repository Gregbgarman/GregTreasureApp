package com.mobile.treasureapp.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Post")
public class Post extends ParseObject {         //stored on Parse

    public static final String KEY_USERNAME="PosterUserName",KEY_USERID="PosterID",KEY_USERPROFILEPIC="PosterProfilePic",
    KEY_CATEGORY="Category",KEY_REASON="PostReason",KEY_SCHOOL="SchoolAttending",KEY_ITEMNAME="ItemName",
    KEY_POSTIMAGE="PostImage";


    public void SetPosterProfilePic(ParseFile posterProfilePic) {
        put(KEY_USERPROFILEPIC,posterProfilePic);
    }

    public ParseFile GetPosterProfilePic() {
        return getParseFile(KEY_USERPROFILEPIC);
    }

    public void SetPostImage(ParseFile postimage) {
        put(KEY_POSTIMAGE,postimage);
    }

    public ParseFile GetPostImage() {
        return getParseFile(KEY_POSTIMAGE);
    }

    public void SetItemName(String itemName) {

        put(KEY_ITEMNAME,itemName);
    }

    public String GetItemName() {
        return getString(KEY_ITEMNAME);
    }

    public void SetCategory(String category) {
        put(KEY_CATEGORY,category);
    }

    public String GetCategory() {
        return getString(KEY_CATEGORY);
    }


    public void SetPostReason(String reason) {

        put(KEY_REASON,reason);
    }

    public String GetPostReason() {
        return getString(KEY_REASON);
    }

    public void SetPosterID(String posterID) {
        put(KEY_USERID,posterID);
    }

    public String GetPosterID() {
        return getString(KEY_USERID);
    }

    public void SetSchoolAttending(String schoolAttending) {
        put(KEY_SCHOOL,schoolAttending);
    }

    public String GetSchoolAttending() {
        return getString(KEY_SCHOOL);
    }

    public void SetPosterUserName(String posterUserName) {
        put(KEY_USERNAME,posterUserName);
    }

    public String GetPosterUserName() {
        return getString(KEY_USERNAME);
    }


}

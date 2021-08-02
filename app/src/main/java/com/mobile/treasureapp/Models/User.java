package com.mobile.treasureapp.Models;

import android.graphics.Bitmap;

import org.parceler.Parcel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {

    public String Email;
    public String Name;
    public String UserID;
    public Bitmap ProfilePicture;       //Bitmap never gets stored on cloud, this will get populated when loading from cloud
    public String ProfilePictureID;     //Each profile picture on cloud will have own ID, and there will be a "DefaultImage" also
    public String SchoolAttending;
    public String SchoolImageUrl;
    public ArrayList<String> FavoriteItemsId;
    public String FirebaseKey;

    public User(){

    }

    public void SetFirebaseKey(String key){
        FirebaseKey=key;
    }

    public String GetFirebaseKey(){
        return FirebaseKey;
    }

    public ArrayList<String> GetFavoriteItemsList(){
        return FavoriteItemsId;
    }

    public void SetFavoriteItemsList(ArrayList <String> favoriteItemsIdList){
        FavoriteItemsId=favoriteItemsIdList;
    }

    public void SetSchoolImageUrl(String url){
        SchoolImageUrl=url;
    }

    public String GetSchoolImageUrl(){
        return SchoolImageUrl;
    }

    public String GetSchoolAttending() {
        return SchoolAttending;
    }

    public void SetSchoolAttending(String schoolAttending) {
        SchoolAttending = schoolAttending;
    }

    public String GetProfilePictureID() {
        return ProfilePictureID;
    }

    public void SetProfilePictureID(String profilePictureID) {
        ProfilePictureID = profilePictureID;
    }

    public String GetEmail(){
        return Email;
    }

    public String GetName(){
        return Name;
    }

    public String GetUserID(){
        return UserID;
    }

    public Bitmap GetProfilePicture(){
        return ProfilePicture;
    }

    public void SetEmail(String email){
        Email=email;
    }

    public void SetName(String name){
        Name=name;
    }

    public void SetUserID(String id){
        UserID=id;
    }

    public void SetUserProfilePicture(Bitmap bitmap){
        ProfilePicture=bitmap;
    }

}

package com.mobile.treasureapp;

import android.app.Application;

import com.mobile.treasureapp.Models.MostRecentMessage;
import com.mobile.treasureapp.Models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class App extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(MostRecentMessage.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.ParseAppId))
                .clientKey(getResources().getString(R.string.ParseClientKey))
                .server("https://treasurebackend.b4a.app")   //https://dmchatapp.b4a.app"    "https://treasurebackend.b4a.app"  "https://parseapi.back4app.com" -original
                .build()
        );
    }
}

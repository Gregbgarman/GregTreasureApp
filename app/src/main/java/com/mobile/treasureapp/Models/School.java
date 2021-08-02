package com.mobile.treasureapp.Models;

import com.mobile.treasureapp.Activities.UserSchoolActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class School {       //will not be stored on Firebase, but created when parsing JSON and displayed in RV

    private String Schoolname;
    private String State;

    public School(){    //empty constructor needed for parceler library

    }

    public School(JSONObject jsonObject) throws JSONException {
        Schoolname=jsonObject.getString("name");
        State=jsonObject.getJSONObject("state").getString("name");

    }

    public String getSchoolname() {
        return Schoolname;
    }

    public String getState() {
        return State;
    }

    public void setSchoolname(String schoolname) {
        Schoolname = schoolname;
    }

    public void setState(String state) {
        State = state;
    }
}

package com.mobile.treasureapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.treasureapp.Adapters.ViewSchoolsAdapter;
import com.mobile.treasureapp.Dialogs.ConfirmSchoolDialog;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.School;
import com.mobile.treasureapp.Models.User;
import com.mobile.treasureapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class UserSchoolActivity extends AppCompatActivity implements ConfirmSchoolDialog.SaveProfileInterface  {

    private EditText etSchoolSearch;
    private RecyclerView rvViewSchools;
    public static ViewSchoolsAdapter viewSchoolsAdapter;
    private List<School> schoolList;
    private JSONArray ResultsArray;
    private User AppUser;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_school);
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));
        getSupportActionBar().hide();
        rvViewSchools=findViewById(R.id.rvShowSchools);
        etSchoolSearch=findViewById(R.id.etSearchSchools);

        AppUser= Parcels.unwrap(getIntent().getParcelableExtra("AppUser"));
        mdatabase=FirebaseDatabase.getInstance().getReference("User");

        etSchoolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewSchoolsAdapter.notifyDataSetChanged();
                try {
                    SearchSchools(etSchoolSearch.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewSchoolsAdapter.notifyDataSetChanged();
            }
        });



        ViewSchoolsAdapter.ShowDialogInterface showDialogInterface=new ViewSchoolsAdapter.ShowDialogInterface() {
            @Override
            public void ShowDialog(String school,String location) {
                FragmentManager fm = UserSchoolActivity.this.getSupportFragmentManager();
                ConfirmSchoolDialog confirmSchoolDialog=new ConfirmSchoolDialog(school);
                confirmSchoolDialog.show(fm,"confirm");
            }
        };

        schoolList=new ArrayList<>();
        viewSchoolsAdapter=new ViewSchoolsAdapter(this,schoolList,showDialogInterface);
        rvViewSchools.setAdapter(viewSchoolsAdapter);
        rvViewSchools.setLayoutManager(new LinearLayoutManager(this));


    }

    public void SearchSchools(String text) throws JSONException {

        String MySchool=getProperString(text);
        String where="%7B++++%22name%22%3A+%7B++++++++%22%24gte%22%3A+%22"+MySchool+"+%22++++%7D%7D";

        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   // String where = URLEncoder.encode("{" +"    \"name\": {" +"        \"$gte\": \"University of Southern \"" +
                   //         "    }" +
                   //         "}", "utf-8");
                    URL url = new URL("https://parseapi.back4app.com/classes/Usuniversitieslist_University?limit=6&include=state&keys=name,state,state.name&where=" + where);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestProperty("X-Parse-Application-Id", getResources().getString(R.string.SchoolsAPIKey)); // This is your app's application id
                    urlConnection.setRequestProperty("X-Parse-REST-API-Key", getResources().getString(R.string.SchoolsRestAPIKey)); // This is your app's REST API key
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        JSONObject data = new JSONObject(stringBuilder.toString()); // Here you have the data that you need
                        ResultsArray=data.getJSONArray("results");
                        Log.d("MainActivity", data.toString(2));
                    } finally {
                        urlConnection.disconnect();
                        schoolList.clear();
                        for (int i=0;i<ResultsArray.length();i++){
                            schoolList.add(new School(ResultsArray.getJSONObject(i)));
                        }


                    }
                } catch (Exception e) {
                    Log.e("MainActivity", e.toString());
                }
            }
        })).start();

    }

    public String getProperString(String text){

        String buildstring="";
        char c;

        for (int i=0;i<text.trim().length();i++){
            c=text.charAt(i);

            if (c==' '){
                buildstring+='+';
            }

            else{
                buildstring+=c;
            }

        }
        return buildstring;
    }


    @Override
    public void SaveProfile(String ShortSchoolName,String SchoolImageUrl) {         //finally creating profile
        String key=mdatabase.push().getKey();
        AppUser.SetSchoolAttending(ShortSchoolName);
        AppUser.SetSchoolImageUrl(SchoolImageUrl);
        AppUser.SetFirebaseKey(key);
        try {
            mdatabase.child(key).setValue(AppUser);
            Toast.makeText(UserSchoolActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserSchoolActivity.this, MainActivity.class));
            finish();
        }catch (Exception e){
            Toast.makeText(UserSchoolActivity.this, "Error Creating Account", Toast.LENGTH_SHORT).show();
        }


    }
}
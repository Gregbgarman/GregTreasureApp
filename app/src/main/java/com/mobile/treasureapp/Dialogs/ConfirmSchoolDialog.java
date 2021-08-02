package com.mobile.treasureapp.Dialogs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobile.treasureapp.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;


public class ConfirmSchoolDialog extends DialogFragment implements OnMapReadyCallback, View.OnClickListener {

    public interface SaveProfileInterface{
        public void SaveProfile(String ShortSchoolName,String SchoolImageUrl);
    }

    private GoogleMap TheGoogleMap;
    private String school;
    private String SchoolName;
    private String PhotoReference,SchoolAddress;
    private Double Lat,Long;
    private ImageView ivSchoolImage;
    private TextView tvSchoolName,tvSchoolAddress;
    private Button btnSave,btnGoBack;
    private SaveProfileInterface saveProfileInterface;
    private String SchoolImageUrl;

    public ConfirmSchoolDialog(String school){      //if searches fail, could try including state in search
        this.school=school;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivSchoolImage=view.findViewById(R.id.ivSchoolPhoto);
        tvSchoolName=view.findViewById(R.id.tvDialogSchoolName);
        btnGoBack=view.findViewById(R.id.btnWrongSchool);
        btnSave=view.findViewById(R.id.btnRightSchool);
        tvSchoolAddress=view.findViewById(R.id.tvSchoolAddress);
        btnGoBack.setOnClickListener(this);btnSave.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MyGooglemap);
        mapFragment.getMapAsync(this);

        QuerySchool();

      //  FindOnMap();

    //    https://maps.googleapis.com/maps/api/place/nearbysearch/json?&name=FloridaStateUniversity&key=AIzaSyDMg3b4aByb4RcbZM8a0siyx0z31IHjDT8
     //   https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Florida%20State%20University&inputtype=textquery&fields=geometry,photos,name,formatted_address&key=AIzaSyDMg3b4aByb4RcbZM8a0siyx0z31IHjDT8


   //     https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=Aap_uEAhq6CbIcFlT3kmrHDoTPvHTDI-giG-7BtqTpw1kqGozxzoVcUy180FKnWN6QXXXVZ13uwt4yeln21DPp2h9zGqeK3kn6cl8DMXzYiOyqE3luPYF9-A8fNEZkp22bQSqOB4y92xwJ1xxhFY1DBMZrlFColry5Gwr6zslNOS42teGwwR&key=AIzaSyDMg3b4aByb4RcbZM8a0siyx0z31IHjDT8

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_school_dialog, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        TheGoogleMap=googleMap;

    }

    public void QuerySchool(){
        String SearchURL=getURL("College");
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        asyncHttpClient.get(SearchURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject=json.jsonObject;

                try {
                    JSONArray jsonArray=jsonObject.getJSONArray("candidates");
                    SchoolAddress=jsonArray.getJSONObject(0).getString("formatted_address");
                    SchoolName=jsonArray.getJSONObject(0).getString("name");
                    Lat=jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    Long=jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    PhotoReference=jsonArray.getJSONObject(0).getJSONArray("photos").getJSONObject(0).getString("photo_reference");


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    SetUPDialog();
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e("SchoolDialog","json error");
            }

        });


    }

    public String getURL(String type){
        String url="";

        if (type.equals("College")){
            url="https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="+school+"&inputtype=textquery&fields=geometry,photos,name,formatted_address&key=" + getResources().getString(R.string.GoogleAPIKey);
        }

        else if (type.equals("Photo")){
            url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference="+PhotoReference+"&key="+getResources().getString(R.string.GoogleAPIKey);
            SchoolImageUrl=url;
        }

        return url;
    }

    public void SetUpImage(){
        String PhotoURL=getURL("Photo");
        Uri uri =  Uri.parse(PhotoURL);

        if (!PhotoURL.contentEquals("NA") && PhotoURL!=null) {
            Glide.with(this).load(uri).into(ivSchoolImage);
        }

    }

    public void SetUPDialog(){
        SetUpImage();
        tvSchoolName.setText(SchoolName);
        tvSchoolAddress.setText(SchoolAddress);
        LatLng latLng=new LatLng(Lat,Long);

        TheGoogleMap.addMarker(new MarkerOptions().position(latLng).title(SchoolName)).showInfoWindow();
        TheGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btnRightSchool:
                saveProfileInterface.SaveProfile(SchoolName,SchoolImageUrl);
                break;

            case R.id.btnWrongSchool:
                dismiss();
                break;
        }

    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        if(context instanceof ConfirmSchoolDialog.SaveProfileInterface){
            saveProfileInterface = (ConfirmSchoolDialog.SaveProfileInterface) context;
        }
        else{
            throw new RuntimeException(context.toString()+"must implement UpdateAdapterInterface");
        }

    }
}
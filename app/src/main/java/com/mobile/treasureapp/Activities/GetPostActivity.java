package com.mobile.treasureapp.Activities;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.treasureapp.Fragments.HomeFragment;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class GetPostActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etItemName,etReasonForPost;
    private Spinner CategorySpinner;
    private String[] Categories;
    private String SpinnerSelection;
    private Button btnLaunchCamera,btnAccessGallery,btnSubmitPost;
    private ImageView ivPostPicture;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    private Uri PictureUri;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private String PostID;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_post);
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));
        getSupportActionBar().hide();
        PictureUri=null;                //needs to be !=null when user submits a post
        SpinnerSelection="Please Select Category";      //needs to be something other than this upon submitting

        progressBar=findViewById(R.id.progress);
        progressBar.hide();
        ivPostPicture=findViewById(R.id.ivPostPicture);
        btnAccessGallery=findViewById(R.id.btnGetPhotoFromGallery);
        btnLaunchCamera=findViewById(R.id.btnLaunchCamera);
        btnSubmitPost=findViewById(R.id.btnSubmitPost);
        btnSubmitPost.setOnClickListener(this);
        btnAccessGallery.setOnClickListener(this);
        btnLaunchCamera.setOnClickListener(this);


        CategorySpinner=findViewById(R.id.Itemspinner);
        etItemName=findViewById(R.id.etItemPosting);
        etReasonForPost=findViewById(R.id.etPostReason);
        Categories=new String[]{"Please Select Category:","Furniture","Bedding/Mattress","Kitchen/Kitchen Appliances","Tailgating",
                "PC/Gaming","School Supplies","Transportation","Cleaning","Other"};

        ArrayAdapter<String> MyAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,Categories);
        MyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CategorySpinner.setAdapter(MyAdapter);
        CategorySpinner.setOnItemSelectedListener(spinnerListener);
        storageReference= FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("Post");

    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            SpinnerSelection=Categories[position];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLaunchCamera:
                LaunchCamera();
                break;

            case R.id.btnGetPhotoFromGallery:
                AccessGallery();
                break;

            case R.id.btnSubmitPost:
               if (AllFieldsFilled()==true){
                   progressBar.show();
                   SaveToParse();
               }
               else{
                   Toast.makeText(this, "Missing Needed Information", Toast.LENGTH_LONG).show();
               }
                break;


        }
    }

    public void LaunchCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(GetPostActivity.this, "com.treasureapp.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, 987);
        }

    }

    public void AccessGallery(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,567);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==567 && data!=null && data.getData()!=null){
            PictureUri=data.getData();
            Glide.with(this).load(PictureUri).into(ivPostPicture);
        }

        if (requestCode == 987) {               //for photos taken with camera
            if (resultCode == RESULT_OK) {
                // camera photo now on disk
                PictureUri=Uri.fromFile(photoFile.getAbsoluteFile());
                Glide.with(this).load(PictureUri).into(ivPostPicture);

            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "APP_TAG");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("tag", "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }



    public boolean AllFieldsFilled(){
        if (etItemName.getText().toString().trim().length()==0){
            return false;
        }

        if (etReasonForPost.getText().toString().trim().length()==0){
            return false;
        }

        if (SpinnerSelection.equals("Please Select Category:")){
            return false;
        }

        if (PictureUri==null){
            return false;
        }

        return true;
    }


    public void SaveToParse(){          //not using post id or image id's anymore-going straight to parse
        Post post=new Post();
        post.SetCategory(SpinnerSelection);
        post.SetPostReason(etReasonForPost.getText().toString().trim());
        post.SetPosterID(MainActivity.CurrentUser.UserID);
        post.SetSchoolAttending(MainActivity.CurrentUser.GetSchoolAttending());
        post.SetPosterUserName(MainActivity.CurrentUser.GetName());
        post.SetItemName(etItemName.getText().toString().trim());

        String filenamePost=getFileName(PictureUri);
        String FileProfilePic=getFileName(MainActivity.ProfilePictureUri);

        try {
            InputStream inputstream = this.getContentResolver().openInputStream(PictureUri);
            byte buffer[] = new byte[inputstream.available()];
            inputstream.read(buffer);
            ParseFile NewImage = new ParseFile(filenamePost, buffer);
            inputstream.close();

            post.SetPostImage(NewImage);

        }
        catch (IOException e){
            e.printStackTrace();
        }

        try {
            InputStream inputstream = this.getContentResolver().openInputStream(MainActivity.ProfilePictureUri);
            byte buffer[] = new byte[inputstream.available()];
            inputstream.read(buffer);
            ParseFile NewImage = new ParseFile(FileProfilePic, buffer);
            inputstream.close();
            post.SetPosterProfilePic(NewImage);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(GetPostActivity.this, "Post Saved!", Toast.LENGTH_SHORT).show();
                HomeFragment.JustMadePost=true;
                progressBar.hide();
                finish();
            }
        });


    }
    /*
    public void SaveToFireBase(){

        //9 setter methods

        Post post=new Post();
        post.SetCategory(SpinnerSelection);
        post.SetDatePosted(Calendar.getInstance().getTime());
        post.SetPostReason(etReasonForPost.getText().toString().trim());
        post.SetPosterID(MainActivity.CurrentUser.UserID);
        post.SetPostImageID(PostID);
        post.SetSchoolAttending(MainActivity.CurrentUser.GetSchoolAttending());
        post.SetPosterProfilePicID(MainActivity.CurrentUser.GetProfilePictureID());
        post.SetPosterUserName(MainActivity.CurrentUser.GetName());
        post.SetItemName(etItemName.getText().toString().trim());

        try {
            mDatabase.child(mDatabase.push().getKey()).setValue(post);
        }catch (Exception e){
            Toast.makeText(this, "Question Not Saved", Toast.LENGTH_SHORT).show();
            Log.e("GetPostActivity",e.getMessage());
            return;
        }

        Toast.makeText(this, "Post Saved", Toast.LENGTH_SHORT).show();
        this.finish();
    }
*/

    public String getFileName(Uri uri) {    //converting uri to string
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}
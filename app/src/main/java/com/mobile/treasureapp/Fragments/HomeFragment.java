package com.mobile.treasureapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.treasureapp.Activities.GetPostActivity;
import com.mobile.treasureapp.Activities.SendMessagesActivity;
import com.mobile.treasureapp.Activities.UserSchoolActivity;
import com.mobile.treasureapp.Adapters.GenericPostAdapter;
import com.mobile.treasureapp.Dialogs.ConfirmSchoolDialog;
import com.mobile.treasureapp.Dialogs.LongClickDialog;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.Models.Post;
import com.mobile.treasureapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Furniture
//Bedding/Mattress
//Kitchen/Kitchen Appliances
//PC/Gaming
//School Supplies
//Transportation
//Cleaning
//Tailgating
//Other

public class HomeFragment extends Fragment {    // LongClickDialog.SendMessageInterface {

    public interface HomeFragmentInterface{
        public void RefreshFeed();
    }

    public interface ShowOptionsInMain{
        public void GetOptions(Bundle bundle);
    }

    public ShowOptionsInMain showOptionsInMain;
    public static boolean JustMadePost;
    private HomeFragmentInterface homeFragmentInterface;
    private RecyclerView RVTailgating,RVKitchen,RVPCGaming,RVFurniture,RVOther,RVSupplies,RVCleaning,RVTransportation,RVBedding;
    private FloatingActionButton fabPost;
    private List<Post> postArrayList;
    private Spinner FilterSpinner;
    private  String[] TheCategories;
    private RecyclerView LoneRV;

    private HashMap<String,List<Post>> ListandTitleHashMap;

    private List<Post> KitchenList;
    private List<Post> TailgatingList;
    private List<Post> FurnitureList;
    private List<Post> PCGamingList;
    private List<Post> CleaningList;
    private List<Post> SuppliesList;
    private List<Post> OtherList;
    private List<Post> TransportationList;
    private List<Post> BeddingList;

    private List<Post> KitchenListLast10;           //last 10 lists used when viewing all categories on screen
    private List<Post> TailgatingListLast10;
    private List<Post> FurnitureListLast10;
    private List<Post> PCGamingListLast10;
    private List<Post> CleaningListLast10;
    private List<Post> SuppliesListLast10;
    private List<Post> OtherListLast10;
    private List<Post> TransportationListLast10;
    private List<Post> BeddingListLast10;

    private GenericPostAdapter KitchenAdapter,TailgatingAdapter,FurnitureAdapter,PCGamingAdapter,CleaningAdapter,
    SuppliesAdapter,OtherAdapter,TransportationAdapter,BeddingAdapter;

    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;

    private Bundle BundleSentFromAdapter;
    private GenericPostAdapter.GenericPostAdapterInterface genericPostAdapterInterface;
    private SwipeRefreshLayout swipeContainer;

    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(List<Post> postList){
        postArrayList=new ArrayList<>();
        postArrayList.addAll(postList);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        JustMadePost=false;

        swipeContainer=view.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.NiceBlue);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                homeFragmentInterface.RefreshFeed();
            }
        });

         genericPostAdapterInterface=new GenericPostAdapter.GenericPostAdapterInterface() {
            @Override
            public void ShowMenu(Bundle bundle) {
                showOptionsInMain.GetOptions(bundle);
            }
        };

        linearLayout=view.findViewById(R.id.FHLinearLayout);
        relativeLayout=view.findViewById(R.id.FHRelativeLayout);
        relativeLayout.setVisibility(View.INVISIBLE);

        RVFurniture=view.findViewById(R.id.rvFurniturePosts);
        RVKitchen=view.findViewById(R.id.rvKitchenPosts);
        RVPCGaming=view.findViewById(R.id.rvPCGamingPosts);
        RVTailgating=view.findViewById(R.id.rvTailgatingPosts);
        RVCleaning=view.findViewById(R.id.rvCleaningPosts);
        RVTransportation=view.findViewById(R.id.rvTransportationPosts);
        RVSupplies=view.findViewById(R.id.rvSuppliesPosts);
        RVBedding=view.findViewById(R.id.rvBedding);


        RVOther=view.findViewById(R.id.rvOtherPosts);
        LoneRV=view.findViewById(R.id.RVSingle);
        FilterSpinner=view.findViewById(R.id.FilterSpinner);

        fabPost=view.findViewById(R.id.fabPosts);
        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GetPostActivity.class));
            }
        });

        TheCategories=new String[]{"All Categories","Furniture","Bedding/Mattress","Kitchen/Kitchen Appliances","Tailgating",
                "PC/Gaming","School Supplies","Transportation","Cleaning","Other"};

        ArrayAdapter<String> MyAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,TheCategories);
        MyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        FilterSpinner.setAdapter(MyAdapter);
        FilterSpinner.setOnItemSelectedListener(spinnerListener);

        InitializeContainers();
        InitializeAllAdaptersLast10();

    }


    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String SpinnerSelection=TheCategories[position];
            if (SpinnerSelection.equals("All Categories")){
                InitializeContainers();
                InitializeAllAdaptersLast10();
            }
            else {
                ChangeLayout(SpinnerSelection);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (JustMadePost==true){
            homeFragmentInterface.RefreshFeed();
            JustMadePost=false;
        }
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeFragmentInterface){
            homeFragmentInterface = (HomeFragmentInterface) context;
        }

        if (context instanceof HomeFragment.ShowOptionsInMain){
            showOptionsInMain = (HomeFragment.ShowOptionsInMain) context;
        }

        else{
            throw new RuntimeException(context.toString()+
                    "must implement SettingsFragmentInterface");
        }

    }

    private void InitializeContainers() {
        KitchenList=new ArrayList<>();TailgatingList=new ArrayList<>();FurnitureList=new ArrayList<>();PCGamingList=new ArrayList<>();
        CleaningList=new ArrayList<>();OtherList=new ArrayList<>();TransportationList=new ArrayList<>();BeddingList=new ArrayList<>();
        SuppliesList=new ArrayList<>();

        KitchenListLast10=new ArrayList<>();TailgatingListLast10=new ArrayList<>();FurnitureListLast10=new ArrayList<>();
        PCGamingListLast10=new ArrayList<>();CleaningListLast10=new ArrayList<>();SuppliesListLast10=new ArrayList<>();
        OtherListLast10=new ArrayList<>();TransportationListLast10=new ArrayList<>();BeddingListLast10=new ArrayList<>();

        ListandTitleHashMap=new HashMap<>();


        for (Post post:postArrayList){      //every post for a school here

            if (post.GetCategory().equals("Furniture")){
                FurnitureList.add(post);
                if (FurnitureListLast10.size()<10) {
                    FurnitureListLast10.add(post);
                }

            }

            else if (post.GetCategory().equals("PC/Gaming")){
                PCGamingList.add(post);
                if (PCGamingListLast10.size()<10) {
                   PCGamingListLast10.add(post);
                }
            }

            else if (post.GetCategory().equals("Kitchen/Kitchen Appliances")){
                KitchenList.add(post);
                if (KitchenListLast10.size()<10) {
                    KitchenListLast10.add(post);
                }
            }

            else if (post.GetCategory().equals("Tailgating")){
                TailgatingList.add(post);
                if (TailgatingListLast10.size()<10) {
                    TailgatingListLast10.add(post);
                }
            }

            else if (post.GetCategory().equals("Bedding/Mattress")){
                BeddingList.add(post);
                if (BeddingListLast10.size()<10) {
                    BeddingListLast10.add(post);
                }
            }
            else if (post.GetCategory().equals("School Supplies")){
                SuppliesList.add(post);
                if (SuppliesListLast10.size()<10) {
                    SuppliesListLast10.add(post);
                }
            }
            else if (post.GetCategory().equals("Transportation")){
                TransportationList.add(post);
                if (TransportationListLast10.size()<10) {
                    TransportationListLast10.add(post);
                }
            }
            else if (post.GetCategory().equals("Cleaning")){
                CleaningList.add(post);
                if (CleaningListLast10.size()<10) {
                    CleaningListLast10.add(post);
                }
            }
            else if (post.GetCategory().equals("Other")){
               OtherList.add(post);
                if (OtherListLast10.size()<10) {
                    OtherListLast10.add(post);
                }
            }
        }

        //9 categories

        ListandTitleHashMap.put("Tailgating",TailgatingList);
        ListandTitleHashMap.put("Bedding/Mattress",BeddingList);
        ListandTitleHashMap.put("Furniture",FurnitureList);
        ListandTitleHashMap.put("Kitchen/Kitchen Appliances",KitchenList);
        ListandTitleHashMap.put("PC/Gaming",PCGamingList);
        ListandTitleHashMap.put("Transportation",TransportationList);
        ListandTitleHashMap.put("School Supplies",SuppliesList);
        ListandTitleHashMap.put("Cleaning",CleaningList);
        ListandTitleHashMap.put("Other",OtherList);

    }

    private void InitializeAllAdaptersLast10(){
        relativeLayout.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);

        FurnitureAdapter=new GenericPostAdapter(getContext(),FurnitureListLast10,true, genericPostAdapterInterface);
        RVFurniture.setAdapter(FurnitureAdapter);
        RVFurniture.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        PCGamingAdapter=new GenericPostAdapter(getContext(),PCGamingListLast10,true, genericPostAdapterInterface);
        RVPCGaming.setAdapter(PCGamingAdapter);
        RVPCGaming.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        KitchenAdapter=new GenericPostAdapter(getContext(),KitchenListLast10,true, genericPostAdapterInterface);
        RVKitchen.setAdapter(KitchenAdapter);
        RVKitchen.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        TailgatingAdapter=new GenericPostAdapter(getContext(),TailgatingListLast10,true, genericPostAdapterInterface);
        RVTailgating.setAdapter(TailgatingAdapter);
        RVTailgating.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        TransportationAdapter=new GenericPostAdapter(getContext(),TransportationListLast10,true, genericPostAdapterInterface);
        RVTransportation.setAdapter(TransportationAdapter);
        RVTransportation.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        CleaningAdapter=new GenericPostAdapter(getContext(),CleaningListLast10,true, genericPostAdapterInterface);
        RVCleaning.setAdapter(CleaningAdapter);
        RVCleaning.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        SuppliesAdapter=new GenericPostAdapter(getContext(),SuppliesListLast10,true, genericPostAdapterInterface);
        RVSupplies.setAdapter(SuppliesAdapter);
        RVSupplies.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        BeddingAdapter=new GenericPostAdapter(getContext(),BeddingListLast10,true, genericPostAdapterInterface);
        RVBedding.setAdapter(BeddingAdapter);
        RVBedding.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        OtherAdapter=new GenericPostAdapter(getContext(),OtherListLast10,true, genericPostAdapterInterface);
        RVOther.setAdapter(OtherAdapter);
        RVOther.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

    }

    private void ChangeLayout(String NewCategory){
       linearLayout.setVisibility(View.INVISIBLE);
       relativeLayout.setVisibility(View.VISIBLE);
        List<Post> LoneList=ListandTitleHashMap.get(NewCategory);

       GenericPostAdapter LoneAdapter=new GenericPostAdapter(getContext(),LoneList,false, genericPostAdapterInterface);
       LoneRV.setAdapter(LoneAdapter);
       LoneRV.setLayoutManager(new GridLayoutManager(this.getContext(), 2));

    }

}
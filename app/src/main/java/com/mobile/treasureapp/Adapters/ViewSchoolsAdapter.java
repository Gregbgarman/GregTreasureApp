package com.mobile.treasureapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.treasureapp.Models.School;
import com.mobile.treasureapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ViewSchoolsAdapter extends RecyclerView.Adapter<ViewSchoolsAdapter.ViewHolder> {

    public interface ShowDialogInterface{
       void ShowDialog(String school,String location);
    }

    private ShowDialogInterface showDialogInterface;
    private Context context;
    private List<School> schoolList;


    public ViewSchoolsAdapter(Context context, List<School> schoolList,ShowDialogInterface showDialogInterface){
        this.context=context;
        this.schoolList=schoolList;
        this.showDialogInterface=showDialogInterface;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder( @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.eachschoollayout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(schoolList.get(position));
    }

    @Override
    public int getItemCount() {
        return schoolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvSchoolName,tvState;
        private Button btnChooseSchool;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvSchoolName=itemView.findViewById(R.id.tvRVSchoolName);
            tvState=itemView.findViewById(R.id.tvRVState);
            btnChooseSchool=itemView.findViewById(R.id.btnChooseSchool);


        }

        public void bind(School school){
            tvSchoolName.setText(school.getSchoolname());
            tvState.setText(school.getState());

            btnChooseSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogInterface.ShowDialog(school.getSchoolname(),school.getState());
                }
            });
        }
    }
}

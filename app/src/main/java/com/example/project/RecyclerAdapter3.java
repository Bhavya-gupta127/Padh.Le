package com.example.project;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class RecyclerAdapter3 extends RecyclerView.Adapter<RecyclerAdapter3.ViewHolder> {

    private static final String TAG = "RecyclerAdapter3";
    List<Task_Card> myList;
    ArrayList <String> type;

    private RecyclerViewClickListener listener;

    public RecyclerAdapter3(List<Task_Card> myList, RecyclerViewClickListener listener) {
        this.myList = myList;

        this.listener = listener;

        type = new ArrayList<>();
        type.add("Small"); type.add("Medium"); type.add("Big"); type.add("Very Big");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.i(TAG, "onCreateViewHolder: ");

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item2, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.taskName.setText(myList.get(position).name);
        holder.taskTime.setText(myList.get(position).sTime);
        holder.taskType.setText(type.get(myList.get(position).id-1));
        if(myList.get(position).completed == true){
            holder.itemView.setBackgroundResource(R.drawable.bcomp);
        } else holder.itemView.setBackgroundResource(R.drawable.b);

        int index=myList.get(position).id;
        switch(index){
            case 1:
                holder.taskImage.setImageResource(R.drawable.small);
                break;
            case 2:
                holder.taskImage.setImageResource(R.drawable.medium);
                break;
            case 3:
                holder.taskImage.setImageResource(R.drawable.big);
                break;
            case 4:
                holder.taskImage.setImageResource(R.drawable.very_big);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView taskImage;
        TextView taskName, taskType, taskTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskImage = itemView.findViewById(R.id.taskImage);
            taskName = itemView.findViewById(R.id.taskName);
            taskType = itemView.findViewById(R.id.taskType);
            taskTime = itemView.findViewById(R.id.taskTime);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
    public interface  RecyclerViewClickListener{
        void onClick(View v, int position);
    }
}
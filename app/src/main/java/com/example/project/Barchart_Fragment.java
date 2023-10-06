package com.example.project;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class Barchart_Fragment extends Fragment {
    BarChart barChart;
    View view;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_barchart_, container, false);

        Log.d("hello","hi");
        BarHelper bh=new BarHelper();

        db.collection("users").document(user.getUid()).collection("Tasks")
                .whereEqualTo("completed", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int easy=0,medium=0,big=0,v_big=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Task_Card tt = document.toObject(Task_Card.class);
                                // getting the difficulty of tasks added and completed on PRESENT DAY
                                if(tt.getDate().equals(LocalDate.now().toString()))
                                {
                                    if (tt.getId() == 1)
                                        easy++;
                                    if (tt.getId() == 2)
                                        medium++;
                                    if (tt.getId() == 3)
                                        big++;
                                    if (tt.getId() == 4)
                                        v_big++;
                                }
                            }
                            // setting the info about the tasks that user did on the present day
                            bh.setEasy(easy);
                            bh.setMedium(medium);
                            bh.setBig(big);
                            bh.setV_big(v_big);
                            drawBC(bh);
                        }else{
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return view;
    }

    private void drawBC(BarHelper bh) {

        barChart = view.findViewById(R.id.barChart);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> xlabels = new ArrayList<>();
        xlabels.add("Small");
        xlabels.add("Medium");
        xlabels.add("Big");
        xlabels.add("Very Big");

        barEntries.add(new BarEntry(0, bh.getEasy()));
        barEntries.add(new BarEntry(1, bh.getMedium()));
        barEntries.add(new BarEntry(2, bh.getBig()));
        barEntries.add(new BarEntry(3, bh.getV_big()));
        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);

        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        BarData barData = new BarData(barDataSet);

        barDataSet.setGradientColor(Color.parseColor("#ffffff"), Color.parseColor("#8A2BE2"));
        barDataSet.setDrawValues(false);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xlabels));
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getAxisRight().setGranularity(1f);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.setData(barData);
        barChart.setExtraOffsets(35f, 35f, 35f, 35f);
        barChart.animateXY(0, 2000, Easing.EaseOutBack);
    }
}
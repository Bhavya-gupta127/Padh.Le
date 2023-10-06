package com.example.project;

import android.graphics.Color;
import android.graphics.Typeface;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Piechart_Fragment extends Fragment {
    PieChart pieChart;
    View view;

    ArrayList <Integer> clr = new ArrayList<>();
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setColorArray();
        view = inflater.inflate(R.layout.fragment_piechart_, container, false);

        db.collection("users").document(user.getUid()).collection("Tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Long> map_pc = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Task_Card tt=document.toObject(Task_Card.class);
                                if(tt.getTime()!=0 && tt.getDate().equals(LocalDate.now().toString()))
                                    map_pc.put(tt.getName(), tt.getTime());
                            }
                            pieChart = view.findViewById(R.id.pieChart);
                            ArrayList<PieEntry> pieEntries =new ArrayList<>();
                            for(String entry : map_pc.keySet()){
                                PieEntry pieEntry = new PieEntry(map_pc.get(entry).floatValue(), entry);
                                pieEntries.add(pieEntry);
                            }
                            drawPC(pieEntries);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return view;
    }

    private void setColorArray(){
        clr.add(Color.parseColor("#00008B"));
        clr.add(Color.parseColor("#4169E1"));
        clr.add(Color.parseColor("#8A2BE2"));
        clr.add(Color.parseColor("#EE82EE"));
    }

    private void drawPC( ArrayList<PieEntry> pieEntries) {
        Description desc = new Description();
        desc.setTextSize(10);
        desc.setText("Each task's contribution to total");
        pieChart.setDescription(desc);

        pieChart.getRenderer().getPaintRender().setShadowLayer(20f, 0f, 0f, Color.parseColor("#000000"));
        pieChart.setExtraOffsets(35f, 35f, 35f, 35f);
        PieDataSet pds = new PieDataSet(pieEntries, "");

        pds.setColors(clr);
        pds.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pds.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);;

        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        PieData pieData = new PieData(pds);
        pieData.setValueTextSize(20);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);

        pieChart.getLegend().setForm(Legend.LegendForm.LINE);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChart.getLegend().setTextSize(12);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterText("Task\nShare");
        pieChart.setCenterTextSize(15f);

        pieChart.setDrawEntryLabels(true);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.animateXY(2000, 2000, Easing.EaseOutBack);
    }
}
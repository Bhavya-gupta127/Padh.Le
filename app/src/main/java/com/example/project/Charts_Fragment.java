package com.example.project;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

// there are 3 Charts to statistically depict the Progress in Tasks of a User
// LineChart: Shows the number of Tasks that user Completed on each day of the previous week
// BarChart: Shows the number of Tasks of each difficulty, which were added and completed on the Present Day
// PieChart: Shows the total % of time spent on each Task on the Present Day

public class Charts_Fragment extends Fragment {

    private Button lineButton, pieButton, barButton;

    public Charts_Fragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_three, container, false);

        lineButton = v.findViewById(R.id.lineButton);
        pieButton = v.findViewById(R.id.pieButton);
        barButton = v.findViewById(R.id.barButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            replaceFragment(new Linechart_Fragment());
        }
        lineButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                replaceFragment(new Linechart_Fragment());
            }
        });
        barButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Barchart_Fragment());
            }
        });
        pieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Piechart_Fragment());
            }
        });
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flayout, fragment);
        fragmentTransaction.commit();
    }
}
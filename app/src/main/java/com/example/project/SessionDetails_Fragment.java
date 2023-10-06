package com.example.project;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// this fragment is to display the details of the users in the session
// on clicking a user card, it will take us to SessionFriend_Fragment, which will have details about their Tasks
// on hitting back, it will take us to SessionCreate_Fragment
public class SessionDetails_Fragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerAdapter2 recyclerAdapter2;

    RecyclerAdapter2.RecyclerViewClickListener listener;
    List<Detail_Card> myList;

    Button reload;
    Button back2;
    FloatingActionButton endsession;
    TextView display_ID;
    MainActivity ob;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_session, container, false);

        ob = (MainActivity)getActivity();
        myList = new ArrayList<>();
        reloadList();
        setOnClickListener();
        recyclerView = view.findViewById(R.id.listv);
        recyclerAdapter2 = new com.example.project.RecyclerAdapter2(myList, listener);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter2);

        ob.frag_no = 2;
        reload = view.findViewById(R.id.reload);
        back2 = view.findViewById(R.id.back2);
        endsession=view.findViewById(R.id.endsession);
        display_ID = view.findViewById(R.id.display_ID);
        display_ID.setText("Session ID: " + ob.ID);

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment beginPage = new SessionCreate_Fragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.container, beginPage).commit();
            }
        });

        // remove user from the session
        endsession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("sessions").document(ob.ID).collection("sessions").document(user.getUid())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                            reloadList();
                            Fragment beginPage = new SessionCreate_Fragment();
                            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                            fm.replace(R.id.container, beginPage).commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Deleted", "Error deleting document", e);
                        }
                    });
            }
        });
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadList();
            }
        });
        return view;
    }

    private void setOnClickListener() {
        listener = new RecyclerAdapter2.RecyclerViewClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v, int position) {
                ob.UID = myList.get(position).Uid;
                ob.myFriend = myList.get(position).Name;
                Fragment friend = new SessionFriend_Fragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.container, friend).commit();
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void reloadList(){
        db.collection("sessions").document(ob.ID).collection("sessions")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Detail_Card person = document.toObject(Detail_Card.class);
                        if(user.getDisplayName()==null)
                            person.setName("Name Find Error");
                        myList.add(person);
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(recyclerAdapter2);
                    // UI for RecyclerView
                    recyclerView.setAlpha(0);
                    recyclerView.setTranslationX(100);
                    recyclerView.animate().alpha(1).translationXBy(-100).setDuration(600);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
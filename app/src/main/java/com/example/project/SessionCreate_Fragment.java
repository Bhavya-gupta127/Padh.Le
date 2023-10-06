package com.example.project;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

// this fragment is to either create of join an existing session
// on success, it will take us to SessionDetails_Fragment, which will have details about the current session
public class SessionCreate_Fragment extends Fragment {
    Button join_button;
    Button create_button;
    EditText entered_id;
    CardView cardView, cardView1;
    FloatingActionButton signOut;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_begin, container, false);

        MainActivity ob = new MainActivity();
        ob.frag_no=1;

        join_button = view.findViewById(R.id.join_button);
        create_button = view.findViewById(R.id.create_button);
        entered_id = view.findViewById(R.id.entered_id);
        cardView = view.findViewById(R.id.cardView);
        cardView1 = view.findViewById(R.id.cardView1);
        signOut = view.findViewById(R.id.signOut);

        animateView(cardView1, 600);
        animateView(cardView, 1000);

        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=entered_id.getText().toString();
                if(str.length()==6) {
                    db.collection("sessions").document(str).collection("sessions")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(task.getResult().size() > 0) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                                ob.ID=str;

                                                joinsession(str);
                                                //go to session fragment
                                                Fragment session = new SessionDetails_Fragment();
                                                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                                                fm.replace(R.id.container, session).commit();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), "Session does not exists", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
                else
                    Toast.makeText(getActivity(), "Enter an ID of length 6", Toast.LENGTH_SHORT).show();
            }
        });

        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=getRand();

                while(str.length()!=6) {
                    str=getRand();
                }
                ob.ID = str;
                createSession(str);

                Fragment session = new SessionDetails_Fragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.container, session).commit();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).handleLogout(view);
            }
        });
        return view;
    }

    private void createSession(String str){
        db.collection("sessions").document(str).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("date", "Document exists!");
                        Toast.makeText(getActivity(), "Please create again ", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("date", "Document does not exist!");
                        joinsession(str);
                    }
                } else {
                    Log.d("date", "Failed with: ", task.getException());
                }
            }
        });
    }

    private void joinsession(String sessioncode) {
        Map<String, Object> details = new HashMap<>();
        details.put("Name", user.getDisplayName());
        details.put("Email", user.getEmail());
        details.put("Uid", user.getUid());
        db.collection("sessions").document(sessioncode).collection("sessions").document(user.getUid())
                .set(details)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing document", e);
                    }
                });
    }
    // to generate a random id for session
    private String getRand(){
        int random = (int)((Math.random())*1000000);
        return String.valueOf(random);
    }

    // to animate the cardviews
    private void animateView(View v, int duration){
        v.setAlpha(0f);
        v.setTranslationY(50);
        v.animate().alpha(1f).translationYBy(-50).setDuration(duration);
    }
}
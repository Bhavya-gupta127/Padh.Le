package com.example.project;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DisplayTasks_Fragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerAdapter.RecyclerViewClickListener listener;

    FloatingActionButton enter;
    List<Task_Card> myList;
    TextView predictedTime;
    LocalDate date = LocalDate.now();

    int small=0, big=0, medium=0, very_big=0;
    String url =  "https://cat1.pythonanywhere.com/predict";

    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DisplayTasks_Fragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Map<String, Object> mydate = new HashMap<>();
        mydate.put("completedTasks",0);
        db.collection("users").document(user.getUid()).collection("Completed").document(date.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("date", "Document exists!");
                    } else {
                        Log.d("date", "Document does not exist!, so we are creating one");
                        //add to doc
                        db.collection("users").document(user.getUid()).collection("Completed").document(date.toString())
                                .set(mydate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("date", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("date", "Error writing document", e);
                                    }
                                });
                    }
                } else {
                    Log.d("date", "Failed with: ", task.getException());
                }
            }
        });
        View v = inflater.inflate(R.layout.fragment_first, container, false);

        predictedTime = v.findViewById(R.id.predictedTime);
        myList = new ArrayList<>();

        enter = v.findViewById(R.id.enter);

        listUpdate();

        setOnClickListener();

        // initializing recyclerView to its XML component
        recyclerView = v.findViewById(R.id.recyclerView);
        // adding the list of Tasks and listener(which tells which task has been clicked) to the Recycler Adapter
        recyclerAdapter = new com.example.project.RecyclerAdapter(myList, listener);

        // using Linear Layout for our Recycler View
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // To display the data in the RecyclerView, an adapter is required. The adapter acts as a bridge between the data set
        // and the RecyclerView, providing the necessary views and data binding.
        recyclerView.setAdapter(recyclerAdapter);

        // Animation and UI
        recyclerView.setAlpha(0);
        recyclerView.setTranslationX(100);
        recyclerView.animate().alpha(1).translationXBy(-100).setDuration(1000);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), AddTask_Activity.class);
                startActivityForResult(intent, 1);
            }
        });

        // to implement the swipes, we have used ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setOnClickListener() {
        listener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent (getActivity(), Stopwatch_Activity.class);
                intent.putExtra("title", myList.get(position).name);
                intent.putExtra("time", myList.get(position).time);
                intent.putExtra("pos", position);
                intent.putExtra("uId", myList.get(position).uId);
                startActivityForResult(intent, 2);
            }
        };
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            String name = myList.get(position).name;
            int id=myList.get(position).id;
            String uId=myList.get(position).uId;
            long time=myList.get(position).time;
            String sTime=myList.get(position).sTime;
            switch(direction){
                // swipe left to delete the Task
                case ItemTouchHelper.LEFT:
                    db.collection("users").document(user.getUid()).collection("Tasks").document(uId)
                            .delete();
                    listUpdate();
                    Snackbar.make(recyclerView, "Deleted: " + name, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // add the task back to firestore
                                    firestoreUpdate(id,name,sTime,time,uId); // user defined func
                                    listUpdate();
                                }
                            }).show();
                    break;
                // swipe right to complete the task
                case ItemTouchHelper.RIGHT:
                    db.collection("users").document(user.getUid()).collection("Tasks").document(uId)
                            .update("completed", true);
                    // updating number of tasks a user has completed on that day (and not the day of creation of task)
                    db.collection("users").document(user.getUid()).collection("Completed").document(date.toString())
                            .update("completedTasks", FieldValue.increment(1));
                    listUpdate();

                    Snackbar.make(recyclerView, "Completed: " + name, Snackbar.LENGTH_LONG)
                            .setAction( "UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection("users").document(user.getUid()).collection("Tasks").document(uId)
                                            .update("completed", false);
                                    db.collection("users").document(user.getUid()).collection("Completed").document(date.toString())
                                            .update("completedTasks", FieldValue.increment(-1));
                                    listUpdate();
                                }
                            }).show();
                    break;
            }
        }

        // UI for RecyclerView
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // copy paste it from: https://github.com/xabaras/RecyclerViewSwipeDecorator
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftLabel("Delete Task")
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_done)
                    .addSwipeRightLabel("Task Completed")
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // this function adds new tasks to the list
        if(requestCode==1 && resultCode==getActivity().RESULT_OK){
            String name=data.getStringExtra("a");
            int id=data.getIntExtra("b",0);
            firestoreUpdate(id,name,"00:00:00",0,"NULL");
            listUpdate();
        }
        // this function gets the time spent on a task
        if(requestCode==2 && resultCode==getActivity().RESULT_OK){
            //update time here
            String uId=data.getStringExtra("uId");
            long time=data.getLongExtra("time",0);
            String sTime=data.getStringExtra("sTime");
            db.collection("users").document(user.getUid()).collection("Tasks").document(uId)
                    .update(
                            "time", time,
                            "sTime",sTime
                    );
            listUpdate();
        }
    }

    /////////////////////////Helper functions///////////////////////////////////////////

    // function to convert time in milliseconds to Time String Format
    String calcSTime(long time){
        String sTime ="";
        long sec=time/1000,min=0,hr=0;
        if(sec>=60){
            min=sec/60;
            sec=sec%60;
        }
        if(min>=60){
            hr=min/60;
            min=min%60;
        }

        if(hr<10)
            sTime+='0';
        sTime+=String.valueOf(hr)+":";
        if(min<10)
            sTime+='0';
        sTime+=String.valueOf(min)+":";
        if(sec<10)
            sTime+='0';
        sTime+= String.valueOf(sec);
        Log.d("TAG", "calcSTime: " + sTime);
        return sTime;
    }

    // funciton to predict the time
    // it is fetching the category of the User and then predicting time based on the Category
    public void Predict() {
        db.collection("users").document(user.getUid()).collection("Details")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Detail_Card dd=document.toObject(Detail_Card.class);
                        String cat=dd.getCategory();
                        long b = 0;
                        switch(cat){
                            case "1":
                                url= "https://catone.pythonanywhere.com/predict";
                                b=-5;
                                break;
                            case "2":
                                url= "https://cattwo.pythonanywhere.com/predict";
                                b=-1;
                                break;
                            case "3":
                                url= "https://catthree.pythonanywhere.com/predict";
                                b=37;
                                break;
                            case "4":
                                url= "https://catfour.pythonanywhere.com/predict";
                                b=38;
                                break;
                            default:
                                b=38;
                                url= "https://catfour.pythonanywhere.com/predict";

                        }
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String data = String.valueOf(jsonObject.getInt("time"));

                                        long b = 0;
                                        switch(cat){
                                            case "1":
                                                b=-5;
                                                break;
                                            case "2":
                                                b=-1;
                                                break;
                                            case "3":
                                                b=37;
                                                break;
                                            case "4":
                                                b=38;
                                                break;
                                            default:
                                                b=38;
                                        }
                                        long time = Integer.parseInt(data)-b;
                                        data = calcSTime(time*60*1000);
                                        predictedTime.setText(data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_LONG).show();
                                }
                            }){

                            @Override
                            protected Map<String, String> getParams(){
                                Map<String,String> params = new HashMap<String,String>();
                                params.put("small", String.valueOf(small));
                                params.put("medium", String.valueOf(medium));
                                params.put("big", String.valueOf(big));
                                params.put("very_big", String.valueOf(very_big));
                                return params;
                            }
                        };
                        try {
                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                            queue.add(stringRequest);
                        }catch (Exception e){
                            Log.d("catch", "message: ");
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addTag(int id){
        switch(id){
            case 1:
                small++;
                break;
            case 2:
                medium++;
                break;
            case 3:
                big++;
                break;
            case 4:
                very_big++;
                break;
        }
    }

    // to add the Task in Firestore
    private void firestoreUpdate(int id,String name,String sTime,long time,String uId) {
        // adding the Task to the Firestore
        Map<String,Object> taskDetail=new HashMap<>();
        taskDetail.put("name",name);
        taskDetail.put("id",id);
        taskDetail.put("time",time);
        taskDetail.put("completed",false);
        taskDetail.put("sTime",sTime);
        taskDetail.put("date", LocalDate.now().toString());
        // when we UNDO a task, uID!=NULL
        if(uId!="NULL") {
            taskDetail.put("uId",uId);
            db.collection("users").document(user.getUid()).collection("Tasks").document(uId).set(taskDetail, SetOptions.merge());
        }
        // when data is being added for the first time, uId == NULL
        else {
            DocumentReference docref = db.collection("users").document(user.getUid()).collection("Tasks").document();
            taskDetail.put("uId", docref.getId());
            db.collection("users").document(user.getUid()).collection("Tasks").document(docref.getId()).set(taskDetail, SetOptions.merge());
        }
    }

    // listUpdate is to bring the tasks from db who are not completed (completed==false)
    private void listUpdate() {
        db.collection("users").document(user.getUid()).collection("Tasks")
            .whereEqualTo("completed", false)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    myList.clear();
                    small=0;
                    big=0;
                    medium=0;
                    very_big=0;
                    // we are bringing all the documents of "Tasks" where completed == true
                    // and then we are converting it to the Object
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Task_Card tt=document.toObject(Task_Card.class);

                        myList.add(tt);
                        addTag(tt.getId());
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(recyclerAdapter);
                    Predict();
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
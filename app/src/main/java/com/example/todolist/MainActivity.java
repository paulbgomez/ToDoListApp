package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView mTaskListView;
    ArrayAdapter<String> mAdapterTasks;
    private String userEmail;
    private List<String> todos = new ArrayList<>();
    private List<String> todosId = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        // get user email
        userEmail = mAuth.getCurrentUser().getEmail();
        mTaskListView = (ListView) findViewById(R.id.list_todo);

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                Toast.makeText(this, "Registered", Toast.LENGTH_LONG).show();

                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // add task to db
                                String taskValue = taskEditText.getText().toString();

                                Map<String, String> task = new HashMap<>();
                                task.put("description", taskValue);
                                task.put("user_email", userEmail);

                                db.collection("todos").add(task);
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            case R.id.logout:
                mAuth.signOut();
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteTask(View view) {
        ImageButton deleteTaskBtn = (ImageButton) findViewById(R.id.task_delete);
        deleteTaskBtn.setBackgroundResource(android.R.color.transparent);
        deleteTaskBtn.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);

        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("todos").document(task)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DONE", "Task successfully deleted!");
                        updateUI();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error deleting task", e);
                    }
                });
        updateUI();
        startAnimation();
    }

    private void startAnimation() {
        View taskView = findViewById(R.id.task_item);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        taskView.startAnimation(animation);
    }

    public void editTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());

        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit task content")
                .setView(taskEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskValue = taskEditText.getText().toString();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("todos").document(task)
                                .update("description", taskValue)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(view.getContext(), "Worked.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Failed.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                        updateUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    private void updateUI() {
        db.collection("todos")
            .whereEqualTo("user_email", userEmail)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    todos.clear();
                    todosId.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.exists() && doc.get("description") != null) {
                            todos.add(doc.getString("description"));
                            todosId.add(doc.getId());
                        }

                        if(todos.size() == 0) {
                            mTaskListView.setAdapter(null);
                        } else {
                            mAdapterTasks = new ArrayAdapter<String>(MainActivity.this, R.layout.item_todo, R.id.task_title, todos);
                            mTaskListView.setAdapter(mAdapterTasks);
                        }
                    }
                }
            });
    }
}
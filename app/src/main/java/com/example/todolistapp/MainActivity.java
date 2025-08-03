package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TaskDatabase taskDatabase;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private EditText inputTitle, inputDescription;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View references
        inputTitle = findViewById(R.id.input_title);
        inputDescription = findViewById(R.id.input_description);
        btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.recycler_view);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(null, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onCheckChanged(Task task, boolean isChecked) {
                task.isCompleted = isChecked;
                new Thread(() -> taskDatabase.taskDao().update(task)).start();
            }

            @Override
            public void onLongClick(Task task) {
                new Thread(() -> taskDatabase.taskDao().delete(task)).start();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show());
            }
        });
        recyclerView.setAdapter(taskAdapter);

        // Database setup
        taskDatabase = TaskDatabase.getInstance(this);

        // Observe LiveData
        taskDatabase.taskDao().getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskAdapter.setTasks(tasks);
            }
        });

        // Add button logic
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = inputTitle.getText().toString().trim();
                String description = inputDescription.getText().toString().trim();

                if (title.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a task title", Toast.LENGTH_SHORT).show();
                    return;
                }

                Task newTask = new Task(title, description, false);

                new Thread(() -> {
                    taskDatabase.taskDao().insert(newTask);
                }).start();

                inputTitle.setText("");
                inputDescription.setText("");
            }
        });
    }
}

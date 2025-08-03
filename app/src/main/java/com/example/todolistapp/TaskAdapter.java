package com.example.todolistapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onCheckChanged(Task task, boolean isChecked);
        void onLongClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskCheckBox.setText(task.title);
        holder.taskCheckBox.setChecked(task.isCompleted);

        if (task.description != null && !task.description.isEmpty()) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(task.description);
        } else {
            holder.description.setVisibility(View.GONE);
        }

        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onCheckChanged(task, isChecked);
        });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(task);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckBox;
        TextView description;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);
            description = itemView.findViewById(R.id.task_description);
        }
    }
}


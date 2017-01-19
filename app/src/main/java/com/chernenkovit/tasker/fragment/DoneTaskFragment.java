package com.chernenkovit.tasker.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chernenkovit.tasker.R;
import com.chernenkovit.tasker.adapter.DoneTasksAdapter;
import com.chernenkovit.tasker.model.ModelTask;

import java.util.ArrayList;
import java.util.List;

import static com.chernenkovit.tasker.database.DBHelper.SELECTION_LIKE_TITLE;
import static com.chernenkovit.tasker.database.DBHelper.SELECTION_STATUS;
import static com.chernenkovit.tasker.database.DBHelper.TASK_DATE_COLUMN;

public class DoneTaskFragment extends TaskFragment {

    OnTaskRestoreListener onTaskRestoreListener;

    public DoneTaskFragment() {
        // Required empty public constructor
    }

    public interface OnTaskRestoreListener {
        void onTaskRestore(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTaskRestoreListener = (DoneTaskFragment.OnTaskRestoreListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskRestoreListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_done_task, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvDoneTasks);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DoneTasksAdapter(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(SELECTION_LIKE_TITLE + " AND " + SELECTION_STATUS,
                new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_DONE)},
                TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_DONE)},
                TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void moveTask(ModelTask task) {
        if (task.getDate() != 0) {
            alarmHelper.removeAlarm(task.getTimestamp());
        }
        onTaskRestoreListener.onTaskRestore(task);
    }
}

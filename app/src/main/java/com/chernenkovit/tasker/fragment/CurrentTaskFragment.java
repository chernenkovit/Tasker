package com.chernenkovit.tasker.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chernenkovit.tasker.R;
import com.chernenkovit.tasker.adapter.CurrentTasksAdapter;
import com.chernenkovit.tasker.model.ModelSeparator;
import com.chernenkovit.tasker.model.ModelTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.chernenkovit.tasker.database.DBHelper.SELECTION_LIKE_TITLE;
import static com.chernenkovit.tasker.database.DBHelper.SELECTION_STATUS;
import static com.chernenkovit.tasker.database.DBHelper.TASK_DATE_COLUMN;
import static com.chernenkovit.tasker.model.ModelSeparator.TYPE_FUTURE;
import static com.chernenkovit.tasker.model.ModelSeparator.TYPE_OVERDUE;
import static com.chernenkovit.tasker.model.ModelSeparator.TYPE_TODAY;
import static com.chernenkovit.tasker.model.ModelSeparator.TYPE_TOMORROW;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTaskFragment extends TaskFragment {

    OnTaskDoneListener onTaskDoneListener;

    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTaskDoneListener = (OnTaskDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskDoneListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CurrentTasksAdapter(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;

        ModelSeparator modelSeparator = null;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        if (newTask.getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());

            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(TYPE_OVERDUE);
                if (!adapter.containsSeparatorOverdue) {
                    adapter.containsSeparatorOverdue = true;
                    modelSeparator = new ModelSeparator(TYPE_OVERDUE);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(TYPE_TODAY);
                if (!adapter.containsSeparatorToday) {
                    adapter.containsSeparatorToday = true;
                    modelSeparator = new ModelSeparator(TYPE_TODAY);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(TYPE_TOMORROW);
                if (!adapter.containsSeparatorTomorrow) {
                    adapter.containsSeparatorTomorrow = true;
                    modelSeparator = new ModelSeparator(TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(TYPE_FUTURE);
                if (!adapter.containsSeparatorFuture) {
                    adapter.containsSeparatorFuture = true;
                    modelSeparator = new ModelSeparator(TYPE_FUTURE);
                }
            }
        }


        if (position != -1) {

            if (!adapter.getItem(position-1).isTask()){
                if (position-2>=0&&adapter.getItem(position-2).isTask()){
                    ModelTask task= (ModelTask) adapter.getItem(position-2);
                    if (task.getDateStatus()==newTask.getDateStatus()){
                        position-=1;
                    }
                } else if (position-2<0&&newTask.getDate()==0 ){
                    position-=1;
                }
            }

            if (modelSeparator!=null){
                adapter.addItem(position-1,modelSeparator);
            }

            adapter.addItem(position, newTask);
        } else {
            if (modelSeparator!=null){
                adapter.addItem(modelSeparator);
            }
            adapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(SELECTION_LIKE_TITLE + " AND " +
                        SELECTION_STATUS + " OR " + SELECTION_STATUS,
                new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)},
                TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(SELECTION_STATUS + " OR " + SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)},
                TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void moveTask(ModelTask task) {
        alarmHelper.removeAlarm(task.getTimestamp());
        onTaskDoneListener.onTaskDone(task);
    }
}

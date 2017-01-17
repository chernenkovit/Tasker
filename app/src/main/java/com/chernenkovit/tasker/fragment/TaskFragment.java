package com.chernenkovit.tasker.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chernenkovit.tasker.MainActivity;
import com.chernenkovit.tasker.R;
import com.chernenkovit.tasker.adapter.TaskAdapter;
import com.chernenkovit.tasker.model.Item;
import com.chernenkovit.tasker.model.ModelTask;

public abstract class TaskFragment extends Fragment {

    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected TaskAdapter adapter;
    public MainActivity activity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }
        addTaskFromDB();
    }

    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        if (position != -1) {
            adapter.addItem(position, newTask);
        } else {
            adapter.addItem(newTask);
        }

        if (saveToDB){
            activity.dbHelper.saveTask(newTask);
        }
    }

    public void removeTaskDialog(final int location){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_remove_message);
        Item item=adapter.getItem(location);
        if (item.isTask()){
            ModelTask removingTask=(ModelTask) item;
            final long timestamp=removingTask.getTimestamp();
            final boolean[] isRemoved = {false};

            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapter.removeItem(location);
                    isRemoved[0] =true;

                    Snackbar snackbar=Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                            R.string.removed,
                            Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addTask(activity.dbHelper.query().getTask(timestamp),false);
                            isRemoved[0]=false;
                        }
                    });

                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View view) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View view) {
                            if (isRemoved[0]){
                                activity.dbHelper.removeTask(timestamp);
                            }
                        }
                    });

                    snackbar.show();

                    dialogInterface.dismiss();
                }
            });

            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }
        builder.show();
    }

    public abstract void addTaskFromDB();

    public abstract void moveTask(ModelTask task);
}

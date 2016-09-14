package com.example.abhishek.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.abhishek.todoapp.R;
import com.example.abhishek.todoapp.Models.TodoItem;

import java.util.ArrayList;


/**
 * Created by abhishek on 9/12/16.
 */
public class TodoListAdapter extends ArrayAdapter<TodoItem> {

    /**
     * Constructor
     * @param context
     * @param todoItems
     */
    public TodoListAdapter(Context context, ArrayList<TodoItem> todoItems) {
        super(context, 0, todoItems);
    }


    /**
     * This method generate single custom list row
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Gets the TodoItem object from the TodoListAdapter at the appropriate position
        TodoItem todoItem = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_todo_list_item, parent, false);
        }

        // TodoName
        TextView todoName = (TextView) convertView.findViewById(R.id.todo_name_tv);
        todoName.setText(todoItem.getTodoName());

        // TodoNote
        TextView todoNote = (TextView) convertView.findViewById(R.id.todo_note_tv);
        todoNote.setText(todoItem.getTodoNote());

        return convertView;
    }


}

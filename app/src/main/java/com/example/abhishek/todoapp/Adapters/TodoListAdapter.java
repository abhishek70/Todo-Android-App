package com.example.abhishek.todoapp.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.abhishek.todoapp.R;
import com.example.abhishek.todoapp.Models.TodoItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.webkit.WebSettings.RenderPriority.HIGH;


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
        TextView todoNameTv = (TextView) convertView.findViewById(R.id.todo_name_tv);
        todoNameTv.setText(todoItem.getTodoName());

        // TodoNote
        TextView todoNoteTv = (TextView) convertView.findViewById(R.id.todo_note_tv);
        todoNoteTv.setText(todoItem.getTodoNote());

        // TodoPriority
        TextView todoPriorityTv = (TextView) convertView.findViewById(R.id.todo_priority_tv);
        todoPriorityTv.setText(todoItem.getTodoPriority());

        // Setting different colors depending on the priority
        int colorId = ContextCompat.getColor(getContext(),R.color.lowPriority);

        switch (todoItem.getTodoPriority()){
            case "HIGH":
                colorId = ContextCompat.getColor(getContext(),R.color.highPriority);
                break;

            case "MEDIUM":
                colorId = ContextCompat.getColor(getContext(),R.color.mediumPriority);
                break;

            case "LOW":
                colorId = ContextCompat.getColor(getContext(),R.color.lowPriority);
                break;

            default:
                colorId = ContextCompat.getColor(getContext(),R.color.lowPriority);
                break;

        }

        todoPriorityTv.setTextColor(colorId);

        // TodoDueDate
        TextView todoDueDateTv  = (TextView) convertView.findViewById(R.id.todo_due_date_tv);

        try {
            // Converting the date into readable format
            String dateString = todoItem.getTodoDueDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            String formattedDate = outputFormat.format(date);

            todoDueDateTv.setText(getContext().getString(R.string.todo_due_date_label)+" "+formattedDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }


}

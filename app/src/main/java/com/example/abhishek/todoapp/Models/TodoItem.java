package com.example.abhishek.todoapp.Models;

import java.util.Date;

/**
 * Created by abhishek on 9/12/16.
 */
public class TodoItem {

    private long mTodoId;
    private String mTodoName;
    private String mTodoNote;
    private String mTodoPriority;
    private String mTodoDueDate;

    /**
     * Empty Constructor
     */
    public TodoItem() {

    }

    /**
     * Constructor for setting TodoName, TodoNote
     * @param todoName
     * @param todoNote
     * @param todoDueDate
     */
    public TodoItem(long todoId, String todoName, String todoNote, String todoPriority,
                    String todoDueDate){
        mTodoId         = todoId;
        mTodoName       = todoName;
        mTodoNote       = todoNote;
        mTodoPriority   = todoPriority;
        mTodoDueDate    = todoDueDate;
    }

    /**
     * Constructor for setting TodoName, TodoNote
     * @param todoName
     * @param todoNote
     */
    public TodoItem(String todoName, String todoNote, String todoPriority,
                    String todoDueDate){
        mTodoName       = todoName;
        mTodoNote       = todoNote;
        mTodoPriority   = todoPriority;
        mTodoDueDate    = todoDueDate;
    }

    /**
     * Setter method for TodoId
     * @param todoId
     */
    public void setTodoId(long todoId) {
        mTodoId = todoId;
    }

    /**
     * Getter method for TodoId
     * @return
     */
    public long getTodoId(){
        return mTodoId;
    }

    /**
     * Setter method for TodoName
     * @param todoName
     */
    public void setTodoName(String todoName) {
        mTodoName = todoName;
    }

    /**
     * Getter method for TodoName
     * @return
     */
    public String getTodoName(){
        return mTodoName;
    }


    /**
     * Setter method for TodoNote
     * @param todoNote
     */
    public void setTodoNote(String todoNote) {
        mTodoNote = todoNote;
    }


    /**
     * Getter method for TodoNote
     * @return
     */
    public String getTodoNote(){
        return mTodoNote;
    }


    /**
     * Setter method for TodoPriority
     * @param todoPriority
     */
    public void setTodoPriority(String todoPriority) {
        mTodoPriority = todoPriority;
    }


    /**
     * Getter method for TodoPriority
     * @return
     */
    public String getTodoPriority() {
        return mTodoPriority;
    }


    public void setTodoDueDate(String todoDueDate) {
        mTodoDueDate = todoDueDate;
    }

    public String getTodoDueDate() {
        return mTodoDueDate;
    }

}

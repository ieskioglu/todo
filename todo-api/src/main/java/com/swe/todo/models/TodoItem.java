package com.swe.todo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document
@Scope("todo_scope")
@Collection("todo_item")
public class TodoItem {
    @Id
    private String id;
    private String listId;
    private String title;
    private String notes;
    private Boolean priority;
    private Date reminder;
    private Boolean completed;
    private Date completedDate;

    static TodoItem of(String listId, String title) {
        return new TodoItem(null, listId, title);
    }

    public TodoItem(String id,
                    String listId,
                    String title) {
        this.id = id;
        this.listId = listId;
        this.title = title;
        this.completed = false;
        this.priority = false;
    }

    public void Complete() {
        this.completed = true;
        this.completedDate = new Date();
    }

    public void changePriority() {
        this.priority = !this.priority;
    }

    public void UnComplete() {
        this.completed = false;
        this.completedDate = null;
    }

    public void Update(String title, String notes, Date reminder) {
        this.title = title;
        this.notes = notes;
        this.reminder = reminder;
    }
}

package com.swe.todo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

import java.util.Date;

@Getter
@AllArgsConstructor
@Document
@Scope("todo_scope")
@Collection("todo_list")
public class TodoList {
    @Id
    private String id;
    private String title;
    private String userId;
    private Date created;

    public void ChangeTitle(String title) {
        this.title = title;
    }
}

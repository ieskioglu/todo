package com.swe.todo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Scope;

@Getter
@AllArgsConstructor
@Document
@Scope("todo_scope")
@Collection("user_list")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
}

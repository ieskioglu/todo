package com.swe.todo.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTodoItemRequest {
    private String listId;
    private String title;
}

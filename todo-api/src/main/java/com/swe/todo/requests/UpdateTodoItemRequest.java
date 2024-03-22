package com.swe.todo.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateTodoItemRequest {
    private String title;
    private String notes;
    private Date reminder;
}

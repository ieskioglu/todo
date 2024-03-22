package com.swe.todo.repositories;

import com.swe.todo.models.TodoItem;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

import java.util.List;

public interface TodoItemRepository extends CouchbaseRepository<TodoItem, String> {
    List<TodoItem> findByListId(String listId);
}

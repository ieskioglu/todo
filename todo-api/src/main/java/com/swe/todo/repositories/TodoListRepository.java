package com.swe.todo.repositories;

import com.swe.todo.models.TodoList;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoListRepository extends CouchbaseRepository<TodoList, String> {
    List<TodoList> findByUserId(String userId, Sort sort);
}

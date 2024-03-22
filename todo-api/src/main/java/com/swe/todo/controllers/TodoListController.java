package com.swe.todo.controllers;

import com.swe.todo.models.TodoItem;
import com.swe.todo.models.TodoList;
import com.swe.todo.repositories.TodoItemRepository;
import com.swe.todo.repositories.TodoListRepository;
import com.swe.todo.requests.CreateTodoListRequest;
import com.swe.todo.requests.UpdateTodoListRequest;
import com.swe.todo.securities.SignedUserHelper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/todoLists")
public class TodoListController {
    private final TodoListRepository todoListRepository;
    private final TodoItemRepository todoItemRepository;

    TodoListController(TodoListRepository todoListRepository, TodoItemRepository todoItemRepository) {
        this.todoListRepository = todoListRepository;
        this.todoItemRepository = todoItemRepository;
    }

    @GetMapping
    public ResponseEntity<List<TodoList>> getAll() {
        var userId = SignedUserHelper.userId();
        Sort sort = Sort.by("created").ascending();
        return ResponseEntity.ok(todoListRepository.findByUserId(userId, sort));
    }

    @PostMapping
    public ResponseEntity<TodoList> create(@RequestBody CreateTodoListRequest request) {
        var userId = SignedUserHelper.userId();

        var todoList = new TodoList(
                UUID.randomUUID().toString(),
                request.getTitle(),
                userId,
                new Date());

        return ResponseEntity.ok(todoListRepository.save(todoList));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateTodoListRequest request) {
        var userId = SignedUserHelper.userId();

        var existsList = todoListRepository.findById(id);

        if (existsList.isEmpty()) {
            return new ResponseEntity<>(
                    "Liste bulunamadı",
                    HttpStatus.BAD_REQUEST);
        }

        var todoList = existsList.get();

        if (!Objects.equals(todoList.getUserId(), userId)) {
            return new ResponseEntity<>(
                    "Liste üzerinde işlem yapma yetkiniz bulunmamaktadır",
                    HttpStatus.BAD_REQUEST);
        }

        todoList.ChangeTitle(request.getTitle());

        return ResponseEntity.ok(todoListRepository.save(todoList));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity update(@PathVariable String id) {
        var userId = SignedUserHelper.userId();

        var existsList = todoListRepository.findById(id);

        if (existsList.isEmpty()) {
            return new ResponseEntity<>(
                    "Liste bulunamadı",
                    HttpStatus.BAD_REQUEST);
        }

        var todoList = existsList.get();

        if (!Objects.equals(todoList.getUserId(), userId)) {
            return new ResponseEntity<>(
                    "Liste üzerinde işlem yapma yetkiniz bulunmamaktadır",
                    HttpStatus.BAD_REQUEST);
        }

        Iterable<TodoItem> todoItems = todoItemRepository.findByListId(todoList.getId());

        todoListRepository.delete(todoList);
        todoItemRepository.deleteAll(todoItems);

        return ResponseEntity.noContent().build();
    }
}

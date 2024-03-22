package com.swe.todo.controllers;

import com.swe.todo.models.TodoItem;
import com.swe.todo.repositories.TodoItemRepository;
import com.swe.todo.repositories.TodoListRepository;
import com.swe.todo.requests.CreateTodoItemRequest;
import com.swe.todo.requests.UpdateTodoItemRequest;
import com.swe.todo.securities.SignedUserHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/todoItems")
public class TodoItemController {

    private final TodoItemRepository todoItemRepository;
    private final TodoListRepository todoListRepository;

    public TodoItemController(TodoItemRepository todoItemRepository, TodoListRepository todoListRepository) {
        this.todoItemRepository = todoItemRepository;
        this.todoListRepository = todoListRepository;
    }

    @GetMapping("/{listId}")
    public ResponseEntity<List<TodoItem>> getAllItems(@PathVariable String listId) {
        return ResponseEntity.ok(todoItemRepository.findByListId(listId));
    }

    @PostMapping()
    public ResponseEntity<TodoItem> createItem(@RequestBody CreateTodoItemRequest request) {
        var todoItem = new TodoItem(UUID.randomUUID().toString(), request.getListId(), request.getTitle());

        return ResponseEntity.ok(todoItemRepository.save(todoItem));
    }

    @PutMapping("/{id}/updateCompletedStatus")
    public ResponseEntity<?> updateCompletedStatus(@PathVariable String id) {
        var todoItem = todoItemRepository.findById(id);
        if (todoItem.isPresent()) {
            var item = todoItem.get();
            if (item.getCompleted()) {
                item.UnComplete();
            } else {
                item.Complete();
            }
            item = todoItemRepository.save(item);
            return ResponseEntity.ok(item);
        }

        return new ResponseEntity<>(
                "Görev bulunamadı",
                HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}/updatePriorityStatus")
    public ResponseEntity<?> updatePriorityStatus(@PathVariable String id) {
        var todoItem = todoItemRepository.findById(id);
        if (todoItem.isPresent()) {
            var item = todoItem.get();
            item.changePriority();
            item = todoItemRepository.save(item);
            return ResponseEntity.ok(item);
        }

        return new ResponseEntity<>(
                "Görev bulunamadı",
                HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateTodoItemRequest request) {
        var userId = SignedUserHelper.userId();

        var existsItem = todoItemRepository.findById(id);

        if (existsItem.isEmpty()) {
            return new ResponseEntity<>(
                    "Görev bulunamadı",
                    HttpStatus.BAD_REQUEST);
        }

        var todoItem = existsItem.get();
        var todoList = todoListRepository.findById(todoItem.getListId());
        if (todoList.isEmpty() || !Objects.equals(todoList.get().getUserId(), userId)) {
            return new ResponseEntity<>(
                    "Bu görev üzerinde işlem yapma yetkiniz bulunmamaktadır",
                    HttpStatus.BAD_REQUEST);
        }

        todoItem.Update(request.getTitle(), request.getNotes(), request.getReminder());
        todoItemRepository.save(todoItem);

        return ResponseEntity.ok(todoItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity update(@PathVariable String id) {
        var userId = SignedUserHelper.userId();

        var existsItem = todoItemRepository.findById(id);

        if (existsItem.isEmpty()) {
            return new ResponseEntity<>(
                    "Görev bulunamadı",
                    HttpStatus.BAD_REQUEST);
        }

        var todoItem = existsItem.get();
        var todoList = todoListRepository.findById(todoItem.getListId());
        if (todoList.isEmpty() || !Objects.equals(todoList.get().getUserId(), userId)) {
            return new ResponseEntity<>(
                    "Bu görev üzerinde işlem yapma yetkiniz bulunmamaktadır",
                    HttpStatus.BAD_REQUEST);
        }

        todoItemRepository.delete(todoItem);

        return ResponseEntity.noContent().build();
    }
}

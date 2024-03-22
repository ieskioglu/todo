import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateTodoItemRequest } from '../models/create-todo-item-request';
import { TodoItem } from '../models/todo-item';
import { UpdateTodoItemRequest } from '../models/update-todo-item-request';

@Injectable({
  providedIn: 'root',
})
export class TodoItemService {
  constructor(private http: HttpClient) {}

  getAll(listId: string): Observable<Array<TodoItem>> {
    return this.http.get<Array<TodoItem>>(`/api/todoItems/${listId}`);
  }

  create(request: CreateTodoItemRequest): Observable<TodoItem> {
    return this.http.post<TodoItem>('/api/todoItems', request);
  }

  updateCompletedStatus(id: string): Observable<TodoItem> {
    return this.http.put<TodoItem>(
      `/api/todoItems/${id}/updateCompletedStatus`,
      {}
    );
  }

  updatePriorityStatus(id: string): Observable<TodoItem> {
    return this.http.put<TodoItem>(
      `/api/todoItems/${id}/updatePriorityStatus`,
      {}
    );
  }

  update(id: string, request: UpdateTodoItemRequest): Observable<TodoItem> {
    return this.http.put<TodoItem>(`/api/todoItems/${id}`, request);
  }

  delete(id: string) {
    return this.http.delete(`/api/todoItems/${id}`);
  }
}

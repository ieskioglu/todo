import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TodoList } from '../models/todo-list';
import { CreateTodoListRequest } from '../models/todo-list-request';
import { UpdateTodoListRequest } from '../models/update-todo-list-request';

@Injectable({
  providedIn: 'root',
})
export class TodoListService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Array<TodoList>> {
    return this.http.get<Array<TodoList>>('/api/todoLists');
  }

  create(request: CreateTodoListRequest): Observable<TodoList> {
    return this.http.post<TodoList>('/api/todoLists', request);
  }

  update(id: string, request: UpdateTodoListRequest): Observable<TodoList> {
    return this.http.put<TodoList>(`/api/todoLists/${id}`, request);
  }

  delete(id: string) {
    return this.http.delete(`/api/todoLists/${id}`);
  }
}

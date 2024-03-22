import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { NzDescriptionsModule } from 'ng-zorro-antd/descriptions';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzDrawerModule } from 'ng-zorro-antd/drawer';
import {
  NzContextMenuService,
  NzDropDownModule,
  NzDropdownMenuComponent,
} from 'ng-zorro-antd/dropdown';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzListModule } from 'ng-zorro-antd/list';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzPopconfirmModule } from 'ng-zorro-antd/popconfirm';
import { NzSpaceModule } from 'ng-zorro-antd/space';
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { NzTypographyModule } from 'ng-zorro-antd/typography';
import { CreateTodoItemRequest } from '../models/create-todo-item-request';
import { TodoItem } from '../models/todo-item';
import { TodoList } from '../models/todo-list';
import { CreateTodoListRequest } from '../models/todo-list-request';
import { UpdateTodoItemRequest } from '../models/update-todo-item-request';
import { UpdateTodoListRequest } from '../models/update-todo-list-request';
import { AuthService } from '../services/auth.service';
import { TodoItemService } from '../services/todo-item.service';
import { TodoListService } from '../services/todo-list.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    NzLayoutModule,
    NzIconModule,
    NzMenuModule,
    RouterModule,
    NzModalModule,
    NzFormModule,
    NzInputModule,
    ReactiveFormsModule,
    NzTypographyModule,
    FormsModule,
    NzListModule,
    NzToolTipModule,
    NzDrawerModule,
    NzDatePickerModule,
    NzAvatarModule,
    NzDividerModule,
    NzDropDownModule,
    NzPageHeaderModule,
    NzSpaceModule,
    NzDescriptionsModule,
    NzButtonModule,
    NzPopconfirmModule,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  isCollapsed: boolean = false;
  isVisible: boolean = false;
  todoLists: Array<TodoList> = [];
  selectedTodoList: TodoList = {} as TodoList;
  todoItemTitle: string = '';
  todoItems: Array<TodoItem> = [];
  isEditMode: boolean = false;

  selectedItem: TodoItem = {} as TodoItem;

  validateForm: FormGroup<{
    title: FormControl<string>;
  }> = this.fb.group({
    title: ['', [Validators.required]],
  });

  validateItemForm: FormGroup<{
    title: FormControl<string>;
    notes: FormControl<string | null>;
    reminder: FormControl<Date | null>;
  }> = this.nfb.group({
    title: this.fb.control<string>('', [Validators.required]),
    notes: this.fb.control<string | null>(''),
    reminder: this.fb.control<Date | null>(null),
  });

  constructor(
    private todoListService: TodoListService,
    private todoItemService: TodoItemService,
    private fb: NonNullableFormBuilder,
    private nfb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private nzContextMenuService: NzContextMenuService
  ) {
    const id = this.route.snapshot.queryParamMap.get('id');
    this.todoListService.getAll().subscribe({
      next: (res) => {
        this.todoLists = res;
        if (id) {
          this.setSelect(id);
        }
      },
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/sign-in']);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe({
      next: (queryParams: any) => {
        this.setSelect(queryParams.id);
      },
    });
  }

  setSelect(id: string) {
    this.todoLists.map((l) => (l.selected = false));
    if (this.todoLists.find((p) => p.id == id)) {
      this.selectedTodoList = this.todoLists.find((p) => p.id == id)!;
      this.selectedTodoList.selected = true;
      this.getTodoItems();
    } else {
      if (this.selectedTodoList) {
        this.selectedTodoList = {} as TodoList;
      }
    }
  }

  getTodoItems() {
    this.todoItemService.getAll(this.selectedTodoList.id).subscribe({
      next: (res) => (this.todoItems = res),
    });
  }

  newList() {
    this.isEditMode = false;
    this.isVisible = true;
  }

  deleteList(list: TodoList) {
    this.todoListService.delete(list.id).subscribe({
      next: () => {
        const index = this.todoLists.findIndex((p) => p.id == list.id);
        this.todoLists.splice(index, 1);
        this.router.navigate(['/home']);
      },
    });
  }

  editList() {
    this.isEditMode = true;
    this.validateForm.patchValue({
      title: this.selectedTodoList.title,
    });
    this.isVisible = true;
  }

  handleCancel() {
    this.isVisible = false;
  }

  handleOk() {
    if (!this.validateForm.valid) {
      Object.values(this.validateForm.controls).forEach((control) => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      });
      return;
    }
    if (!this.isEditMode) {
      var request: CreateTodoListRequest = this.validateForm
        .value as CreateTodoListRequest;
      this.todoListService
        .create(request)
        .pipe()
        .subscribe({
          next: (res) => {
            res.selected = true;
            this.todoLists.push(res);
            this.isVisible = false;
            this.router.navigate(['/home'], { queryParams: { id: res.id } });
            this.validateForm.reset();
          },
        });
    } else {
      var request: UpdateTodoListRequest = this.validateForm
        .value as UpdateTodoListRequest;
      this.todoListService
        .update(this.selectedTodoList.id, request)
        .pipe()
        .subscribe({
          next: (res) => {
            this.selectedTodoList.title = res.title;
            const index = this.todoLists.findIndex((p) => p.id == res.id);
            this.todoLists.fill(res, index, index + 1);
            this.isVisible = false;
            this.validateForm.reset();
          },
        });
    }
  }

  onKeyDown(event: any) {
    const title = this.todoItemTitle;
    this.todoItemTitle = '';
    if (title.length > 0) {
      const todoItemRequest: CreateTodoItemRequest = {
        listId: this.selectedTodoList.id,
        title: title,
      };
      this.todoItemService.create(todoItemRequest).subscribe({
        next: (res) => this.todoItems.push(res),
      });
    }
  }

  changeCompletedStatus(item: TodoItem) {
    this.todoItemService.updateCompletedStatus(item.id).subscribe({
      next: (res) => {
        item.completedDate = res.completedDate;
        item.completed = res.completed;
      },
    });
  }

  changePriorityStatus(item: TodoItem) {
    this.todoItemService.updatePriorityStatus(item.id).subscribe({
      next: (res) => {
        item.priority = res.priority;
      },
    });
  }

  contextMenu($event: MouseEvent, menu: NzDropdownMenuComponent): void {
    this.nzContextMenuService.create($event, menu);
  }

  closeMenu(): void {
    this.nzContextMenuService.close();
  }

  edit(item: TodoItem) {
    this.selectedItem = item;
    this.validateItemForm.patchValue({
      title: this.selectedItem.title,
      reminder: this.selectedItem.reminder,
      notes: this.selectedItem.notes,
    });
  }

  updateItem() {
    if (!this.validateItemForm.valid) {
      Object.values(this.validateItemForm.controls).forEach((control) => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      });
      return;
    }

    const request: UpdateTodoItemRequest = this.validateItemForm
      .value as UpdateTodoItemRequest;

    this.todoItemService.update(this.selectedItem.id, request).subscribe({
      next: (res) => {
        const index = this.todoItems.findIndex((p) => p.id == res.id);
        this.todoItems.fill(res, index, index + 1);
        this.validateItemForm.reset();
        this.selectedItem = {} as TodoItem;
      },
    });
  }

  delete(item: TodoItem) {
    this.todoItemService.delete(item.id).subscribe({
      next: () => {
        const index = this.todoItems.findIndex((p) => p.id == item.id);
        this.todoItems.splice(index, 1);
      },
    });
  }

  close() {
    this.selectedItem = {} as TodoItem;
  }
}

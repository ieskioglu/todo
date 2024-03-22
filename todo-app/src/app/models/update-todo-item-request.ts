export interface UpdateTodoItemRequest {
  title: string;
  notes: string | null;
  reminder: Date | null;
}

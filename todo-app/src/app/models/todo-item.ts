export interface TodoItem {
  id: string;
  listId: string;
  title: string;
  notes: string | null;
  priority: boolean;
  reminder: Date | null;
  completed: boolean;
  completedDate: Date | null;
}

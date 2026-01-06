export function formatDateForInput(dateStr: string | Date): string {
  if (!dateStr) return '';
  const date = new Date(dateStr);

  const offset = date.getTimezoneOffset() * 60000;
  const localDate = new Date(date.getTime() - offset);
  return localDate.toISOString().split('T')[0];
}

export enum EventStatus {
  SCHEDULED = 'SCHEDULED',
  REGISTRATION_OPEN = 'REGISTRATION_OPEN',
  REGISTRATION_CLOSED = 'REGISTRATION_CLOSED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export function readableEventStatus(status: EventStatus): string {
  switch (status) {
    case EventStatus.SCHEDULED:
      return 'Programmato';
    case EventStatus.REGISTRATION_OPEN:
      return 'Iscrizioni Aperte';
    case EventStatus.REGISTRATION_CLOSED:
      return 'Iscrizioni Chiuse';
    case EventStatus.COMPLETED:
      return 'Completato';
    case EventStatus.CANCELLED:
      return 'Annullato';
    default:
      return 'Sconosciuto';
  }
}

export function eventStatusColorClass(status: EventStatus): string {
  switch (status) {
    case EventStatus.SCHEDULED:
      return 'bg-blue-100 text-blue-600 border-blue-200';
    case EventStatus.REGISTRATION_OPEN:
      return 'bg-green-100 text-green-700 border-green-200 ring-green-500/20';
    case EventStatus.REGISTRATION_CLOSED:
      return 'bg-amber-100 text-amber-800 border-amber-200';
    case EventStatus.COMPLETED:
      return 'bg-slate-100 text-slate-500 border-slate-200';
    case EventStatus.CANCELLED:
      return 'bg-red-100 text-red-600 border-red-200 line-through decoration-red-400';
    default:
      return 'bg-gray-100 text-gray-700';
  }
}

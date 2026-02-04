import {
  faBan,
  faCheckCircle,
  faClock,
  faExclamationCircle,
  IconDefinition
} from '@fortawesome/free-solid-svg-icons';

export enum AffiliationStatus {
  SUBMITTED = 'SUBMITTED',
  ACCEPTED = 'ACCEPTED',
  REJECTED = 'REJECTED',
  EXPIRED = 'EXPIRED',
}

export function readableAffiliationStatus(status: AffiliationStatus): string {
  switch (status) {
    case AffiliationStatus.SUBMITTED:
      return 'Inviata';
    case AffiliationStatus.ACCEPTED:
      return 'Attiva';
    case AffiliationStatus.REJECTED:
      return 'Respinta';
    case AffiliationStatus.EXPIRED:
      return 'Scaduta';
    default:
      return 'Sconosciuto';
  }
}

export function affiliationStatusColorClass(status: AffiliationStatus): string {
  switch (status) {
    case AffiliationStatus.SUBMITTED:
      return 'bg-amber-100 text-amber-800 border-amber-200';
    case AffiliationStatus.ACCEPTED:
      return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    case AffiliationStatus.REJECTED:
    case AffiliationStatus.EXPIRED:
      return 'bg-red-100 text-red-600 border-red-200';
    default:
      return 'bg-gray-100 text-gray-700';
  }
}

export function statusIcon(status: AffiliationStatus): IconDefinition {
  switch (status) {
    case AffiliationStatus.SUBMITTED:
      return faClock;
    case AffiliationStatus.ACCEPTED:
      return faCheckCircle;
    case AffiliationStatus.REJECTED:
      return faBan;
    case AffiliationStatus.EXPIRED:
      return faExclamationCircle;
    default:
      return faExclamationCircle;
  }
}

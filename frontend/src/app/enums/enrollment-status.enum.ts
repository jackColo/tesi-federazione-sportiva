export enum EnrollmentStatus {
    DRAFT = "DRAFT",
    SUBMITTED = "SUBMITTED",
    APPROVED = "APPROVED",
    REJECTED = "REJECTED",
    RETIRED = "RETIRED",
}


export function readableEnrollmentStatus(status: EnrollmentStatus): string {
  switch (status) {
    case EnrollmentStatus.SUBMITTED:
      return 'Richiesta inviata';
    case  EnrollmentStatus.APPROVED:
      return 'Iscrizione accettata';
    case  EnrollmentStatus.REJECTED:
      return 'Iscrizione respinta';
    case  EnrollmentStatus.RETIRED:
      return 'Ritirato';
    case  EnrollmentStatus.DRAFT:
      return 'Bozza';
    default:
      return 'Sconosciuto';
  }
}

export function enrollmentStatusColorClass(status: EnrollmentStatus): string {
  switch (status) {
    case  EnrollmentStatus.SUBMITTED:
      return 'bg-amber-100 text-amber-800 border-amber-200';
    case  EnrollmentStatus.APPROVED:
      return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    case  EnrollmentStatus.REJECTED:
    case  EnrollmentStatus.RETIRED:
      return 'bg-red-100 text-red-600 border-red-200';
    default:
      return 'bg-gray-100 text-gray-700';
  }
}

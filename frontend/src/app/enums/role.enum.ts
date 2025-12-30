export enum Role {
    ATHLETE = "ATHLETE",
    CLUB_MANAGER = "CLUB_MANAGER",
    FEDERATION_MANAGER = "FEDERATION_MANAGER"
}

export function readableRole(role: Role): string {
    switch (role) {
        case Role.ATHLETE:
            return "Atleta";
        case Role.CLUB_MANAGER:
            return "Responsabile Club";
        case Role.FEDERATION_MANAGER:
            return "Amministratore";
        default:
            return "Utente";
    }
}

export function roleClass(role: Role): string {
    switch (role) {
        case Role.ATHLETE:
            return 'bg-green-100 text-green-700 border-green-200';
        case Role.CLUB_MANAGER:
            return 'bg-blue-100 text-blue-700 border-blue-200';
        case Role.FEDERATION_MANAGER:
            return 'bg-purple-100 text-purple-700 border-purple-200';
        default:
            return 'bg-gray-100 text-gray-700 border-gray-200';
    }
}

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

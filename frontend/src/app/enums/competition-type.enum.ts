export enum CompetitionType {
    KICK_BOXING = "KICK_BOXING",
    K1 = "K1",
    BOXE = "BOXE",
    MMA = "MMA",
    GRAPPLING = "GRAPPLING",
}


export function readableCompetitionType(type: CompetitionType): string {
  switch (type) {
    case CompetitionType.K1:
      return 'K1';
    case CompetitionType.KICK_BOXING:
      return 'Kick boxing';
    case CompetitionType.BOXE:
      return 'Boxe';
    case CompetitionType.MMA:
      return 'Mixed martial arts';
    case CompetitionType.GRAPPLING:
      return 'Grappling';
    default:
      return 'Sconosciuto';
  }
}
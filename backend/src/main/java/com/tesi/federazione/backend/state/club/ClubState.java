package com.tesi.federazione.backend.state.club;

import com.tesi.federazione.backend.model.Club;

public interface ClubState {
    void next(Club club);
    void expire(Club club);
    boolean canOperate();
}

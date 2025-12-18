package com.tesi.federazione.backend.state.athlete;

import com.tesi.federazione.backend.model.Athlete;

public interface AthleteState {
    void next(Athlete athlete);
    void expire(Athlete athlete);

    boolean canOperate();
}

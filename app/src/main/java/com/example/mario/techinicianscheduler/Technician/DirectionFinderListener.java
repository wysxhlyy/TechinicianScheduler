package com.example.mario.techinicianscheduler.Technician;

import java.util.List;

/**
 * A listener for the DirectionFinder.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<DirectionFinder.Route> route);
}

package com.example.mario.techinicianscheduler.Technician;

import java.util.List;

/**
 * Created by mario on 2017/1/31.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<DirectionFinder.Route> route);
}

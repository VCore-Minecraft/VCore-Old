package de.verdox.vcore.performance.efficiency;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {

    private List<Long> timeStamps = new ArrayList<>();

    /**
     * Returns time since last log
     * @return
     */
    public long logTime(){
        long timeStamp = System.currentTimeMillis();
        long lastTimeStamp = getLastTimeStamp();
        timeStamps.add(timeStamp);
        return timeStamp - lastTimeStamp;
    }

    public long getLastTimeStamp(){
        if(timeStamps.size() == 0)
            return System.currentTimeMillis();
        else
            return timeStamps.get(timeStamps.size()-1);
    }

}

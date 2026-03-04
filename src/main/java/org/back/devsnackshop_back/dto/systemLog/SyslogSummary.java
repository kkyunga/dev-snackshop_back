package org.back.devsnackshop_back.dto.systemLog;

import java.util.List;
import java.util.Map;

public class SyslogSummary {
    private long                          oomCount;
    private long                          diskFullCount;
    private long                          crashCount;
    private List<Map.Entry<String, Long>> topErrorServices;
    private List<String> criticalLines;

    public SyslogSummary(long oomCount, long diskFullCount, long crashCount,
                         List<Map.Entry<String, Long>> topErrorServices,
                         List<String> criticalLines) {
        this.oomCount         = oomCount;
        this.diskFullCount    = diskFullCount;
        this.crashCount       = crashCount;
        this.topErrorServices = topErrorServices;
        this.criticalLines    = criticalLines;
    }

    public long                          getOomCount()         { return oomCount; }
    public long                          getDiskFullCount()    { return diskFullCount; }
    public long                          getCrashCount()       { return crashCount; }
    public List<Map.Entry<String, Long>> getTopErrorServices() { return topErrorServices; }
    public List<String>                  getCriticalLines()    { return criticalLines; }
}

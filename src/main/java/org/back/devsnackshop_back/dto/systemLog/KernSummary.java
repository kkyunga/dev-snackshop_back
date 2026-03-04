package org.back.devsnackshop_back.dto.systemLog;

import java.util.List;

public class KernSummary {
    private long         ioError;
    private long         hwError;
    private long         segfault;
    private long         kernelPanic;
    private List<String> criticalLines;

    public KernSummary(long ioError, long hwError, long segfault,
                       long kernelPanic, List<String> criticalLines) {
        this.ioError       = ioError;
        this.hwError       = hwError;
        this.segfault      = segfault;
        this.kernelPanic   = kernelPanic;
        this.criticalLines = criticalLines;
    }

    public long         getIoError()       { return ioError; }
    public long         getHwError()       { return hwError; }
    public long         getSegfault()      { return segfault; }
    public long         getKernelPanic()   { return kernelPanic; }
    public List<String> getCriticalLines() { return criticalLines; }
}

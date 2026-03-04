package org.back.devsnackshop_back.dto.systemLog;

public class FullLogReport {
    private String        serverHost;
    private SyslogSummary syslog;
    private AuthSummary   auth;
    private KernSummary   kernel;

    public FullLogReport(String serverHost, SyslogSummary syslog,
                         AuthSummary auth, KernSummary kernel) {
        this.serverHost = serverHost;
        this.syslog     = syslog;
        this.auth       = auth;
        this.kernel     = kernel;
    }

    public String        getServerHost() { return serverHost; }
    public SyslogSummary getSyslog()     { return syslog; }
    public AuthSummary   getAuth()       { return auth; }
    public KernSummary   getKernel()     { return kernel; }
}

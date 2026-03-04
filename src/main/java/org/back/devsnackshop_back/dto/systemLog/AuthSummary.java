package org.back.devsnackshop_back.dto.systemLog;

import java.util.List;

public class AuthSummary {
    private long         loginFailed;
    private long         loginSuccess;
    private long         sudoCount;
    private long         newUserCount;
    private List<String> suspiciousIps;

    public AuthSummary(long loginFailed, long loginSuccess, long sudoCount,
                       long newUserCount, List<String> suspiciousIps) {
        this.loginFailed   = loginFailed;
        this.loginSuccess  = loginSuccess;
        this.sudoCount     = sudoCount;
        this.newUserCount  = newUserCount;
        this.suspiciousIps = suspiciousIps;
    }

    public long         getLoginFailed()         { return loginFailed; }
    public long         getLoginSuccess()        { return loginSuccess; }
    public long         getSudoCount()           { return sudoCount; }
    public long         getNewUserCount()        { return newUserCount; }
    public List<String> getSuspiciousIps()       { return suspiciousIps; }
}

package org.back.devsnackshop_back.dto.systemLog;

public class ServerConnection {
    private String host;
    private int    port;
    private String user;

    // 인증 방식 구분
    private String password;      // 비밀번호 방식
    private String pemKeyPath;    // pem 파일 경로 방식
    private byte[] pemKeyBytes;   // pem 파일 업로드 방식 (파일 내용 직접)

    public enum AuthType { PASSWORD, PEM_PATH, PEM_BYTES }
    private AuthType authType;

    // 생성자 - 비밀번호
    public static ServerConnection withPassword(String host, int port, String user, String password) {
        ServerConnection conn = new ServerConnection(host, port, user);
        conn.password = password;
        conn.authType = AuthType.PASSWORD;
        return conn;
    }

    // 생성자 - pem 파일 경로
    public static ServerConnection withPemPath(String host, int port, String user, String pemKeyPath) {
        ServerConnection conn = new ServerConnection(host, port, user);
        conn.pemKeyPath = pemKeyPath;
        conn.authType   = AuthType.PEM_PATH;
        return conn;
    }

    // 생성자 - pem 파일 바이트 (프론트에서 파일 업로드한 경우)
    public static ServerConnection withPemBytes(String host, int port, String user, byte[] pemKeyBytes) {
        ServerConnection conn = new ServerConnection(host, port, user);
        conn.pemKeyBytes = pemKeyBytes;
        conn.authType    = AuthType.PEM_BYTES;
        return conn;
    }

    private ServerConnection(String host, int port, String user) {
        this.host = host;
        this.port = port;
        this.user = user;
    }

    public String   getHost()        { return host; }
    public int      getPort()        { return port; }
    public String   getUser()        { return user; }
    public String   getPassword()    { return password; }
    public String   getPemKeyPath()  { return pemKeyPath; }
    public byte[]   getPemKeyBytes() { return pemKeyBytes; }
    public AuthType getAuthType()    { return authType; }

}

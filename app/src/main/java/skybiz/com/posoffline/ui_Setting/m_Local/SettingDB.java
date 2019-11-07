package skybiz.com.posoffline.ui_Setting.m_Local;

/**
 * Created by 7 on 30/10/2017.
 */

public class SettingDB {
    private int RunNo;
    private String ServerName, UserName, Password, DBName, Port, ConnYN;

    public SettingDB(int runNo, String serverName, String userName, String password, String DBName, String port, String connYN) {
        RunNo = runNo;
        ServerName = serverName;
        UserName = userName;
        Password = password;
        this.DBName = DBName;
        Port = port;
        ConnYN = connYN;
    }

    public int getRunNo() {
        return RunNo;
    }

    public void setRunNo(int runNo) {
        RunNo = runNo;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDBName() {
        return DBName;
    }

    public void setDBName(String DBName) {
        this.DBName = DBName;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String port) {
        Port = port;
    }

    public String getConnYN() {
        return ConnYN;
    }

    public void setConnYN(String connYN) {
        ConnYN = connYN;
    }
}

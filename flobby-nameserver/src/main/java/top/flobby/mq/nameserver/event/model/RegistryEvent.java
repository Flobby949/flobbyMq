package top.flobby.mq.nameserver.event.model;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 注册事件，首次连接nameserver使用
 * @create : 2025-04-30 11:22
 **/

public class RegistryEvent extends Event{
    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

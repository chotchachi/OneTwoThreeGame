package Model;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public Socket socket = null;
    public String userName;
    public ObjectOutputStream dout = null;

    public Client(Socket sk, String userName) {
        this.socket = sk;
        this.userName = userName;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ObjectOutputStream getDout() {
        return dout;
    }

    public void setDout(ObjectOutputStream dout) {
        this.dout = dout;
    }
    
    
}

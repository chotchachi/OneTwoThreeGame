package chat;

import Model.User;
import UI.app_main_ui;
import UI.call_notify_ui;
import UI.call_ui;
import UI.invite_ui;
import UI.login_ui;
import UI.one_two_three;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatClient {

    public static int port = 8888;
    public static String ipServer = "192.168.1.6";

    public static String myIP = "10.211.55.3";
    public static int myPort = 50000;

    public static Socket socket = null;
    public static boolean login = false;

    public static User isMe;
    public static User nguoiNhan;

    public static String myUserName;
    public static String myRivalUserName;

    public static boolean connected = false;

    public static app_main_ui app_main_ui = null;
    public static login_ui login_ui = null;
    public static invite_ui inviteUI = null;
    public static one_two_three OTT = null;
    public static List<User> userList = new ArrayList<>();
    public static boolean playing = false;

    public static call_ui callUI = null;

    //port cho server con nhận dữ liệu từ python
    //phải trùng với port trên python
    //khác port server chính
    public static final int myServerPort = 8000;

    public static void main(String[] args) throws IOException {
        app_main_ui = new app_main_ui();
        login_ui = new login_ui();
        OTT = new one_two_three();
        login_ui.setVisible(true);

        callUI = new call_ui();
 
        ChatClient.socket = new Socket(ChatClient.ipServer, ChatClient.port);
        Thread receive = new Receive(ChatClient.socket);
        receive.start();

        connected = true;
        System.out.println("Kết nối đến Server thành công");

        //start thread server con
//        Thread myServer = new MyServer(myServerPort);
//        myServer.start();
    }
}

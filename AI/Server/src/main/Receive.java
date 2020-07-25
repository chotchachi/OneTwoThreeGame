package main;

import Model.DataSocket;
import Model.Client;
import Model.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receive extends Thread {

    private Socket socket = null;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream = null;
    private String currentUser = "";
    
    public Receive(Socket socket) {
        this.socket = socket;
        try {
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void run() {
        DataSocket msg = null;
        try {
            while (true) {
                inputStream = new ObjectInputStream(socket.getInputStream());
                msg = (DataSocket) inputStream.readObject();
                System.out.println("Server: User " + msg.action);
                switch (msg.action) {
                    case "login":
                        login(msg.data[0]);
                        break;
                    case "loaduser":
                    case "updateUser":
                        loadUser();
                        break;
                    case "request_call":
                        requestCall(msg);
                        break;
                    case "respon_call":
                        responCall(msg);
                        break;
                    case "sendfinger":
                        sendFinger(msg);
                        break;
                    default:
                        System.out.println("Unknow Action");
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("User disconnect 1");
        } catch (SQLException ex) {
            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
                System.out.println("User disconnect");
                removeUser();
            } catch (IOException | SQLException ex) {
                Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void login(String username) {
        DataSocket dtsk = new DataSocket();
        this.currentUser = username;
        
        try {
            System.out.println("New User Login: " + username);

            addClient(username);
            
            //them vao db
            Statement statement = ChatServer.connect.createStatement();
            String sql = "Insert into user(username) values ('" + username + "') ";
            statement.executeUpdate(sql);

            dtsk.action = "loginok";

            outputStream.writeObject(dtsk);
            outputStream.flush();

        } catch (SQLException | IOException ex) {
            Logger.getLogger(Receive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addClient(String userName){
        Client client = new Client(socket, userName);
        client.dout = outputStream;
        ChatServer.listClient.add(client);
    }
    
    public void loadUser() throws SQLException, IOException {
        DataSocket dtsk = new DataSocket();

        List<User> userList = new ArrayList<>();

        Statement statement = ChatServer.connect.createStatement();
        
        //lay all user
        String sql2 = "Select * from user";
        ResultSet rs = statement.executeQuery(sql2);
        while (rs.next()) {
            int id = rs.getInt("id");
            String userName = rs.getString("username");
            userList.add(new User(userName));
        }
        
        dtsk.action = "loaduser";
        dtsk.setUserList(userList);

        outputStream.writeObject(dtsk);
        outputStream.flush();
    }
    
    private void removeUser() throws SQLException {
        Statement statement = ChatServer.connect.createStatement();
        String sql = "DELETE FROM user WHERE username = '"+currentUser+"'";
        statement.execute(sql);
    }
    
    public void responCall(DataSocket data) {
        for (Client client : ChatServer.listClient) {
            if (client.getUserName().equals(data.data[1])) {
                try {
                    client.dout.writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    public void requestCall(DataSocket data) {
        for (Client client : ChatServer.listClient) {
            if (client.getUserName().equals(data.data[1])) { //tim nguoi nhan
                try {
                    client.dout.writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                break;
            }
        }
    }

    private void sendFinger(DataSocket data) {
        System.out.println(data.data[0]+" finger is: "+data.data[2] + " VS " + data.data[1]);
        for (Client client : ChatServer.listClient) {
            if (client.getUserName().equals(data.data[1])) { //tim nguoi nhan
                try {
                    client.dout.writeObject(data);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                break;
            }
        }
    }
}

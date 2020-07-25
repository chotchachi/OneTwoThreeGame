package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSocket implements Serializable{
    public String action = null;
    public String[] data;
    public ArrayList<String[]> data_arr;
    public User user;
    public List<User> userList;
    public boolean accept = false;
    
    public DataSocket(){
        
    }
    
    public DataSocket(String action,String[] data, ArrayList<String[]> data_arr) {
        this.action = action;
        this.data = data;
        this.data_arr = data_arr;
    }   

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public ArrayList<String[]> getData_arr() {
        return data_arr;
    }

    public void setData_arr(ArrayList<String[]> data_arr) {
        this.data_arr = data_arr;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}

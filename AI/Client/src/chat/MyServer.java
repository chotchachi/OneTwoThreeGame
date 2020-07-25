/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thanh
 */
public class MyServer extends Thread {
    private final int myServerPort;
    public static String fingerValue;
    
    public MyServer(int myServerPort) {
        this.myServerPort = myServerPort;
    }

    @Override
    public void run() {
        try {
            //Tạo 1 server con để nhận dữ liệu từ python
            DatagramSocket myServer = new DatagramSocket(myServerPort);

            System.out.println("Server con đang chạy tại port: " + myServerPort);

            byte[] inServer = new byte[1024];

            //tạo packet nhận dữ liệu
            DatagramPacket rcvPkt = new DatagramPacket(inServer, inServer.length);

            while (true) {
                // chờ nhận dữ liệu từ client
                myServer.receive(rcvPkt);

                System.out.println("IP Address: " + rcvPkt.getAddress() + " port:" + rcvPkt.getPort());

                fingerValue = new String(rcvPkt.getData());
                
                System.out.println("Finger Value: " + fingerValue);
                
                ChatClient.OTT.txt_fingerValue.append(fingerValue+"\n");
            }
        } catch (SocketException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

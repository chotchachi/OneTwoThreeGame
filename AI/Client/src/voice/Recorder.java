package voice;

import chat.ChatClient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.TargetDataLine;

public class Recorder extends Thread{
        public TargetDataLine audio_in = null;
        public DatagramSocket dout;
        byte byte_buff[] = new byte[512];
        public InetAddress fr_ip;
        public int fr_port;
        @Override
        public void run(){
                Long pack = 0l;
                while(ChatClient.playing){
                    try {
                        int read = audio_in.read(byte_buff, 0, byte_buff.length);
                        DatagramPacket data = new DatagramPacket(byte_buff, byte_buff.length, fr_ip, fr_port);
                        System.out.println("send: #"+ pack++);
                        dout.send(data);
                    } catch (IOException ex) {
                        Logger.getLogger(Recorder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
               
                System.out.println("call in recorder: recorder is stop");
                audio_in.drain();
                audio_in.close();
                
                System.out.println("call in recorder: audio is drain and close");
            
        }
}

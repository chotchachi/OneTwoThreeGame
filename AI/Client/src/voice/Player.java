package voice;

import chat.ChatClient;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.SourceDataLine;

public class Player extends Thread{
    public DatagramSocket din;
    public SourceDataLine audio_out;
    byte[] buffer = new byte[512];
        @Override
        public void run(){
        Long pack = 0l;
        try {
            
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

            System.out.println("Server socket created. Waiting for incoming data...");
            while(ChatClient.playing){
                din.receive(incoming);
                buffer = incoming.getData();
                audio_out.write(buffer, 0, buffer.length);
                System.out.println("receive: #"+ pack++);
            }
            System.out.println("call in player: player is stop");
            audio_out.drain();
            audio_out.close();
            System.out.println("call in player: audio is drain and close");
        } catch (SocketException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
}

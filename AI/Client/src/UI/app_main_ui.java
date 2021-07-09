package UI;

import Model.DataSocket;
import chat.ChatClient;
import Model.User;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

public class app_main_ui extends javax.swing.JFrame {

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    public Clip clip;
    public String[] data_from_server;

    public app_main_ui() {
        initComponents();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width - this.getSize().width - 50, 50);
        list_user.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        list_user = new javax.swing.JList<>();
        btn_update = new javax.swing.JButton();

        jMenuItem1.setText("jMenuItem1");
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText("jMenuItem2");
        jPopupMenu1.add(jMenuItem2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));
        setLocation(new java.awt.Point(0, 0));
        setResizable(false);
        setSize(new java.awt.Dimension(500, 780));

        list_user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                list_userMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(list_user);

        btn_update.setText("Cập nhật người chơi");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_update)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 661, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addComponent(btn_update)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void list_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_list_userMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            this.call(list_user.getSelectedIndex());
        }
    }//GEN-LAST:event_list_userMouseClicked

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        try {
            OutputStream outputStream = ChatClient.socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            DataSocket dtsk = new DataSocket();
            String[] data = new String[2];
            dtsk.action = "updateUser";
            dtsk.data = data;

            objectOutputStream.writeObject(dtsk);
        } catch (IOException ex) {
            Logger.getLogger(app_main_ui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_updateActionPerformed

    public void play(int index) {
        User user = ChatClient.userList.get(index);
        ChatClient.myRivalUserName = user.getUserName();

        sendLoiMoi("request_game");
    }

    public void sendLoiMoi(String mode) {
        //request_call
        ChatClient.playing = true;
        ChatClient.inviteUI = new invite_ui();
        //ChatClient.inviteUI.init_audio();
        //ChatClient.inviteUI.init_player();

        int x, y;
        x = this.getLocation().x + this.getWidth();
        y = this.getLocation().y;
        if (x + ChatClient.inviteUI.getWidth() > dim.width) {
            x = this.getLocation().x - ChatClient.inviteUI.getWidth();
        }
        ChatClient.inviteUI.setLocation(x, y);
        ChatClient.inviteUI.setVisible(true);

        DataSocket dtsk = new DataSocket();
        dtsk.action = mode;
        dtsk.accept = true;
        String data[] = new String[4];
        data[0] = String.valueOf(ChatClient.myUserName);
        data[1] = String.valueOf(ChatClient.myRivalUserName);
        data[2] = ChatClient.inviteUI.my_ip;
        data[3] = String.valueOf(ChatClient.inviteUI.my_port);
        dtsk.data = data;
        ObjectOutputStream dout;
        try {
            dout = new ObjectOutputStream(ChatClient.socket.getOutputStream());
            dout.writeObject(dtsk);
        } catch (IOException ex) {
            Logger.getLogger(app_main_ui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showGameNotify(String rivalUserName) {
        System.out.println("Showcallnotify");
        notify_ui notifyUI = new notify_ui();
        notifyUI.setTitle(rivalUserName);
        notifyUI.txt_tennguoigoi.setText(rivalUserName + " mời bạn chơi cùng");
        notifyUI.setLocation(dim.width / 2 - notifyUI.getWidth() / 2, dim.height / 2 - notifyUI.getHeight() / 2);
        notifyUI.setVisible(true);
        this.clip = this.playsound("ringer.wav");
        this.clip.start();
    }

    public Clip playsound(String path) {
        String file_path = "sound/" + path;
        try {
            URL yourFile = getClass().getClassLoader().getResource(file_path);
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;

            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            Logger.getLogger(app_main_ui.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public void deniedPlay() {
        DataSocket dtsk = new DataSocket();
        dtsk.action = "respon_game";
        String[] data = new String[3];
        data[0] = String.valueOf(ChatClient.myUserName);
        data[1] = String.valueOf(ChatClient.myRivalUserName);
        data[2] = "Từ chối";
        dtsk.data = data;
        dtsk.accept = false;
        ObjectOutputStream dout;
        try {
            dout = new ObjectOutputStream(ChatClient.socket.getOutputStream());
            dout.writeObject(dtsk);
            System.out.println("Đã gửi phản hồi: từ chối");
        } catch (IOException ex) {
            Logger.getLogger(app_main_ui.class.getName()).log(Level.SEVERE, null, ex);
        }

        ChatClient.myRivalUserName = "";
    }

    public void acceptPlay(String mode) {
        DataSocket dtsk = new DataSocket();
        dtsk.action = mode;
        dtsk.accept = true;
        String data[] = new String[2];
        data[0] = String.valueOf(ChatClient.myUserName);
        data[1] = String.valueOf(ChatClient.myRivalUserName);
        //data[2] = ChatClient.inviteUI.my_ip;
        //data[3] = String.valueOf(ChatClient.inviteUI.my_port);
        dtsk.data = data;
        ObjectOutputStream dout;
        try {
            dout = new ObjectOutputStream(ChatClient.socket.getOutputStream());
            dout.writeObject(dtsk);
        } catch (IOException ex) {
            Logger.getLogger(app_main_ui.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (mode.equalsIgnoreCase("respon_game")) {
            if (ChatClient.OTT != null) {
                ChatClient.OTT.setVisible(true);
                ChatClient.OTT.startCount();
            }
        }
    }

    public void call(int index) {
        User user = ChatClient.userList.get(index);
        ChatClient.myRivalUserName = user.getUserName();
        ChatClient.nguoiNhan = user;

        init_call("request_call");
    }

    public void init_call(String mode) {
        ChatClient.callUI.init_audio();
        ChatClient.callUI.init_player();

        int x, y;
        x = this.getLocation().x + this.getWidth();
        y = this.getLocation().y;
        if (x + ChatClient.callUI.getWidth() > dim.width) {
            x = this.getLocation().x - ChatClient.callUI.getWidth();
        }
        ChatClient.callUI.setLocation(x, y);
        ChatClient.callUI.setVisible(true);

        DataSocket dtsk = new DataSocket();
        dtsk.setAction(mode);
        dtsk.setAccept(true);
        dtsk.setNguoiGui(ChatClient.isMe);
        dtsk.setNguoiNhan(ChatClient.nguoiNhan);
        String data[] = new String[2];
        data[0] = ChatClient.myIP;
        data[1] = String.valueOf(ChatClient.myPort);
        dtsk.setData(data);
        ObjectOutputStream dout;
        try {
            dout = new ObjectOutputStream(ChatClient.socket.getOutputStream());
            dout.writeObject(dtsk);
        } catch (IOException ex) {
            Logger.getLogger(app_main_ui.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (mode.equals("respon_call")) {
            try {
                // nếu chấp chận thì khởi tạo phần thu âm
                ChatClient.callUI.init_recorder(InetAddress.getByName(this.data_from_server[0]), Integer.valueOf(this.data_from_server[1]));
                ChatClient.callUI.lb_status.setText("Đã kết nối");
            } catch (UnknownHostException ex) {
                Logger.getLogger(app_main_ui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void showCallNotify(String rivalUserName) {
        call_notify_ui notifyUI = new call_notify_ui();
        notifyUI.setTitle(rivalUserName);
        notifyUI.txt_tennguoigoi.setText(rivalUserName);
        notifyUI.setLocation(dim.width / 2 - notifyUI.getWidth() / 2, dim.height / 2 - notifyUI.getHeight() / 2);
        notifyUI.setVisible(true);
        this.clip = this.playsound("ringer.wav");
        this.clip.start();
    }

    public void tuchoi() {
        DataSocket dtsk = new DataSocket();
        dtsk.setAction("respon_call");
        dtsk.setNguoiGui(ChatClient.isMe);
        dtsk.setNguoiNhan(ChatClient.nguoiNhan);
        dtsk.setAccept(false);
        try {
            ObjectOutputStream dout = new ObjectOutputStream(ChatClient.socket.getOutputStream());
            dout.writeObject(dtsk);
            System.out.println("Đã gửi phản hồi: từ chối");
        } catch (IOException ex) {

        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(app_main_ui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new app_main_ui().setVisible(true);
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_update;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JList<User> list_user;
    // End of variables declaration//GEN-END:variables

}

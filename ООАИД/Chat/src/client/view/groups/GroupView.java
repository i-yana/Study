package client.view.groups;

import client.model.listeners.ClientGroupListener;
import client.view.listeners.GroupViewListener;
import client.view.observers.ObservableGroupView;
import protocol.info.UserInfo;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 13.12.15.
 */
public class GroupView extends JPanel implements ObservableGroupView, ClientGroupListener {

    private javax.swing.JButton sendButton;
    private javax.swing.JButton leaveButton;
    private javax.swing.JList<String> usersList;
    private javax.swing.JPanel jPanel1;

    private javax.swing.JPanel jPanel3;


    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;

    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private DefaultListModel<String> usersInGroup;
    private String groupName;
    private List<GroupViewListener> listeners = new ArrayList<>();
    private JTabbedPane tabbedPane;

    public GroupView(String groupName, JTabbedPane tabbedPane) {
        this.groupName = groupName;
        this.tabbedPane = tabbedPane;
        initComponents();
    }

    private void initComponents() {
        jPanel1 = this;
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        usersList = new javax.swing.JList<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();
        leaveButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        DefaultCaret caret = (DefaultCaret)jTextArea2.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        jPanel3.setBackground(new java.awt.Color(204, 204, 0));

        usersList.setBackground(new java.awt.Color(204, 255, 204));
        usersInGroup = new DefaultListModel<>();
        usersList.setModel(usersInGroup);
        jScrollPane3.setViewportView(usersList);
        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
        );

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setEditable(false);
        jScrollPane1.setViewportView(jTextArea2);
        sendButton.setText("SEND");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyAboutSendMessage(groupName, jTextArea1.getText());
            }
        });
        leaveButton.setBackground(new java.awt.Color(255, 0, 0));
        leaveButton.setText("LEAVE");
        leaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notifyAboutLeaveFromGroup(groupName);
                int index = tabbedPane.getSelectedIndex();
                tabbedPane.removeTabAt(index);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER) && e.isShiftDown()) {
                    notifyAboutSendMessage(groupName, jTextArea1.getText());
                    jTextArea1.setText("");
                    jTextArea1.requestFocus();
                }

            }
        });
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jScrollPane1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(leaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                                                .addGap(0, 15, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(sendButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(leaveButton))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    public void notifyAboutSendMessage(String groupName, String text) {
        for(GroupViewListener groupViewListener: listeners){
            groupViewListener.handleMessageFromClient(text, groupName, Color.BLACK);
        }
    }


    @Override
    public void notifyAboutLeaveFromGroup(String groupName) {
        for(GroupViewListener groupViewListener: listeners){
            groupViewListener.handleLeave(groupName);
        }
    }



    @Override
    public void subscribeToChange(GroupViewListener listener) {
        listeners.add(listener);
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public void handleUpdateParticipants(String groupName, List<UserInfo> participants) {
        if(this.groupName.equals(groupName)){
            usersInGroup.clear();
            System.out.println("LIST");
            for(UserInfo userInfo:participants){
                System.out.println(userInfo.getLogin());
                usersInGroup.addElement(userInfo.getLogin());
            }
        }
    }

    @Override
    public void handleMessageFromServer(String groupName, String message) {
        if(this.groupName.equals(groupName)){
            jTextArea2.append("\n"+message);
        }
    }
}

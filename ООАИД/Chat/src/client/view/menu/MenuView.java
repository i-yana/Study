package client.view.menu;

import client.model.listeners.ClientMenuListener;
import client.view.groups.GroupSettingView;
import client.view.groups.GroupView;
import client.view.listeners.MenuViewListener;
import client.view.observers.ObservableMenuView;
import protocol.info.Group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yana on 03.12.15.
 */
public class MenuView extends JFrame implements ObservableMenuView, ClientMenuListener {

    private List<MenuViewListener> listeners = new ArrayList<>();
    private final HashMap<String, GroupView> groups = new HashMap<>();
    public MenuView() {
        initComponents();
    }

    private void createSettingWindow(){
        GroupSettingView settingView = new GroupSettingView(this);
        this.setEnabled(false);
        settingView.setVisible(true);
    }

    private void initComponents() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                notifyAboutLogoutCommand();
                super.windowClosing(e);
            }

        });
        setResizable(false);
        placeForGroupWindow = new JTabbedPane();
        jScrollPane4 = new JScrollPane();
        jList4 = new JList<>();
        jButton4 = new JButton();
        jButton5 = new JButton();
        jButton6 = new JButton();
        jTextField1 = new JTextField();

        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(0, 153, 204));

        placeForGroupWindow.setBackground(new Color(153, 153, 255));



        jList4.setBackground(new Color(255, 255, 102));
        jList4.setFont(new Font("HanziPen SC", 0, 18));
        groupList = new DefaultListModel<>();
        jList4.setModel(groupList);
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedItem = jList4.getSelectedValue();
                    if(groups.containsKey(selectedItem)){
                        return;
                    }
                    notifyAboutJoinToGroupRequest(selectedItem);
                }
            }
        };
        jList4.addMouseListener(mouseListener);
        jList4.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        jList4.setName("Groups");
        jList4.setVisibleRowCount(13);
        jScrollPane4.setViewportView(jList4);

        jButton4.setText("Create public group");
        jButton4.setActionCommand("Create public group");
        jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                createSettingWindow();
            }
        });

        jButton5.setText("Create private group");
        jButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

            }
        });

        jButton6.setText("Friend list");
        jButton6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

            }
        });

        jTextField1.setText("");
        jTextField1.setBorder(BorderFactory.createEtchedBorder());
        jTextField1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String seq = cz.getText();
                if(seq.isEmpty()){
                    jList4.setModel(groupList);
                    return;
                }
                DefaultListModel<String> searchList = new DefaultListModel<String>();
                for(int i = 0; i< groupList.size(); i++){
                    if(groupList.getElementAt(i).length()>seq.length()){
                        if (groupList.getElementAt(i).substring(0, seq.length()).equals(seq)) {
                            searchList.addElement(groupList.getElementAt(i));
                        }
                    }
                }
                jList4.setModel(searchList);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(placeForGroupWindow, GroupLayout.PREFERRED_SIZE, 610, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(23, 23, 23)
                                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(jButton6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jButton4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jButton5, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)))
                                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane4, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE)))
                                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(placeForGroupWindow)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, GroupLayout.PREFERRED_SIZE, 390, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }



    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList<String> jList4;
    private javax.swing.JTabbedPane placeForGroupWindow;
    private javax.swing.JTextField jTextField1;
    private DefaultListModel<String> groupList;


    public void addGroupToPanel(String name, GroupView groupPanel){
        placeForGroupWindow.addTab(name, groupPanel);
        placeForGroupWindow.setSelectedComponent(groupPanel);
        groups.put(name, groupPanel);
    }


    @Override
    public void notifyAboutJoinToGroupRequest(String groupName) {
        for (MenuViewListener listener: listeners){
            listener.handleJoin(groupName, true);
        }
    }

    @Override
    public void subscribeToChanging(MenuViewListener listener) {
        listeners.add(listener);
    }


    @Override
    public void notifyAboutLogoutCommand() {
        for(MenuViewListener listener: listeners){
            listener.handleLogout();
        }
    }

    @Override
    public void notifyAboutCreateGroup(String groupName, String isPublic) {
        for (MenuViewListener listener: listeners){
            listener.handleCreateGroup(groupName, true);
        }
    }


    @Override
    public void handleAnswerOnRequestGroupList(List<Group> groups) {
        synchronized (groupList) {
            groupList.clear();
            for (Group group : groups) {
                groupList.addElement(group.getGroupName());
            }
        }
    }

    @Override
    public void handleSuccessAnswerOnCreateGroup(String info) {
        notifyAboutOpenGroupWindow(info, true);
    }

    @Override
    public void handleLeave(String info) {
        groups.remove(info);
        System.out.println("leave");
    }

    @Override
    public void handleAddGroupToList(String groupName, boolean groupType) {
        synchronized (groupList) {
            groupList.addElement(groupName);
        }
    }

    @Override
    public void handleRemoveGroup(String groupName) {
        synchronized (groupList) {
            groupList.removeElement(groupName);
        }
    }

    @Override
    public void handleSuccessAnswerJoin(String groupName) {
        notifyAboutOpenGroupWindow(groupName, true);
    }

    @Override
    public void notifyAboutOpenGroupWindow(String groupName, boolean isPublic){
        for (MenuViewListener listener: listeners){
            listener.handleOpenGroupWindow(groupName, true, placeForGroupWindow);
        }
    }

    public boolean consist(String groupName) {
        synchronized (groupList) {
            return groupList.contains(groupName);
        }
    }
}

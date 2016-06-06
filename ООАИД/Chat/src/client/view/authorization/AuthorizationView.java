package client.view.authorization;

import client.controller.AuthorizationController;
import client.model.listeners.ClientAuthorizeListener;
import client.view.listeners.AuthorizeViewListener;
import client.view.observers.ObservableAuthorizeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yana on 13.12.15.
 */
public class AuthorizationView extends JFrame implements ObservableAuthorizeView, ClientAuthorizeListener {

    private List<AuthorizeViewListener> listeners = new ArrayList<>();
    private Label infoLabel;

    public AuthorizationView() {
        initComponents();
    }


    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        infoLabel = new java.awt.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "enter login and password", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(0, 153, 153))); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Login");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Password");

        jButton1.setText("Authorization");
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jTextField1.getText().isEmpty()){
                    changeInfoLabel("please, enter login");
                    return;
                }
                if(jTextField2.getText().isEmpty()){
                    changeInfoLabel("please, enter password");
                    return;
                }
                notifyAboutAuthorization(jTextField1.getText(), jTextField2.getText());
            }
        });

        jButton2.setText("Registration");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(jTextField1.getText().isEmpty()){
                    changeInfoLabel("enter login");
                    return;
                }
                if(jTextField2.getText().isEmpty()){
                    changeInfoLabel("enter password");
                    return;
                }
                notifyAboutRegistration(jTextField1.getText(), jTextField2.getText());
            }
        });

        infoLabel.setText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(39, 39, 39))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(38, 38, 38)
                                                                .addComponent(jButton1)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                                        .addComponent(jTextField2)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(16, 16, 16)
                                                                .addComponent(jButton2))))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(111, 111, 111)
                                                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }



    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;


    public static void main(String[] args) {
        AuthorizationController controller = new AuthorizationController();

    }


    private void changeInfoLabel(String text){
        infoLabel.setText(text);
    }


    @Override
    public void subscribeToChange(AuthorizeViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyAboutAuthorization(String login, String password) {
        for (AuthorizeViewListener listener: listeners){
            listener.handleLogin(login, password);
        }
    }

    @Override
    public void notifyAboutRegistration(String login, String password) {
        for (AuthorizeViewListener listener: listeners){
            listener.handleRegistration(login, password);
        }
    }

    @Override
    public void notifyAboutOpenMainMenu() {
        for (AuthorizeViewListener listener: listeners){
            listener.handleOpenMainMenu();
        }
    }

    @Override
    public void handleCompleteRegistration() {
        changeInfoLabel("registration complete");
        notifyAboutOpenMainMenu();
    }

    @Override
    public void handleCompleteAuthorization() {
        changeInfoLabel("authorization complete");
        notifyAboutOpenMainMenu();
    }

    @Override
    public void handleAnswerAboutError(String reason) {
        changeInfoLabel(reason);
    }

}

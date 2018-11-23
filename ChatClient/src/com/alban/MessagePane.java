package com.alban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class MessagePane extends JPanel implements MessageListener {

    private final ChatClient client;
    private final String login;

    /*document model(JList model) and
    current conversations*/
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> messageList = new JList<>(listModel);
    //input field
    private JTextField inputField = new JTextField();

    public MessagePane(ChatClient client, String login) {
        this.client = client;
        this.login = login;

        //adding myself ass message listener
        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {

            //adding event listener to the input. if the user press enter, this action will call
            @Override
            public void actionPerformed(ActionEvent e) {
                //after user press the enter, then this message will be sended to the client
                try {
                    String text = inputField.getText();
                    client.msg(login, text);
                    //adding conversation
                    listModel.addElement("You: " + text);
                    //resetting the empty text field
                    inputField.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    //adding to our conversation
    @Override
    public void onMessage(String fromLogin, String msgBody) {
        //filtering the certain user for certain pane
        if (login.equalsIgnoreCase(fromLogin)) {
            String line = fromLogin + ": " + msgBody;
            listModel.addElement(line);
        }
    }
}

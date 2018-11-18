package com.alban;

/*this interface is for to listen for online
and offline messages from the server*/


public interface UserStatusListener {

    // So in this listener I am going to have 2 methods. One for online when the user comes online and the other one is for when the user comes offline
    public void online(String login);
    public void offline(String login);
}

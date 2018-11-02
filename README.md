# ChatApp 

Created with IntelliJ IDEA

## Summary

    1- ServerSocket -> Accept() method, Connections
    
    2- Socket (Commnication with the Clients)
        2.1 - getOutputStream()
        2.2 - getInputStream() 
        
    3- Worker thread for handle Client communicatios.
    
    4- Accepting multiple connections
    
    5- User Presence
        *(Added Maven to my project for resolving token errors)
        5.1- Created commands to handle users username, passwords.
        5.2- Created commands to user so can can login, logoff and 
        their presens (they could see each other when the others are online)
        
    6- Handling Direct & Group Messaging
        6.1- Handled Logoff method
        6.2- Sending messages from one to another user (direct messaging)
        6.3- Group messaging (anybody member of that topic will recieve the msg)
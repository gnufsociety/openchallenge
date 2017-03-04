package com.gnufsociety.openchallenge;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("Message received:"+ remoteMessage.getNotification().getBody()+" from " +remoteMessage.getFrom());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}

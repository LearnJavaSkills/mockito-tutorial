package in.learnjavaskills.mockitotutorial.service;

public class NotificationService
{
    public boolean sendNotification(String toEmail, String Subject, String body)
    {
        System.out.println("notification send");
        return true;
    }
}

package aut.ap.service;


import Exceptions.UserNotFoundException;
import aut.ap.framework.ServiceBase;
import aut.ap.model.User;
import aut.ap.model.EmailRecipient;
import java.util.List;


public class UserService extends ServiceBase<User> {
    public UserService() {
        super(User.class);
    }


    public User findByEmail(String email) {
        String userEmail = email.contains("@") ? email : email + "@milou.com";

        try (var session = getSessionFactory().openSession()) {
            User user = session.createNativeQuery(
                            "select * from users where user_email = :email",
                            User.class)
                    .setParameter("email", userEmail)
                    .uniqueResult();

            if (user == null) {
                throw new UserNotFoundException(userEmail);
            }
            return user;
        }
    }




    public User register(String name, String email, String password){

        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }

        try {
            findByEmail(email);
            throw new IllegalArgumentException("This email is already taken.");
        } catch (UserNotFoundException e) {
            System.out.println("The user doesn't exist, so we can proceed with registration.");
        }

        User user = new User(name, email, password);
        persist(user);
        return user;
    }




    public void login(String email, String password) {
        try {
            User user = findByEmail(email);

            if (!user.getPassword().equals(password)) {
                throw new IllegalArgumentException("Incorrect password.");
            }

            System.out.println("Welcome back, " + user.getName() + "!");
            showUnreadEmails(user);

        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("Invalid email.");
        }
    }




    public void showUnreadEmails(User user) {
        try (var session = getSessionFactory().openSession()) {

            List<EmailRecipient> unreadEmails = session.createNativeQuery(
                            "select * from email_recipients " +
                                    "where user_id = :userId and is_read = false " +
                                    "order by(select sent_at from emails where id = email_id) desc",
                            EmailRecipient.class)
                    .setParameter("userId", user.getId())
                    .list();


            if (unreadEmails.isEmpty()) {
                System.out.println("No unread emails.");
            }

            else {
                System.out.println(unreadEmails.size() + " unread emails:");
                for (EmailRecipient recipient : unreadEmails) {

                    if (recipient.getEmail() != null) {
                        System.out.println("From: " + recipient.getEmail().getSender().getEmail());
                        System.out.println("Subject: " + recipient.getEmail().getSubject());
                        System.out.println("Code: " + recipient.getEmail().getCode());
                    }

                    else {
                        System.out.println("Email data missing");
                    }
                }
            }
        }
    }
}
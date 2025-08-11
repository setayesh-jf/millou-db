package aut.ap.service;



import Exceptions.EmailNotFoundException;
import aut.ap.framework.ServiceBase;
import aut.ap.model.Email;
import aut.ap.model.User;
import aut.ap.model.EmailRecipient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.hibernate.Session;
import java.util.HashSet;



public class EmailService extends ServiceBase<Email> {

    public EmailService() {
        super(Email.class);
    }

    public Email sendEmail(User sender, String subject, String body, List<User> recipientUsers) {
        Email email = new Email();
        email.setSender(sender);
        email.setSubject(subject);
        email.setBody(body);
        email.setSentAt(LocalDateTime.now());
        email.setOriginalEmail(null);

        Set<EmailRecipient> recipients = new HashSet<>();

        for (User user : recipientUsers) {
            recipients.add(new EmailRecipient(email, user));
        }

        email.setRecipients(recipients);

        persist(email);
        System.out.println("Successfully sent your email.");
        System.out.println("Code: " + email.getCode());
        return email;
    }




    private Email fetchByEmailCode(String code, Session session) {
        Email email = session.createNativeQuery(
                        "select * from emails where code = :code", Email.class)
                .setParameter("code", code)
                .uniqueResult();


        if (email == null) {
            throw new EmailNotFoundException(code);
        }

        return email;
    }




    public void viewEmails(User user, String EmailCategory) {
        List<EmailRecipient> emails = new ArrayList<>();

        try (var session = getSessionFactory().openSession()) {
            switch (EmailCategory.toLowerCase()) {
                case "all":
                    emails = session.createNativeQuery(
                                    "select * from email_recipients where recipient_id = :userId", EmailRecipient.class)
                            .setParameter("userId", user.getId())
                            .getResultList();
                    break;

                case "unread":
                    emails = session.createNativeQuery(
                                    "select * from email_recipients where recipient_id = :userId and is_read = false", EmailRecipient.class)
                            .setParameter("userId", user.getId())
                            .getResultList();
                    break;

                case "sent":
                    List<Email> sentEmails = session.createNativeQuery(
                                    "select * from emails where sender_id = :userId", Email.class)
                            .setParameter("userId", user.getId())
                            .getResultList();

                    for (Email email : sentEmails) {
                        emails.add(new EmailRecipient(email, user));
                    }

                    break;

                default:
                    System.out.println("Invalid selection.");
                    return;
            }

            if (emails.isEmpty()) {
                System.out.println("No emails found.");
            }

            else {
                System.out.println(EmailCategory.substring(0, 1).toUpperCase() + EmailCategory.substring(1) + " Emails:");
                for (EmailRecipient recipient : emails) {
                    Email email = recipient.getEmail();
                    System.out.println("+ " + email.getSender().getEmail() + " - " + email.getSubject() + " (" + email.getCode() + ")");
                }
            }
        }catch (Exception e) {
            System.out.println("Error while viewing emails: " + e.getMessage());
        }
    }




    public void viewEmailByCode(User user, String code) {
        try (Session session = getSessionFactory().openSession()) {
            Email email = fetchByEmailCode(code, session);

            List<EmailRecipient> recipients = session.createNativeQuery(
                            "select * from email_recipients where email_id = :emailId", EmailRecipient.class)
                    .setParameter("emailId", email.getId())
                    .getResultList();

            boolean isSender = email.getSender().equals(user);
            boolean isRecipient = false;
            for (EmailRecipient emailRecipient : recipients) {
                if (emailRecipient.getRecipient().equals(user)) {
                    isRecipient = true;
                    break;
                }
            }

            if (!isSender && !isRecipient) {
                System.out.println("You cannot read this email.");
                return;
            }

            System.out.println("Code: " + email.getCode());

            Set<String> uniqueEmails = new HashSet<>();
            StringBuilder recipientsStr = new StringBuilder();
            for (EmailRecipient emailRecipient : recipients) {
                String TargetEmail = emailRecipient.getRecipient().getEmail();
                if (uniqueEmails.add(TargetEmail)) {
                    if (recipientsStr.length() > 0) recipientsStr.append(", ");
                    recipientsStr.append(TargetEmail);
                }
            }

            System.out.println("Recipient(s): " + recipientsStr);
            System.out.println("Subject: " + email.getSubject());
            System.out.println("Date: " + email.getSentAt());
            System.out.println();
            System.out.println(email.getBody());

            session.beginTransaction();
            for (EmailRecipient emailRecipient : recipients) {
                if (emailRecipient.getRecipient().equals(user) && !emailRecipient.isRead()) {
                    emailRecipient.setRead(true);
                    emailRecipient.setReadAt(LocalDateTime.now());
                    session.update(emailRecipient);
                }
            }

            session.getTransaction().commit();

        } catch (EmailNotFoundException e) {
        System.out.println(e.getMessage());
    }

    }




    public void replyToEmail(User replier, String originalCode, String replyBody) {
        try (Session session = getSessionFactory().openSession()) {
            Email originalEmail = fetchByEmailCode(originalCode, session);

            List<EmailRecipient> recipients = session.createNativeQuery(
                            "select * from email_recipients where email_id = :emailId", EmailRecipient.class)
                    .setParameter("emailId", originalEmail.getId())
                    .getResultList();

            boolean isAllowed = originalEmail.getSender().equals(replier);
            if (!isAllowed) {
                for (EmailRecipient emailRecipient : recipients) {
                    if (emailRecipient.getRecipient().equals(replier)) {
                        isAllowed = true;
                        break;
                    }
                }
            }

            if (!isAllowed) {
                System.out.println("You cannot reply to this email.");
                return;
            }

            Email replyEmail = new Email();
            replyEmail.setSender(replier);
            replyEmail.setSubject("[Re] " + originalEmail.getSubject());
            replyEmail.setBody(replyBody);
            replyEmail.setOriginalEmail(originalEmail);
            replyEmail.setSentAt(LocalDateTime.now());

            Set<User> replyRecipients = new HashSet<>();
            if (!originalEmail.getSender().equals(replier)) {
                replyRecipients.add(originalEmail.getSender());
            }

            for (EmailRecipient emailRecipient : recipients) {
                User u = emailRecipient.getRecipient();
                if (!u.equals(replier)) {
                    replyRecipients.add(u);
                }
            }

            for (User u : replyRecipients) {
                replyEmail.addRecipient(u);
            }

            session.beginTransaction();
            session.persist(replyEmail);
            session.getTransaction().commit();

            System.out.println("Successfully sent your reply to email " + originalCode);
            System.out.println("Code: " + replyEmail.getCode());

        } catch (EmailNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }




    public void forwardEmail(User sender, String originalCode, List<User> recipients) {
        try (Session session = getSessionFactory().openSession()) {
            Email originalEmail = fetchByEmailCode(originalCode, session);

            Email forwardedEmail = new Email();
            forwardedEmail.setSender(sender);
            forwardedEmail.setSubject("[Fw] " + originalEmail.getSubject());
            forwardedEmail.setBody(originalEmail.getBody());
            forwardedEmail.setOriginalEmail(originalEmail);
            forwardedEmail.setSentAt(LocalDateTime.now());

            Set<EmailRecipient> recipientSet = new HashSet<>();
            for (User user : recipients) {
                recipientSet.add(new EmailRecipient(forwardedEmail, user));
            }

            forwardedEmail.setRecipients(recipientSet);

            session.beginTransaction();
            session.persist(forwardedEmail);
            session.getTransaction().commit();

            System.out.println("Successfully forwarded your email.");
            System.out.println("Code: " + forwardedEmail.getCode());

        } catch (EmailNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred while forwarding: " + e.getMessage());
        }
    }
}
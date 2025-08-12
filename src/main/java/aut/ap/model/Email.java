package aut.ap.model;
import aut.ap.framework.UniEntity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "emails")

public class Email extends UniEntity {

    @Column(unique = true, length = 6)
    private String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmailRecipient> recipients = new HashSet<>();

    @Basic(optional = false)
    @Column(name = "email_subject", nullable = false, length = 255)
    private String subject;

    @Basic(optional = false)
    @Lob
    @Column(name = "email_body", nullable = false)
    private String body;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_email_id", nullable = true)
    private Email originalEmail;

    public Email() {
        this.code = generateRandomCode();
        this.originalEmail = null;
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }


    public String getCode() {
        return code;
    }


    public User getSender() {
        return sender;
    }


    public String getSubject() {
        return subject;
    }


    public String getBody() {
        return body;
    }


    public LocalDateTime getSentAt() {
        return sentAt;
    }


    public Email getOriginalEmail() {
        return originalEmail;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public void setSender(User sender) {
        this.sender = sender;
    }


    public Set<EmailRecipient> getRecipients() {
        return recipients;
    }


    public void setRecipients(Set<EmailRecipient> recipients) {
        this.recipients = recipients;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }


    public void setBody(String body) {
        this.body = body;
    }


    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }


    public void setOriginalEmail(Email originalEmail) {
        this.originalEmail = originalEmail;
    }


    public void addRecipient(User user) {
        this.recipients.add(new EmailRecipient(this, user));
    }


    public Set<User> getRecipientUsers() {
        Set<User> users = new HashSet<>();
        for (EmailRecipient r : recipients) {
            users.add(r.getRecipient());
        }
        return users;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(id, email.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", sender=" + sender.getEmail() +
                ", subject='" + subject + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }
}
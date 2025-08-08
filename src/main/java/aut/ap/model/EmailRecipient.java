package aut.ap.model;


import jakarta.persistence.*;
import aut.ap.framework.UniEntity;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_recipients")

public class EmailRecipient extends UniEntity{

    @ManyToOne(optional = false)
    @JoinColumn(name = "email_id")
    private Email email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User recipient;

    @Basic(optional = false)
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;


    public Email getEmail() {
        return email;
    }


    public User getRecipient() {
        return recipient;
    }


    public LocalDateTime getReadAt() {
        return readAt;
    }


    public void setEmail(Email email) {
        this.email = email;
    }


    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }


    public boolean isRead() {
        return isRead;
    }


    public void setRead(boolean isRead) {
        this.isRead = isRead;
        if (isRead) {
            this.readAt = LocalDateTime.now();
        } else {
            this.readAt = null;
        }
    }


    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }


    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }


    public EmailRecipient() {
    }


    public EmailRecipient(Email email, User recipient) {
        this.email = email;
        this.recipient = recipient;
        this.isRead = false;
        this.readAt = null;
    }
}
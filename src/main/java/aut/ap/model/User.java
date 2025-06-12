package aut.ap.model;

import jakarta.persistence.*;
import aut.ap.framework.UniEntity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends UniEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Basic(optional = false)
    @Column(name = "user_email", nullable = false, length = 100)
    private String email;

    @Basic(optional = false)
    @Column(name = "user_password", nullable = false)
    private String password;


    @OneToMany(mappedBy = "recipient")
    private List<EmailRecipient> receivedEmails = new ArrayList<>();


    public String getName(){
        return name;
    }


    public String getEmail(){
        return email;
    }


    public String getPassword(){
        return password;
    }



    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email.contains("@") ? email : email + "@milou.com";
    }

    public void setPassword(String password){
        this.password = password;
    }



    public User() {

    }

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email + '\'' +
                '}';
    }
}

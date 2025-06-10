package aut.ap.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class User {
    private String name;
    private String email;
    private String password;
    private int id;

    public String getName(){
        return name;
    }


    public String getEmail(){
        return email;
    }


    public String getPassword(){
        return password;
    }


    public int getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }


    public void setId(int id){
        this.id = id;
    }


    public User() {

    }

    public User(String name, String email, String password, int id){
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = id;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email + ", password=" + password+
                '}';
    }
}

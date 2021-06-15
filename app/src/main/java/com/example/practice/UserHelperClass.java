package com.example.practice;

public class UserHelperClass {
    String username ,email,phoneNo, password, Image_id;



    public UserHelperClass( String username, String email, String phoneNo, String password, String Image_id) {
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
        this.Image_id = Image_id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage_id() {
        return Image_id;
    }

    public void setImage_id(String Image_id) {
        this.Image_id = Image_id;
    }
}

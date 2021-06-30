package com.example.mycontacts;

public class CONTACTS {

    public String name;
    public String phone;
    public String email;
    public  String key;

    public CONTACTS(String key, String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

}

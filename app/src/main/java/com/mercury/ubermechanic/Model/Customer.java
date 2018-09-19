package com.mercury.ubermechanic.Model;

public class Customer {
    String name;
    String phone;
    String email;


    public Customer(String name, String phone, String email, String noOfCovers) {
        this.name = name;
        this.phone = phone;
        this.email = email;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}

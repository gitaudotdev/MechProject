package com.mercury.ubermechanic.Model;

public class User {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String NationalId;
    private String avatarURL;
    private String services;
    private String rates;


    public User() {
    }

    public User(String name, String email, String phone, String password, String nationalId, String avatarURL, String services, String rates) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        NationalId = nationalId;
        this.avatarURL = avatarURL;
        this.services = services;
        this.rates = rates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNationalId() {
        return NationalId;
    }

    public void setNationalId(String nationalId) {
        NationalId = nationalId;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getRates() {
        return rates;
    }

    public void setRates(String rates) {
        this.rates = rates;
    }
}

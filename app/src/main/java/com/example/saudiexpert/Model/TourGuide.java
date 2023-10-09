package com.example.saudiexpert.Model;

import java.io.Serializable;

public class TourGuide implements Serializable {

    private String key;
    private int idNumber;
    private String firstName, lastName, email, password;
    private String BirthDate;
    private String SpeakingLanguages, Nationality, BriefDescription;
    private String imageUrl;

    public TourGuide() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }

    public String getSpeakingLanguages() {
        return SpeakingLanguages;
    }

    public void setSpeakingLanguages(String speakingLanguages) {
        SpeakingLanguages = speakingLanguages;
    }

    public String getNationality() {
        return Nationality;
    }

    public void setNationality(String nationality) {
        Nationality = nationality;
    }

    public String getBriefDescription() {
        return BriefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        BriefDescription = briefDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

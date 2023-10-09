package com.example.saudiexpert.Model;

import java.io.Serializable;

public class TourBook implements Serializable {

    private String
            ID,
            tourGuideKey,
            touristKey,
            tourID,
            date,
            time,
            status;

    public TourBook() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTourGuideKey() {
        return tourGuideKey;
    }

    public void setTourGuideKey(String tourGuideKey) {
        this.tourGuideKey = tourGuideKey;
    }

    public String getTouristKey() {
        return touristKey;
    }

    public void setTouristKey(String touristKey) {
        this.touristKey = touristKey;
    }

    public String getTourID() {
        return tourID;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
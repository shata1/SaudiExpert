package com.example.saudiexpert.Model;

import java.io.Serializable;

public class Tour implements Serializable {

    private String
            tourID,
            tourGuideKey,
            tourTitle,
            tourDescription,
            tourLocation,
            tourStarTime,
            tourDuration,
            tourMeetingPoint,
            tourImageUrl;

    private int numberOfPeople, tourPrice;

    public Tour() {
    }

    public String getTourID() {
        return tourID;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public String getTourGuideKey() {
        return tourGuideKey;
    }

    public void setTourGuideKey(String tourGuideKey) {
        this.tourGuideKey = tourGuideKey;
    }

    public String getTourTitle() {
        return tourTitle;
    }

    public void setTourTitle(String tourTitle) {
        this.tourTitle = tourTitle;
    }

    public String getTourDescription() {
        return tourDescription;
    }

    public void setTourDescription(String tourDescription) {
        this.tourDescription = tourDescription;
    }

    public String getTourLocation() {
        return tourLocation;
    }

    public void setTourLocation(String tourLocation) {
        this.tourLocation = tourLocation;
    }

    public String getTourStarTime() {
        return tourStarTime;
    }

    public void setTourStarTime(String tourStarTime) {
        this.tourStarTime = tourStarTime;
    }

    public String getTourDuration() {
        return tourDuration;
    }

    public void setTourDuration(String tourDuration) {
        this.tourDuration = tourDuration;
    }

    public String getTourMeetingPoint() {
        return tourMeetingPoint;
    }

    public void setTourMeetingPoint(String tourMeetingPoint) {
        this.tourMeetingPoint = tourMeetingPoint;
    }

    public String getTourImageUrl() {
        return tourImageUrl;
    }

    public void setTourImageUrl(String tourImageUrl) {
        this.tourImageUrl = tourImageUrl;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public int getTourPrice() {
        return tourPrice;
    }

    public void setTourPrice(int tourPrice) {
        this.tourPrice = tourPrice;
    }
}

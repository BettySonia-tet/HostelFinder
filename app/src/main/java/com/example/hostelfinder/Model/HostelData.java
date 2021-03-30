package com.example.hostelfinder.Model;

public class HostelData {

    private String hostelName;
    private String location;
    private String type;
    private String rules;
    private String sharing;
    private String price;
    private String availableSpaces;
    private String hostelImage1;
    private String hostelImage2;
    private String hostelImage3;
    private String postid;
    private String HostelLatitude,HostelLongitude;

    public HostelData() {
    }

    public HostelData(String hostelName, String location, String type, String rules, String sharing, String price, String availableSpaces, String hostelImage1, String hostelImage2, String hostelImage3, String postid, String hostelLatitude, String hostelLongitude) {
        this.hostelName = hostelName;
        this.location = location;
        this.type = type;
        this.rules = rules;
        this.sharing = sharing;
        this.price = price;
        this.availableSpaces = availableSpaces;
        this.hostelImage1 = hostelImage1;
        this.hostelImage2 = hostelImage2;
        this.hostelImage3 = hostelImage3;
        this.postid = postid;
        HostelLatitude = hostelLatitude;
        HostelLongitude = hostelLongitude;
    }

    public String getHostelLatitude() {
        return HostelLatitude;
    }

    public void setHostelLatitude(String hostelLatitude) {
        HostelLatitude = hostelLatitude;
    }

    public String getHostelLongitude() {
        return HostelLongitude;
    }

    public void setHostelLongitude(String hostelLongitude) {
        HostelLongitude = hostelLongitude;
    }

    public String getHostelName() {
        return hostelName;
    }

    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getSharing() {
        return sharing;
    }

    public void setSharing(String sharing) {
        this.sharing = sharing;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailableSpaces() {
        return availableSpaces;
    }

    public void setAvailableSpaces(String availableSpaces) {
        this.availableSpaces = availableSpaces;
    }

    public String getHostelImage1() {
        return hostelImage1;
    }

    public void setHostelImage1(String hostelImage1) {
        this.hostelImage1 = hostelImage1;
    }

    public String getHostelImage2() {
        return hostelImage2;
    }

    public void setHostelImage2(String hostelImage2) {
        this.hostelImage2 = hostelImage2;
    }

    public String getHostelImage3() {
        return hostelImage3;
    }

    public void setHostelImage3(String hostelImage3) {
        this.hostelImage3 = hostelImage3;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}

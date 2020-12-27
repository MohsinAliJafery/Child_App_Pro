package com.survey.hujuhj.Model;

public class User {

    private String ID, Username, ImageUrl, Status, ParentID, hasChild;

    public User(String ID, String Username, String ImageUrl, String Status, String ParentID, String hasChild) {
        this.ID = ID;
        this.Username = Username;
        this.ImageUrl = ImageUrl;
        this.Status = Status;
        this.ParentID = ParentID;
        this.hasChild = hasChild;

    }

    public User() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getHasChild() {
        return hasChild;
    }

    public void setHasChild(String hasChild) {
        this.hasChild = hasChild;
    }
}

package com.lostandfound.lostandfound;

import android.net.Uri;

/**
 * Created by Ayush on 25/05/2017.
 */

public class ObjectInformation {

    String ItemName;
    String Tag;
    String Category;
    String Type;
    String ObjectImage;
    String Description;
    String Address;
    String UserEmail;
    String Uri;
    int Views;
    String ObjectID;

    public ObjectInformation() {

    }

    public ObjectInformation(String tag, String category, String type, String objectImage, String description, String address, String itemname, String uri, String userEmail, int views,String objectid) {
        Tag = tag;
        Category = category;
        Type = type;
        ObjectImage = objectImage;
        Description = description;
        Address = address;
        ItemName = itemname;
        Uri = uri;
        Views=views;
        UserEmail=userEmail;
        ObjectID=objectid;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri (String uri) {
        Uri = uri;
    }

    public int getViews() {
        return Views;
    }

    public void setViews (int views) {
        Views = views;
    }


    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectid) {
        ObjectID = objectid;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }
    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getObjectImage() {
        return ObjectImage;
    }

    public void setObjectImage(String objectImage) {
        ObjectImage = objectImage;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}

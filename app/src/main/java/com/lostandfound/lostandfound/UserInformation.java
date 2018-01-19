package com.lostandfound.lostandfound;

/**
 * Created by Ayush on 25/05/2017.
 */

public class UserInformation  {

    private String name;
    private String address;
    private String phone_num;
    private String Profile_pic;
    private String Email;

    public UserInformation(){

    }

    public UserInformation(String address, String name, String phone_num,String Profile_pic, String Email) {
        this.address = address;
        this.name = name;
        this.phone_num = phone_num;
        this.Profile_pic =  Profile_pic;
        this.Email =  Email;

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfile_pic() {
        return Profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        Profile_pic = profile_pic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }
}

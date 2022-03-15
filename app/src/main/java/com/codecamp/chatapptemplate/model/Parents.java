package com.codecamp.chatapptemplate.model;



public class Parents {
    String p_id;
    String p_fullName;
    String p_email;
    String p_imageURL;


    public Parents() {

    }

    public Parents( String email, String fullName,String id, String imageURL){
        this.p_fullName = fullName;
        this.p_email = email;
        this.p_imageURL = imageURL;
        this.p_id=id;
    }

    public String getP_id() {
        return p_id;
    }

    public String getP_fullName() {
        return p_fullName;
    }

    public String getP_email() {
        return p_email;
    }

    public String getP_imageURL() {
        return p_imageURL;
    }



    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public void setP_fullName(String p_fullName) {
        this.p_fullName = p_fullName;
    }

    public void setP_email(String p_email) {
        this.p_email = p_email;
    }

    public void setP_imageURL(String p_imageURL) {
        this.p_imageURL = p_imageURL;
    }


}


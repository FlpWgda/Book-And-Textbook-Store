package com.service.store.entity;

import javax.persistence.*;

@Entity
@Table
public class ImageUrl {

    @Id
    private String url;


    public ImageUrl() {
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

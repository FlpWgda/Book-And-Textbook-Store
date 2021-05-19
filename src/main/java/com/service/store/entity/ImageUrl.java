package com.service.store.entity;

import javax.persistence.*;

@Entity
@Table
public class ImageUrl {

    @Id
    private Integer imageUrlId;

    private String url;

    @ManyToOne
    private Item item;

    public ImageUrl() {
    }

    public Integer getImageUrlId() {
        return imageUrlId;
    }

    public void setImageUrlId(Integer imageUrlId) {
        this.imageUrlId = imageUrlId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}

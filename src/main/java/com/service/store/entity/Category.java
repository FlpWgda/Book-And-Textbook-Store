package com.service.store.entity;

import javax.persistence.*;

@Entity
@Table
public class Category {

    @Id
    private String genreName;

    public Category() {
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}

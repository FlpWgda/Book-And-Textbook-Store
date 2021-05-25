package com.service.store.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class Author {


    @Id
    private String name;


    public Author() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.service.store.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class Author {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer authorId;

    private String name;

    @ManyToMany(mappedBy = "authors")
    private List<Item> items;

    public Author() {
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}

package com.service.store.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table
public class ListOfItems {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer listOfItemsId;

    private String name;

    private Timestamp dateAdded;

    private boolean isBasket;

    @ManyToMany
    @JoinTable
    private List<Item> items;


    public ListOfItems() {
    }

    public Integer getListOfItemsId() {
        return listOfItemsId;
    }

    public void setListOfItemsId(Integer listOfItemsId) {
        this.listOfItemsId = listOfItemsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isBasket() {
        return isBasket;
    }

    public void setBasket(boolean basket) {
        isBasket = basket;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}

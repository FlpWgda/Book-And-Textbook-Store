package com.service.store.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class OrderInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer transactionId;

    private Double cost;

    @Enumerated(EnumType.STRING)
    private StateOfOrder stateOfOrder;

    private Timestamp dateOfLastModification;


    @OneToOne
    @JoinColumn
    private ListOfItems listOfItems;

    public OrderInfo() {
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public StateOfOrder getStateOfOrder() {
        return stateOfOrder;
    }

    public void setStateOfOrder(StateOfOrder stateOfOrder) {
        this.stateOfOrder = stateOfOrder;
    }

    public Timestamp getDateOfLastModification() {
        return dateOfLastModification;
    }

    public void setDateOfLastModification(Timestamp dateOfLastModification) {
        this.dateOfLastModification = dateOfLastModification;
    }


    public ListOfItems getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(ListOfItems listOfItems) {
        this.listOfItems = listOfItems;
    }
}

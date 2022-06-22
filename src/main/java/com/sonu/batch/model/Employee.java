package com.sonu.batch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer SerialNumber;
    private String Region;
    private String Country;
    private String ItemType;
    private String SalesChannel;
    private String OrderPriority;
    private String OrderDate;
    private String OrderID;
    private String ShipDate;
    private String UnitsSold;
    private String UnitPrice;
    private String UnitCost;
    private String TotalRevenue;
    private String TotalCost;
    private String TotalProfit;
}

package models;

import javax.persistence.*;

@Entity
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="PHARMACY_ID")
    public Long pharmacyID;

    @Column(name = "PHARM_NAME")
    public String pharmName;
}

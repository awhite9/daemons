package models;

import javax.persistence.*;

@Entity
public class RXCUI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="PRESCRIPTION_ID")
    public Long prescriptionID;

    @Column(name = "RXCUI")
    public Long rxcui;



}

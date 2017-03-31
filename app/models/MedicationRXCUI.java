package models;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class MedicationRXCUI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="MEDICATION_ID")
    public Long medicationID;

    @Column(name = "MEDICATION_NAME")
    public String medicationName;

    @Column(name = "RXCUI")
    public Long rxcui;


}

package models;

import javax.persistence.*;

@Entity
public class MedicationName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="MEDICATION_ID")
    public Long medicationID;

    @Column(name = "MEDICATION_NAME")
    public String medicationName;
}

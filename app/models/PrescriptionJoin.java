package models;


import javax.persistence.*;

@Entity
public class PrescriptionJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="PRESCRIPTION_ID")
    public Long prescriptionID;

    @Column(name ="DOSAGE")
    public String dosage;

    @Column(name = "NAME")
    public String name;
}

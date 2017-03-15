package models;


import javax.persistence.*;

@Entity
public class Allergies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="ALLERGY_ID")
    public Long allergyID;

    @Column(name = "ALLERGY_NAME")
    public String allergyName;

}

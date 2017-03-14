package models;

import javax.persistence.*;

@Entity
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="VACCINE_ID")
    public Long vaccinationGivenID;

}

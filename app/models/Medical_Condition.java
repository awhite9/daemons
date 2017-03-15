package models;

import javax.persistence.*;

@Entity
public class Medical_Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="MEDICAL_CONDITION_ID")
    public Long medicalConditionID;

    @Column(name = "MC_NAME")
    public String mcName;
}

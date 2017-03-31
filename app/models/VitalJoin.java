package models;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;

@Entity
public class VitalJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATE_TAKEN")
    public LocalDate dateTaken;

    @Column(name = "val1")
    public String val1;

    @Column(name = "val2")
    public String val2;
}

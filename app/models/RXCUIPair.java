package models;


public class RXCUIPair {

    private Long drug1;
    private Long drug2;


    public RXCUIPair(Long drug1, Long drug2)
    {
        this.drug1 = drug1;
        this.drug2 = drug2;
    }

    public Long getDrug1()
    {
        return drug1;
    }
    public Long getDrug2()
    {
        return drug2;
    }
}

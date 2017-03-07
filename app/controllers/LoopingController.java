package controllers;


import models.SuperJoin;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

public class LoopingController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public LoopingController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional(readOnly = true)
    public Result checkDB()
    {
        int count = 0;
        boolean going = true;

        while (going)
        {

            List<SuperJoin> superJoin = (List<SuperJoin>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.first_name, p.cell_phone, pr.start_time, f.frequency, pre.DOSAGE, m.NAME from patient p join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID join frequency f on pr.FREQUENCY_ID = f.FREQUENCY_ID join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID join medication m on pre.MEDICATION_ID = m.MEDICATION_ID", SuperJoin.class).getResultList();

            for(SuperJoin loop : superJoin)
            {
                System.out.println(loop.getFirstName()+" "+count);
            }
            count++;
            try
            {
                Thread.sleep(10000);
            }
            catch (Exception e)
            {
                System.out.println("oh no");
            }
            if(count>200)
            {
                going = false;
            }
        }
        return ok("Loop Started");
    }

}

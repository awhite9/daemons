package controllers;

import models.PatientVital;
import models.SuperJoin;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.List;

import static play.libs.Json.toJson;

public class TableJoinController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public TableJoinController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

        @Transactional(readOnly = true)
        public Result getSuperJoin ()
        {
            List<SuperJoin> superJoin = (List<SuperJoin>) jpaApi.em().createNativeQuery("select p.PATIENT_ID, p.first_name, p.cell_phone, pr.start_time, f.frequency, pre.DOSAGE, m.NAME from patient p join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID join frequency f on pr.FREQUENCY_ID = f.FREQUENCY_ID join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID join medication m on pre.MEDICATION_ID = m.MEDICATION_ID", SuperJoin.class).getResultList();

            return ok(toJson(superJoin));
        }



        @Transactional(readOnly = true)
        public Result getGoogleChart()
        {
            List<PatientVital> patientVitals = (List<PatientVital>) jpaApi.em().createNativeQuery(
                    "select pv.PATIENT_VITAL_ID, v.name, pv.value, pv.date_taken from patient p \n" +
                    "join patient_vital pv on p.PATIENT_ID = pv.PATIENT_ID \n" +
                    "join vitals v on pv.VITAL_ID = v.VITAL_ID where v.NAME = \'weight\'", PatientVital.class).getResultList();


            for (PatientVital p:patientVitals)
            {
                System.out.println("google charts pulling from DB");
            }
            return ok(views.html.googlecharts.render(patientVitals));
        }


    }


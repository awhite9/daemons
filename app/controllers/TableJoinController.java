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
        public Result getGoogleChart() {
            Query query = jpaApi.em().createNativeQuery("select p.patient_id, v.name, pv.value, pv.date_taken from patient p join patient_vital pv on p.PATIENT_ID = pv.PATIENT_ID join vitals v on pv.VITAL_ID = v.VITAL_ID where v.NAME = :weight", PatientVital.class );
            query.setParameter("weight", "weight");
            List<PatientVital> patientVitals = (List<PatientVital>) query.getResultList();

            for (PatientVital p:patientVitals)
            {
                System.out.println("WEIGHT: " + p.value);
            }
            return ok(toJson(patientVitals));
        }


    }


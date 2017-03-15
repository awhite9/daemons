package controllers;



import models.VitalDate;
import models.VitalList;
import models.VitalManagerJoin;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.ok;

public class VitalManagerController {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public VitalManagerController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional(readOnly = true)
    public Result vitalManager()
    {
        List<VitalManagerJoin> vitalManagerList = (List<VitalManagerJoin>) jpaApi.em().createNativeQuery("select pv.PATIENT_VITAL_ID, v.vital_name, pv.value, pv.date_taken from patient_vital pv\n" +
                "join vitals v on pv.VITAL_ID = v.VITAL_ID\n" +
                "order by v.VITAL_NAME, pv.DATE_TAKEN desc", VitalManagerJoin.class).getResultList();

        List<VitalDate> vitalDates = (List<VitalDate>) jpaApi.em().createNativeQuery("select date_taken from patient_vital group by date_taken desc limit 3", VitalDate.class).getResultList();

        for(VitalDate date: vitalDates)
        {
            System.out.println(date);
        }

        List<VitalList> vitalList = new ArrayList<>();
        VitalList vitalItem = null;

        int vitalIndex = 1;

        for(VitalManagerJoin vital: vitalManagerList)
        {
            if (vitalItem == null || !vitalItem.getVitalName().equals(vital.vitalName))
            {
                vitalItem = new VitalList();
                vitalList.add(vitalItem);
                vitalItem.setVitalName(vital.vitalName);
                vitalIndex = 1;
            }

            switch (vitalIndex)
            {
                case 1:
                    vitalItem.setDate1(vital.dateTaken);
                    vitalItem.setId1((vital.patientVitalID));
                    vitalItem.setValue1(vital.value);
                    break;
                case 2:
                    vitalItem.setDate2(vital.dateTaken);
                    vitalItem.setId2((vital.patientVitalID));
                    vitalItem.setValue2(vital.value);

                    break;
                case 3:
                    vitalItem.setDate3(vital.dateTaken);
                    vitalItem.setId3((vital.patientVitalID));
                    vitalItem.setValue3(vital.value);

                    break;
                default:
                    //we do not care about values past thr third one
            }
            vitalIndex++;
        }


        return ok(views.html.vitalManager.render(vitalDates, vitalList, vitalManagerList));
    }
}

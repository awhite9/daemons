package controllers;

import models.Interaction;
import models.RXCUI;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

public class InteractionController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public InteractionController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    @Transactional(readOnly = true)
    public Result interactions() {
        List<Interaction> interaction = (List<Interaction>) jpaApi.em().createNativeQuery("select di.drug_interaction_id, m.MEDICATION_NAME as \"drug1Name\", di.rxcui, di.inter_rxcui, md.medication_name as \"drug2Name\", di.INTER_DESCRIPTION from drug_interaction di\n" +
                "join medication m on m.RXCUI = di.RXCUI \n" +
                "join medication md on md.RXCUI = di.INTER_RXCUI\n" +
                "where di.rxcui in (select rxcui from prescription p  join medication m on p.MEDICATION_ID = m.MEDICATION_ID where patient_id = 11)\n" +
                "and di.inter_rxcui in (select rxcui from prescription p  join medication m on p.MEDICATION_ID = m.MEDICATION_ID where patient_id = 11)\n" +
                "and m.MEDICATION_NAME in (select medication_name from prescription p join medication m on p.MEDICATION_ID = m.MEDICATION_ID where patient_id = 11)\n" +
                "and md.MEDICATION_NAME in (select medication_name from drug_interaction dri join medication md on dri.INTER_RXCUI = md.RXCUI)", Interaction.class).getResultList();

        return ok(views.html.drugInteraction.render(interaction));
    }


}

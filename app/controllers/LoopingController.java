package controllers;


import models.Prescription_Reminder;
import models.SuperJoin;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.List;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import java.util.HashMap;
import java.util.Map;



public class LoopingController extends Controller {

    private final FormFactory formFactory;
    private final JPAApi jpaApi;

    @Inject
    public LoopingController(FormFactory formFactory, JPAApi jpaApi) {
        this.formFactory = formFactory;
        this.jpaApi = jpaApi;
    }

    //this is the program that is called though the run function in the daemon package daemon
    @Transactional
    public Result checkDB()
    {

        System.out.println("In the checkDB function");
            List<SuperJoin> superJoin = (List<SuperJoin>) jpaApi.em().createNativeQuery("select pr.REMINDER_ID, p.PATIENT_ID, p.first_name, p.cell_phone, pr.NEXT_REMINDER, f.frequency, pre.DOSAGE, m.MEDICATION_NAME, f.FREQUENCY_ID, pre.PRESCRIPTION_ID from patient p\n" +
                    "join prescription_reminder pr on p.PATIENT_ID = pr.PATIENT_ID\n" +
                    "join prescription pre on pr.PRESCRIPTION_ID = pre.PRESCRIPTION_ID\n" +
                    "join medication m on pre.MEDICATION_ID = m.MEDICATION_ID\n" +
                    "join frequency f on pre.FREQUENCY_ID = f.FREQUENCY_ID", SuperJoin.class).getResultList();

            for(SuperJoin loop : superJoin)
            {   //checking to see if there is a reminder in the past
                int r =LocalTime.now().compareTo(loop.nextReminder);
                System.out.println("int R from comparison function: "+r);
                if(r>0)
                {   //what to do when here is a reminder in the past
                    System.out.println("Time has passed!! do something!!");

                    //actually sending the SMS
                    prescriptionSMS(loop.firstName, loop.medicationName, loop.dosage, loop.cellPhone);
                    System.out.println("Message Sent");

                    System.out.println("current reminder time: "+loop.nextReminder);
                    System.out.println("current time: "+LocalTime.now());
                    LocalTime newNextReminder = LocalTime.now().plusSeconds(loop.frequency);
                    System.out.println("New Reminder time: "+newNextReminder);

                    //saves the newNextTime reminder into the database
                    Long reminderID = loop.reminderID;
                    Prescription_Reminder prescriptionReminder = (Prescription_Reminder) jpaApi.em().createQuery("select pr from Prescription_Reminder pr where reminderID = :id").setParameter("id", reminderID).getSingleResult();
                    prescriptionReminder.nextReminder = newNextReminder;
                    jpaApi.em().persist(prescriptionReminder);


                    System.out.println("next Scheduled time after db update: "+prescriptionReminder.nextReminder);

                }
                else
                {
                    System.out.println("not time yet...");
                }
            }



        return redirect(routes.LoopingController.buttonPage());
    }

    public Result buttonPage()
    {
        return ok(views.html.button.render());
    }

    public static void prescriptionSMS(String firstName, String name, String dosage, String cellPhone)
    {
        AmazonSNSClient snsClient = new AmazonSNSClient();
        String message = firstName+", its time to take your "+name+" "+dosage;
        String phoneNumber = "+1"+cellPhone;
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("mySenderID") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));
        sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);

    }
    public static void sendSMSMessage(AmazonSNSClient snsClient, String message,
                                      String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
    }

}

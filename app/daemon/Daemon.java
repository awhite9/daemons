package daemon;



import controllers.routes;
import play.Logger;

import java.net.HttpURLConnection;
import java.net.URL;

public class Daemon implements Runnable
{
        private static int runNumber = 0;
        private static boolean running = false;



        public static boolean getRunning()
        {
            return running;
        }

        @Override
        public void run()
        {
            try {


                URL url = new URL("http://localhost:9000/buttonStart");

                //Get the request setup
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                //Make the actual call to the web service
                request.setRequestMethod("POST");
                request.connect();
                request.getResponseCode();
                System.out.println("We got here!!");

            } catch (Exception e)
            {
                Logger.error("oh no! got some exception: " + e.getMessage());
            }


            running = true;
            System.out.println("Test: "+runNumber++);
        }
    }



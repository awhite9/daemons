package daemon;



import controllers.routes;

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
            running = true;
            routes.TableJoinController.getSuperJoin().fragment();

            System.out.println("Test: "+runNumber++);
        }
    }



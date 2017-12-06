package eu.asterics.component.processor.acousticscanning;

import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;



import java.lang.*;

import eu.asterics.mw.services.AstericsThreadPool;

public class ProcessSelection implements Runnable {
        private boolean selectionDone = false;
        private final AcousticScanningInstance owner;
        private String keyset;
        Future<?> runningThread = null;
        boolean finish=false;
    
        protected BlockingQueue<String> queue = null;
        /**
         * The class constructor.
         * 
         * @param owner
         *            the TextSpeller instance
         */
        public ProcessSelection(AcousticScanningInstance owner, BlockingQueue queue) {
            this.owner = owner;
            this.queue = queue;
        }
    
        public void start() {
            finish=false;
            runningThread= AstericsThreadPool.instance.execute(this);
        }
        
        public void stop() {
            runningThread.cancel(true);    
            finish=true;
        }
     
        public void addSelections(String keyset) {
            String currentToken="";
            StringTokenizer st = new StringTokenizer(keyset,",");

            selectionDone=false;
            queue.clear();
            while (st.hasMoreTokens()) {
                currentToken=st.nextToken();
                try {
                    queue.put(currentToken);
                } catch (Exception e) { 
                    System.out.println("put interrupted");
                }
            }
        }
        
        public void select() {
            selectionDone=true;
        }
    
        @Override
        public void run() {
            System.out.println("Playthread started");           
            String currentToken="";
            while (finish==false) {
                try {
                    currentToken=queue.take();
                    if (currentToken.contains("#"))
                        owner.marySay(currentToken.substring(0,currentToken.indexOf("#")));
                    else owner.marySay(currentToken);
                    
                    if ((!finish) && (selectionDone==false))
                      Thread.sleep(owner.propDelayTime);
                }
                catch (InterruptedException e) {
                    System.out.println("take interrupted");
                } 
                
                if (selectionDone==true)
                {
                    System.out.println("selected key:"+currentToken);
                    owner.addKey(currentToken);
                    queue.clear();
                    selectionDone=false;
                }
           }
           System.out.println("Playthread finished");           
        }
    }
    
    

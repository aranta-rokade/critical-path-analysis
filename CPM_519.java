import java.io.*;
import java.util.*;

public class CPM_519 {
    
    EventNodeGraph eventNodeGraph;

    public static void main(String[] args) {
        CPM_519 objClass = new CPM_519();
        objClass.eventNodeGraph = objClass.createEventNodeGraph();
        objClass.calculateEarliestCompletion(objClass.eventNodeGraph.NO_MILESTONES);
        objClass.calculateLatestCompletion(1);
        objClass.calculateTimesForActivity();
        //objClass.eventNodeGraph.print();
        //objClass.eventNodeGraph.printMilestones();
        
        System.out.println("==============================================");
        System.out.println("\tCRITICAL PATH ANALYSIS");
        System.out.println("==============================================");
        System.out.println("Activity:");
        System.out.println("X(n) v->w");
        System.out.println("X: Activity Name\n"
                + "n: Activity Duration\n"
                + "v: start milestone of activtity\n"
                + "w: end milestone of activtity");
        System.out.println("ECT : Earliest Completetion Time of the activity(in weeks)\n"
                + "LCT: Latest Completion Time of the activity(in weeks)\n"
                + "SLACK: Slack Time of the activity(in weeks)");
        System.out.println("\nCritical Path: \n"
                + "(1) > A > (2) > B .... Z > (n) > END\n"
                + "1,2,3,...,n : Milestones\n"
                + "A,B,C,...,Z : Activities\n");
        objClass.eventNodeGraph.printActivities();
        
        System.out.println("\n=======================\n"
                + "\tCRITICAL PATH\n"
                + "=======================");        
        objClass.criticalPath(1);
        System.out.println();
    }
    
    /**
     * Calculates the Earliest Completion Time(ECT) of a milestone.
     * Uses recursion to find all the previous milestones' ECT and adds the activity's duration.
     * Selects the max duration as the current milestone's ECT 
     * as it has to wait for the inward activities to complete
     */
    int calculateEarliestCompletion(int finalMilestoneID) {
        finalMilestoneID -= 1;
        Milestone m = eventNodeGraph.milestoneList.get(finalMilestoneID);
        
        /**
         * first milestone EC1 = 0
         */
        if(m.inActivities.size() == 0) {
            m.ECT = 0;
            return m.ECT;
        }
        else {
            int len = 0;
            int new_ect = 0;
            
        /**
         * ECT = Max(ECT of previous milestone + time taken for each inward activity)
         */
        while(len < m.inActivities.size()) {
                Activity inActivity = m.inActivities.get(len);
                if(m.ECT < ( new_ect = calculateEarliestCompletion(inActivity.fromMilestoneID)
                                + inActivity.duration)) {
                    m.ECT = new_ect;
                }
                len++;
            }
            return m.ECT;
        }
    }
    
    /**
     * Calculates the Latest Completion Time(LCT) of a milestone.
     * Uses recursion to find all the next milestones' LCT and minus the activity's duration.
     * Selects the min duration as the current milestone's LCT 
     * as it can begin as soon as one outward activities to begins
     */
    int calculateLatestCompletion(int startMilestoneID) {
        startMilestoneID -= 1;
        Milestone m = eventNodeGraph.milestoneList.get(startMilestoneID);
        /**
         * last milestone LCTn = ECTn
         */
        if(m.outActivities.size() == 0) {
            m.LCT = m.ECT;
            return m.ECT;
        }
        else {
            int len = 0;
            int new_lct = 0;
            /**
             * LCT = Min(LCT of next milestone - time taken for each outward activity)
             */
            while(len < m.outActivities.size()) {
                Activity outActivity = m.outActivities.get(len);
                if(m.LCT > ( new_lct = calculateLatestCompletion(outActivity.toMilestoneID)
                                - outActivity.duration)) {
                    m.LCT = new_lct;
                }
                len++;
            }
            return m.LCT;
        }
    }
    
    /**
     * Calculates the Earliest Completion Time(ECT) and Latest Completion Time(LCT) of all Activities.
     */
    void calculateTimesForActivity() {
        for (int i = 0; i < eventNodeGraph.activityList.size(); i++) {
            Activity a = eventNodeGraph.activityList.get(i);
            
            /**
             * ECT = ECT of previous milestone + duration of activity
             */
            Milestone from = eventNodeGraph.milestoneList.get(a.fromMilestoneID-1);
            a.ECT = from.ECT + a.duration;

            /**
             * LCT = LCT of previous milestone
             */            
            Milestone to = eventNodeGraph.milestoneList.get(a.toMilestoneID-1);
            a.LCT = to.LCT;
            
            /**
             * SLACK = LCT of next milestone - ECT of previous milestone - duration of activity
             */
            a.SLACK = to.LCT - from.ECT - a.duration;
        }
    }
    
    /**
     * Calculates the critical path of the project.
     * Uses recursion to call the next milestone until it reaches the last milestone.
     * Selects the path(activity) having slack as ZERO
     */
    void criticalPath(int startMilestoneID) {
        startMilestoneID -= 1;
        Milestone m = eventNodeGraph.milestoneList.get(startMilestoneID);
        if(m.outActivities.size() == 0) {
            System.out.println("END");
            return;
        }
        int len = 0;
        while(len < m.outActivities.size()) {
            Activity outActivity = m.outActivities.get(len);
            if(outActivity.SLACK == 0) {
                if(m.inActivities.size() == 0) {
                    System.out.print("START > (" + m.milestoneID + ") > ");
                }
                System.out.print(outActivity.activity + " > (" + outActivity.toMilestoneID + ") > ");
                criticalPath(outActivity.toMilestoneID);
            }
            len++;
        }
    }
    
    /**
     * Initializes the graph.
     */
    EventNodeGraph createEventNodeGraph(){    
        EventNodeGraph graph = new EventNodeGraph(17);
        graph.addActivity(1, 2, 'A', 3);        
        graph.addActivity(1, 3, 'B', 6);
        graph.addActivity(1, 4, 'C', 5);
        graph.addActivity(2, 5, 'D', 2);
        graph.addActivity(3, 6, 'E', 4);
        graph.addActivity(4, 7, 'F', 8);
        graph.addActivity(5, 8, 'G', 4);
        graph.addActivity(6, 8, 'H', 7);
        graph.addActivity(6, 9, 'I', 1);
        graph.addActivity(7, 9, 'J', 3);
        graph.addActivity(7, 13, 'K', 12);
        graph.addActivity(8, 10, 'L', 4);
        graph.addActivity(9, 11, 'M', 5);
        graph.addActivity(9, 12, 'N', 3);
        graph.addActivity(10, 14, 'O', 6);
        graph.addActivity(11, 14, 'P', 4);
        graph.addActivity(12, 15, 'Q', 9);
        graph.addActivity(13, 15, 'R', 8);
        graph.addActivity(14, 16, 'S', 2);
        graph.addActivity(15, 16, 'T', 3);
        graph.addActivity(16, 17, 'U', 2);
        
        for (int i = 1; i <= graph.NO_MILESTONES; i++) {
            graph.addMilestone(i);
        }

        return graph;
    }
}

/**
* Class Milestone to store the milestone's properties:
* ECT 
* LCT 
* list of inward activities
* list outward activities 
*/
class Milestone {
    int milestoneID;
    int ECT = 0;
    int LCT = 99999999;
    ArrayList<Activity> inActivities;
    ArrayList<Activity> outActivities;
    
    public Milestone(int _milestoneID, ArrayList<Activity> _inActivities, ArrayList<Activity> _outActivities) {
        milestoneID = _milestoneID;
        inActivities = _inActivities;
        outActivities = _outActivities;
    }
    
    void print() {
        System.out.println("Milestone : " + milestoneID + 
                "\nECT : " + ECT + " LCT : " + LCT + "\n");
    }
}

/**
* Class Activity to store the activity's properties:
* fromMilestone
* toMilestone
* activity name
* duration
* ECT 
* LCT 
* SLACK time
*/
class Activity {
    int fromMilestoneID;
    int toMilestoneID;
    char activity;
    int duration;
    
    int ECT = 0;
    int LCT = 0;
    int SLACK = 0;
    
    Activity(int _fromMilestoneID, int _toMilestoneID, char _activity, int _duration) {
        fromMilestoneID = _fromMilestoneID;
        toMilestoneID = _toMilestoneID;
        activity = _activity;
        duration = _duration; 
    }
    void print() {
        System.out.println(activity + "(" + duration + ") " +fromMilestoneID + "->" + toMilestoneID + 
                "\nECT : " + ECT + " LCT : " + LCT + " SLACK : " + SLACK +"\n");
    }
}

/**
* Class EventNodeGraph to store the graph, properties are:
* NO_MILESTONES
* list of all activities
* list of all milestones
*/
class EventNodeGraph {
    
    int NO_MILESTONES;
    
    ArrayList<Activity> activityList;
    ArrayList<Milestone> milestoneList;
    
    EventNodeGraph(int noMilestones) {
        NO_MILESTONES = noMilestones;
        milestoneList =new ArrayList<Milestone>();
        activityList = new ArrayList<Activity>();
    }
    
    /**
    * adds a Milestone and intialises its variables
    */
    void addMilestone(int milestoneID) {
        Milestone milestone = new Milestone(milestoneID, getInwardActivites(milestoneID),getOutwardActivites(milestoneID));
        milestoneList.add(milestone);
    }
    
    /**
    * adds a Activity and intialies its variables
    */
    void addActivity(int fromMilestoneID, int toMilestoneID, char activityName, int activityDuration) {
        Activity activity = new Activity(fromMilestoneID, toMilestoneID, activityName, activityDuration);
        activityList.add(activity);
    }
    
    /**
     * gets all the inward activities of a milestone
     */
    public ArrayList<Activity> getInwardActivites(int toMilestoneID) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
                
        for (int i = 0; i < activityList.size(); i++) {
            if(activityList.get(i).toMilestoneID == toMilestoneID)
               activities.add(activityList.get(i)); 
        }
        return activities;
    }
    
    
    /**
     * gets all the outward activities of a milestone
     */
    public ArrayList<Activity> getOutwardActivites(int fromMilestoneID) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
                
        for (int i = 0; i < activityList.size(); i++) {
            if(activityList.get(i).fromMilestoneID == fromMilestoneID)
               activities.add(activityList.get(i)); 
        }
        return activities;
    }
    
    /**
     * prints the graph as milestones with inward and outward activities
     */
    void print() {
        System.out.println("\n=======================\n"
                + "\tGRAPH\n"
                + "=======================");
        for (int i = 0; i < milestoneList.size(); i++) {
            Milestone m = milestoneList.get(i);
            
            m.print();
            System.out.println("In");
            if(m.inActivities.size() == 0 ) {
                System.out.println("None");
            } else {
                for (int j = 0; j < m.inActivities.size(); j++) {
                    m.inActivities.get(j).print();
                }
            }
            System.out.println("Out");
            if(m.outActivities.size() == 0 ) {
                System.out.println("None");
            } else {
                for (int j = 0; j < m.outActivities.size(); j++) {
                    m.outActivities.get(j).print();
                }
            }
        }
    }
    
    /**
     * prints all the activities
     */
    void printActivities() {
        System.out.println("\n=======================\n"
                + "\tACTIVITIES\n"
                + "=======================");
        for (int i = 0; i < activityList.size(); i++) {
            activityList.get(i).print();
        }
    }
    
    /**
     * prints all the milestones
     */
    void printMilestones() {
        System.out.println("\n=======================\n"
                + "\tMILESTONES\n"
                + "=======================");
        for (int i = 0; i < milestoneList.size(); i++) {
            milestoneList.get(i).print();
        }
    }
}
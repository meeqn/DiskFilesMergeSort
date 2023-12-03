package org.DFMS;

import org.DFMS.Experimenting.ReportListener;

public class Main {

    private static void clearStartTapes(Tape t1, Tape t2){
        t1.clearTapeFile();
        t2.clearTapeFile();
    }
    private static void sortFile(ReportListener reportListener, DistributionTape t3, Tape t1, Tape t2, boolean detailedPrint){
        System.out.println("File pre-sort contents: ");
        t3.printTapeContents();
        int phase = 0;
        while(true) {
            reportListener.incPhases();
            phase+=1;
            if(detailedPrint) {
                System.out.println("Phase " + phase + " before splitting");
                System.out.println("t3:");
                t3.printTapeContents();
            }
            t3.splitBetweenTapes();
            if(detailedPrint) {
                System.out.println("Phase " + phase + " before merging");
                System.out.println("t1:");
                t1.printTapeContents();
                System.out.println("t2:");
                t2.printTapeContents();
            }
            Tape.mergeTapes(t3, t1, t2);
            if(t3.isTwoSeriesMerge()){
                break;
            }
        }
        System.out.println("File post-sort contents: ");
        t3.printTapeContents();
        reportListener.printStats();
    }
    public static void main(String[] args) {
        String filesDir = "src/main/resources/";
        final String t1FilePath = filesDir + "t1.txt";
        final String t2FilePath = filesDir + "t2.txt";
        final String t3FilePath = filesDir + "t3.txt";
        Tape t1;
        Tape t2;
        DistributionTape t3;
        ReportListener[] reportListeners = {
                new ReportListener(20),
                new ReportListener(80),
                new ReportListener(320),
                new ReportListener(1280),
                new ReportListener(4960)
        };
        for(int i=0; i< reportListeners.length; i++){
            t1 = new Tape(t1FilePath, reportListeners[i]);
            t2 = new Tape(t2FilePath, reportListeners[i]);
            int runsAmount;
            clearStartTapes(t1, t2);
            t3 = new DistributionTape(t3FilePath, t1, t2, reportListeners[i]);
//          runsAmount = t3.fillManually();
            runsAmount = t3.fillRandomly(reportListeners[i].getRecordsAmount());
            reportListeners[i].setRunsAmount(runsAmount);
            sortFile(reportListeners[i], t3, t1, t2, false);
        }
    }
}
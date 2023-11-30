package org.DFMS;

public class Main {

    private static void clearStartTapes(Tape t1, Tape t2){
        t1.clearTapeFile();
        t2.clearTapeFile();
    }
    private static void sortFile(DistributionTape t3, Tape t1, Tape t2){
        t3.splitBetweenTapes();
        Tape.mergeTapes(t3, t1, t2);
        //TODO loop it and add checking if after splitting t2 is empty
        //TODO add printing contents of tapes after phases
    }
    public static void main(String[] args) {
        String filesDir = "src/main/resources/";
        final String t1FilePath = filesDir + "t1.txt";
        final String t2FilePath = filesDir + "t2.txt";
        final String t3FilePath = filesDir + "t3.txt";

        Tape t1 = new Tape(t1FilePath);
        Tape t2 = new Tape(t2FilePath);
        clearStartTapes(t1, t2);

        DistributionTape t3 = new DistributionTape(t3FilePath, t1, t2);
//        t3.fillRandomly(); // uncomment if you're not passing pre-made file
        sortFile(t3, t1, t2);
    }
}
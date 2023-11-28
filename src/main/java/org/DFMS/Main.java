package org.DFMS;

import org.DFMS.Data.Buffering.DistributionTape;
import org.DFMS.Data.Buffering.Tape;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private static void sortFile(DistributionTape t3, Tape t1, Tape t2){
        t3.splitTape(t1, t2);
    }
    public static void main(String[] args) {
        String filesDir = "src/main/resources/";
        final String t1FilePath = filesDir + "t1.txt";
        final String t2FilePath = filesDir + "t2.txt";
        final String t3FilePath = filesDir + "t3.txt";

        Tape t1 = new Tape(t1FilePath);
        Tape t2 = new Tape(t2FilePath);
        DistributionTape t3 = new DistributionTape(t3FilePath, t1, t2);
        //t3.fillRandomly();
        sortFile(t3, t1, t2);
    }
}
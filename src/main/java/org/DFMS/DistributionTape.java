package org.DFMS;

import lombok.Getter;
import lombok.Setter;
import org.DFMS.Data.Buffering.Storing.Record;
import org.DFMS.Experimenting.ReportListener;

import java.io.*;
import java.sql.SQLOutput;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class DistributionTape extends Tape {
    private Tape[] tapes = new Tape[2];
    private int activeTapeIndex;
    @Getter
    @Setter
    private boolean twoSeriesMerge;
    public DistributionTape(String path, Tape t1, Tape t2, ReportListener repListener){
        super(path, repListener);
        tapes[0] = t1;
        tapes[1] = t2;
        activeTapeIndex = 0;
    }
    private Tape getActiveTape(){
        return this.tapes[activeTapeIndex];
    }
    private void switchActiveBuffer(){
        this.activeTapeIndex = (this.activeTapeIndex + 1)%2;
    }
    private void flushBuffers(){ //function for writing records remaining in buffers to their tapes
        if(!this.getActiveTape().buffer.isEmpty()){
            this.getActiveTape().loadBufferToFile();
        }
        this.switchActiveBuffer();
        if(!this.getActiveTape().buffer.isEmpty()){
            this.getActiveTape().loadBufferToFile();
        }
    }
    public void splitBetweenTapes(){
        this.activeTapeIndex=0;
        try (Scanner t3Stream = new Scanner(new File(this.getFilePath()))){
            Record lastPushed = null;   //this variable remembers last record in run, null indicates start of new run
            Record currPushed;    //variable storing record that is being pushed
            while (true) {
                currPushed = this.getNextRecord(t3Stream);
                if(currPushed == null){
                    break;
                }
                if(currPushed.compareTo(lastPushed) < 0){    //if run is ongoing but record being currently pushed is smaller than last
                    this.switchActiveBuffer();
                }
                getActiveTape().putNextRecord(currPushed);
                lastPushed = currPushed;
            }
            this.flushBuffers();
            this.clearTapeFile();
        }
        catch (FileNotFoundException | NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }
    public int fillRandomly(int recordsAmount){
        int runsAmount=1;
        int prevNum=0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFilePath()))) {
            Random random = new Random();
            for (int i = 0; i < recordsAmount; i++) {
                int num = random.nextInt(100000);
                if(i>0 && num<prevNum){
                    runsAmount+=1;
                }
                prevNum = num;

                writer.write(Integer.toString(num));
                writer.write(" ");
            }
            return runsAmount;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public int fillManually(){
        int runsAmount=1;
        int prevNum=0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFilePath()))) {
            System.out.println("Input desired amount of records: ");
            Scanner userInput = new Scanner(System.in);
            int recordsAmount = userInput.nextInt();
            for (int i = 0; i < recordsAmount; i++) {
                System.out.println("Input record number " + (i+1));
                int num = userInput.nextInt();
                if(i>0 && num<prevNum){
                    runsAmount+=1;
                }
                prevNum = num;
                writer.write(Integer.toString(num));
                writer.write(" ");
            }
            return runsAmount;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

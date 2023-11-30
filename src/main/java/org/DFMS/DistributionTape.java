package org.DFMS;

import org.DFMS.Data.Buffering.Storing.Record;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DistributionTape extends Tape {
    private Tape[] tapes = new Tape[2];
    private int activeTapeIndex;
    public DistributionTape(String path, Tape t1, Tape t2){
        super(path);
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
    private void flushBuffer(){ //function for writing records remaining in buffers to their tapes
        this.getActiveTape().loadBufferToFile();
        this.switchActiveBuffer();
        this.getActiveTape().loadBufferToFile();
    }
    public void splitBetweenTapes(){
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
            this.flushBuffer();
            this.clearTapeFile();
        }
        catch (FileNotFoundException | NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }
}

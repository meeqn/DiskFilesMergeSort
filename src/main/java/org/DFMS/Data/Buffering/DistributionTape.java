package org.DFMS.Data.Buffering;

import org.DFMS.Data.Buffering.Storing.Record;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DistributionTape extends Tape{
    private Tape[] tapes = new Tape[2];
    private int activeTapeIndex;
    public DistributionTape(String path, Tape t1, Tape t2){
        super(path);
        tapes[0] = t1;
        tapes[1] = t2;
        activeTapeIndex = 0;
    }
    private void switchActiveBuffer(){
        this.activeTapeIndex = (this.activeTapeIndex + 1)%2;
    }
    private Tape getActiveTape(){
        return this.tapes[activeTapeIndex];
    }
    private void flushBuffers(){
        this.getActiveTape().loadBuffer();
        this.switchActiveBuffer();
        this.getActiveTape().loadBuffer();
    }
    public void splitTape(Tape t1, Tape t2){
        try (Scanner t3_stream = new Scanner(new File(this.getFilePath()))){
            Record lastPushed = null;
            Record currPushed;
            while (t3_stream.hasNextInt()) {
                this.buffer.pushNextRecord(new Record(t3_stream.nextInt()));
                if(this.buffer.isFull()){
                    while(!this.buffer.isEmpty()){
                        currPushed = this.buffer.popFirstRecord();
                        if(lastPushed != null && currPushed.compareTo(lastPushed) < 0){
                            this.switchActiveBuffer();
                            lastPushed = null;
                        }
                        this.getActiveTape().buffer.pushNextRecord(currPushed);
                        if(this.getActiveTape().buffer.isFull()){
                            this.getActiveTape().loadBuffer();
                        }
                        lastPushed = currPushed;
                    }
                }
            }
            this.flushBuffers();
        }
        catch (FileNotFoundException | NoSuchElementException e) {
            throw new RuntimeException(e);
        }
    }
}

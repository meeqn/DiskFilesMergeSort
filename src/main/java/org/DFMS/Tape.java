package org.DFMS;

import lombok.Getter;
import org.DFMS.Data.Buffering.Buffer;
import org.DFMS.Data.Buffering.Storing.Record;
import org.DFMS.Experimenting.ReportListener;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

//NOTES
//TAPE_SIZE is required only for automatic and manual generation methods, if you load pre-filled file
//the code will also handle it
public class Tape {
    @Getter
    private final String filePath;
    @Getter
    protected Buffer buffer;
    public ReportListener repListener;
    public Tape(String path, ReportListener repListener){
        this.filePath = path;
        this.repListener = repListener;
        this.buffer = new Buffer();
    }
    public void loadBufferToFile(){
        try (BufferedWriter file_stream = new BufferedWriter(new FileWriter(this.getFilePath(), true))) {
            int recordValue;
            this.repListener.incWrites();
            while(!this.buffer.isEmpty()){
                recordValue = this.buffer.popFirstRecord().getVal();
                file_stream.write(Integer.toString(recordValue));
                file_stream.write(" ");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //post-sorting method for flushing records remaining in destTape buffer, clears other tapes
    private static void postSort(Tape destTape, Tape sourceTape1, Tape sourceTape2, Scanner t1Stream, Scanner t2Stream){
        t1Stream.close();
        t2Stream.close();
        if(!destTape.buffer.isEmpty()) {
            destTape.loadBufferToFile();
        }
        sourceTape1.clearTapeFile();
        sourceTape2.clearTapeFile();
    }
    public static void mergeTapes(DistributionTape destTape, Tape t1, Tape t2){
        try {
            Scanner t1Stream = new Scanner(new File(t1.filePath));
            Scanner t2Stream = new Scanner(new File(t2.filePath));
            boolean t1Blocked = false;
            boolean t2Blocked = false;

            Record lastPushed = null;
            Record lastPushedT1 = null;
            Record lastPushedT2 = null;

            Record currPushedT1 = t1.getNextRecord(t1Stream);
            Record currPushedT2 = t2.getNextRecord(t2Stream);
            destTape.setTwoSeriesMerge(true);
            while(true){
                if(currPushedT1 != null && currPushedT2 != null){ //both tapes are not empty
                    //current record breaks the run on t1
                    if(currPushedT1.compareTo(lastPushedT1)<0){
                        t1Blocked=true;
                        destTape.setTwoSeriesMerge(false);
                    }
                    //current record breaks the run on t2
                    if(currPushedT2.compareTo(lastPushedT2)<0){
                        t2Blocked=true;
                        destTape.setTwoSeriesMerge(false);
                    }

                    if(!t1Blocked && !t2Blocked){ //both tapes can still contribute to current run
                        lastPushed = getSmaller(currPushedT1, currPushedT2);
                        destTape.putNextRecord(lastPushed);
                    }
                    else if(!t1Blocked){ //t1 can still contribute to current run
                        lastPushed = currPushedT1;
                        destTape.putNextRecord(lastPushed);
                    }
                    else if(!t2Blocked){ //t2 can still contribute to current run
                        lastPushed = currPushedT2;
                        destTape.putNextRecord(lastPushed);
                    }
                    else{ //both tapes start contributing to current run
                        lastPushed = null;
                        lastPushedT1 = null;
                        lastPushedT2 = null;
                        t1Blocked = false;
                        t2Blocked = false;
                    }

                    if(lastPushed == currPushedT1){
                        lastPushedT1 = lastPushed;
                        currPushedT1 = t1.getNextRecord(t1Stream);
                    }
                    else if(lastPushed == currPushedT2){
                        lastPushedT2 = lastPushed;
                        currPushedT2 = t2.getNextRecord(t2Stream);
                    }
                }
                else if(currPushedT1 != null){ //only tape1 is not empty
                    lastPushed = currPushedT1;
                    destTape.putNextRecord(lastPushed);
                    currPushedT1 = t1.getNextRecord(t1Stream);
                }
                else if(currPushedT2 != null){ //only tape2 is not empty
                    lastPushed = currPushedT2;
                    destTape.putNextRecord(lastPushed);
                    currPushedT2 = t2.getNextRecord(t2Stream);
                }
                else{ //both tapes are empty
                    break;
                }
            }
            postSort(destTape, t1, t2, t1Stream, t2Stream);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static Record getSmaller(Record r1, Record r2){
        if(r1.compareTo(r2)<0){
            return r1;
        }
        else if(r2.compareTo(r1)<0){
            return r2;
        }
        else{
            return r1;
        }
    }
    public Record getNextRecord(Scanner tapeStream){
        if(this.buffer.isEmpty()){
            this.fillBufferFromFileStream(tapeStream);
            if(this.buffer.isEmpty()){
                return null;
            }
            repListener.incReads();
        }
        return this.buffer.popFirstRecord();
    }
    public void putNextRecord(Record record){
        this.buffer.pushNextRecord(record);
        if(this.buffer.isFull()){ //check if buffer is full, so it can be loaded to tape file
            this.loadBufferToFile();
        }
    }
    public void fillBufferFromFileStream(Scanner filestream){
        while(filestream.hasNextInt() && !this.buffer.isFull()){
            this.buffer.pushNextRecord(new Record(filestream.nextInt()));
        }
    }

    public void printTapeContents(){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(this.filePath));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void clearTapeFile(){
        try (FileWriter writer = new FileWriter(this.getFilePath())){
            writer.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

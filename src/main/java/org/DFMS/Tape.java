package org.DFMS;

import lombok.Getter;
import org.DFMS.Data.Buffering.Buffer;
import org.DFMS.Data.Buffering.Storing.Record;

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
    private static final int SCALE_FACTOR = 2; //how many buffers to fill the tape
    private static final int TAPE_SIZE = Buffer.BUFFER_SIZE * SCALE_FACTOR;
    public Tape(String path){
        this.filePath = path;
        this.buffer = new Buffer();
    }
    public void loadBufferToFile(){
        try (BufferedWriter file_stream = new BufferedWriter(new FileWriter(this.getFilePath(), true))) {
            int recordValue;
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
    private static void postSort(Tape destTape, Tape sourceTape1, Tape sourceTape2){
        destTape.loadBufferToFile();
        sourceTape1.clearTapeFile();
        sourceTape2.clearTapeFile();
    }
    public static void mergeTapes(Tape destTape, Tape t1, Tape t2){
        try {
            Scanner t1Stream = new Scanner(new File(t1.filePath));
            Scanner t2Stream = new Scanner(new File(t2.filePath));

            Record lastPushed = null;
            Record currPushedT1 = t1.getNextRecord(t1Stream);
            Record currPushedT2 = t2.getNextRecord(t2Stream);
            while(true){
                if(currPushedT1 != null && currPushedT2 != null){
                    if(currPushedT1.compareTo(lastPushed)>=0 && currPushedT2.compareTo(lastPushed)>=0){
                        lastPushed = getSmaller(currPushedT1, currPushedT2);
                        destTape.putNextRecord(lastPushed);
                    }
                    else if(currPushedT1.compareTo(lastPushed)>=0){
                        lastPushed = currPushedT1;
                        destTape.putNextRecord(lastPushed);
                    }
                    else if(currPushedT2.compareTo(lastPushed)>=0){
                        lastPushed = currPushedT2;
                        destTape.putNextRecord(lastPushed);
                    }
                    else{
                        lastPushed=null;
                    }
                }
                else if(currPushedT1 != null){
                    lastPushed = currPushedT1;
                    destTape.putNextRecord(lastPushed);
                }
                else if(currPushedT2 != null){
                    lastPushed = currPushedT2;
                    destTape.putNextRecord(lastPushed);
                }
                else{
                    break;
                }
                if(lastPushed == currPushedT1){
                    currPushedT1 = t1.getNextRecord(t1Stream);
                }
                else if(lastPushed == currPushedT2){
                    currPushedT2 = t2.getNextRecord(t2Stream);
                }

            }

            t1Stream.close();
            t2Stream.close();
            postSort(destTape, t1, t2);
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
    public void fillRandomly(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            Random random = new Random();
            for (int i = 0; i < TAPE_SIZE; i++) {
                int num = random.nextInt(100);
                writer.write(Integer.toString(num));
                if( i<TAPE_SIZE - 1)
                    writer.write(" ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void fillManually(){
        //TODO
    }
    public void clearTapeFile(){
        try (FileWriter writer = new FileWriter(this.getFilePath());){
            writer.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.DFMS.Data.Buffering;

import lombok.Getter;
import org.DFMS.Data.Buffering.Storing.Record;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

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
    public void loadBuffer(){
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
            e.printStackTrace();
        }
    }
    public void fillManually(){
        //TODO
    }
}

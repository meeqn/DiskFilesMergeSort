package org.DFMS.Data.Buffering;

import org.DFMS.Data.Buffering.Storing.Record;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Buffer {
    public static final int BUFFER_SIZE = 10; //number of records fitting in buffer
    private LinkedList<Record> recordList;
    public Buffer(){
        this.recordList = new LinkedList<>();
    }
    public Record popFirstRecord(){
        return this.recordList.pop();
    }
    public void pushNextRecord(Record record){
        this.recordList.addLast(record);
    }
    public boolean isFull(){
        return this.recordList.size()==BUFFER_SIZE;
    }
    public boolean isEmpty(){
        return this.recordList.isEmpty();
    }
}

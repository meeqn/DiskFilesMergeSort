package org.DFMS.Experimenting;


import lombok.Getter;
import lombok.Setter;

public class ReportListener {
    @Getter
    private int phases;
    @Getter
    private int reads;
    @Getter
    private int writes;
    @Getter
    private int recordsAmount;
    @Getter
    @Setter
    private int runsAmount;

    public ReportListener(int recordsAmount){
        this.phases = 0;
        this.reads = 0;
        this.writes = 0;
        this.recordsAmount = recordsAmount;
    }
    public void incPhases(){
        this.phases+=1;
    }
    public void incReads(){
        this.reads+=1;
    }
    public void incWrites(){
        this.writes+=1;
    }
    public void printStats(){
        System.out.println("phases: " + this.getPhases());
        System.out.println("reads/writes: " + this.getReads() + "/" + this.getWrites() );
        System.out.println(this.getRecordsAmount() + " records in " + this.getRunsAmount() + " initial runs");
    }
}

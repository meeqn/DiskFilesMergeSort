package org.DFMS.Data.Buffering.Storing;

import lombok.Getter;

public class Record implements Comparable<Record>{

    @Getter
    private final Integer sortVal;
    @Getter
    private final Integer val; //can later change back to primitive, after test
    public Record(int val){
        this.val = val;
        this.sortVal = this.calculateSortVal();
    }
    private Integer calculateSortVal(){
        int tmpSortVal = 1;
        for(int i=2; i<this.val; i++){
            if(this.val%i==0){
                tmpSortVal += i;
            }
        }
        return tmpSortVal;
    }
    @Override
    public int compareTo(Record o) {
        if(o == null){
            return 1;
        }
        return this.val.compareTo(o.getVal());
    }
}

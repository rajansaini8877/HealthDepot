package com.myappcompany.rajan.healthdepot.model;

import java.util.Comparator;

public class RecordComparator implements Comparator<RecordItem> {
    @Override
    public int compare(RecordItem o1, RecordItem o2) {
        return o2.getTimestamp().compareTo(o1.getTimestamp());
    }
}

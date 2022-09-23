package me.pulsi_.bankplus.shinyfeatures;

import java.util.Comparator;

public class BankComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        Long l1 = Long.parseLong(o1.split(":")[1]);
        Long l2 = Long.parseLong(o2.split(":")[1]);
        return l1.compareTo(l2);
    }
}

package edu.eci.arsw.blacklistvalidator;

import java.lang.Math;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class SearchBlackList extends Thread {
    private AtomicInteger ocurrencesCount = new AtomicInteger(0);
    private AtomicInteger checkedListsCount = new AtomicInteger(0);
    private LinkedList<Integer> blackListOcurrences = new LinkedList<>();
    private String ipaddress;
    private int min;
    private int max;
    private HostBlacklistsDataSourceFacade skds;

    SearchBlackList(String ipaddress, int min, int max, HostBlacklistsDataSourceFacade skds,
            AtomicInteger ocurrencesCount, AtomicInteger checkedListsCount) {
        this.min = min;
        this.max = max;
        this.ipaddress = ipaddress;
        this.skds = skds;
        this.checkedListsCount = checkedListsCount;
        this.ocurrencesCount = ocurrencesCount;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public HostBlacklistsDataSourceFacade getSkds() {
        return skds;
    }

    public int getCheckedListsCount() {
        return checkedListsCount.intValue();
    }

    public LinkedList<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }

    public int getOcurrencesCount() {
        return ocurrencesCount.intValue();
    }

    public void run() {
        for (int i = min; i < max && ocurrencesCount.get() < 5; i++) {
            if (isInterrupted())
                break;
            checkedListsCount.incrementAndGet();

            if (skds.isInBlackListServer(i, ipaddress)) {

                blackListOcurrences.add(i);
                ocurrencesCount.incrementAndGet();
            }

        }
    }

}
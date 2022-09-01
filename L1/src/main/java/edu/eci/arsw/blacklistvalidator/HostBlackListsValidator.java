/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.atomic.*;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private AtomicInteger ocurrencesCount = new AtomicInteger(0);
    private List<Integer> blackListOcurrences = new LinkedList<>();
    private AtomicInteger checkedListsCount = new AtomicInteger(0);

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * 
     * @param ipaddress suspicious host's IP address.
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress) {

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        for (int i = 0; i < skds.getRegisteredServersCount() && ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT; i++) {
            checkedListsCount.incrementAndGet();

            if (skds.isInBlackListServer(i, ipaddress)) {

                blackListOcurrences.add(i);
                ocurrencesCount.incrementAndGet();
            }
        }

        if (ocurrencesCount.get() >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
                new Object[] { checkedListsCount, skds.getRegisteredServersCount() });

        return blackListOcurrences;
    }

    public List<Integer> checkHost(String ipaddress, int cores) {

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        int serverAmount = skds.getRegisteredServersCount();
        int groups = (int) Math.ceil((serverAmount / cores));
        SearchBlackList[] threads = new SearchBlackList[cores];
        for (int i = 0; i < cores; i++) {
            threads[i] = new SearchBlackList(ipaddress, i * groups,
                    ((i + 1) * groups) > serverAmount ? serverAmount : (i + 1) * groups, skds, ocurrencesCount,
                    checkedListsCount);
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            threads[i].start();
        }

        while (checkedListsCount.get() < serverAmount && ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT) {

        }
        for (int i = 0; i < cores; i++) {
            blackListOcurrences.addAll(threads[i].getBlackListOcurrences());
        }
        if (ocurrencesCount.get() >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
                new Object[] { checkedListsCount, skds.getRegisteredServersCount() });

        long fin = System.currentTimeMillis();
        System.out.println("Hilos: " + cores + "   Tiempo duracion: " + (-start + fin));
        return blackListOcurrences;
    }

    public int getocurrencesCount() {
        return ocurrencesCount.intValue();
    }

    public int getcheckedlistCount() {
        return checkedListsCount.intValue();
    }

    public List<Integer> getblackListOcurrences() {
        return blackListOcurrences;
    }

    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

}
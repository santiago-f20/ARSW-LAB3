package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;

public class SearchBlackList extends Thread {

    private int inf;
    private int sup;
    private String host;
    private HostBlacklistsDataSourceFacade skds;
    private int countBlackList;
    private int listCount;
    private LinkedList<Integer> blackListOcurrences = new LinkedList<>();

    /**
     * Constructor
     * 
     * @param a
     * @param b
     * @param ip
     * @param blackList
     */
    public SearchBlackList(int a, int b, String ip, HostBlacklistsDataSourceFacade blackList) {
        this.inf = a;
        this.sup = b;
        this.host = ip;
        this.skds = blackList;
    }

    /**
     * Funcion que revisa en la lista negra de servidores si la ip ingresada está.
     * Si la direccion ip está, se agrega en una LinkedList y sube el contador de
     * listas negras.
     */
    public void run() {
        for (int ip = this.inf; ip <= this.sup; ip++) {
            this.listCount++;
            if (this.skds.isInBlackListServer(ip, this.host)) {
                this.blackListOcurrences.add(ip);
                this.countBlackList++;
            }
        }
    }

    /**
     * Retorna la cantidad de veces que el host ha sido encontrado en listas negras
     * 
     * @return
     */
    public int timesFound() {
        return this.countBlackList;
    }

    /**
     * Retorna la lista de posiciones de las listas negras donde apareció la
     * direccion
     * 
     * @return -> linkedList<Integer>
     */
    public LinkedList<Integer> getBlackList() {
        return this.blackListOcurrences;
    }

    /**
     * Retorna la cantidad de listas que fueron revisadas
     * 
     * @return -> cantidad de listas (int)
     */
    public int getListCount() {
        return this.listCount;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author hcadavid
 */
public class CountThread extends Thread {
    private int A;
    private int B;

    public CountThread(int A, int B) {
        this.A = A;
        this.B = B;
    }

    public void run() {
        while (A < B) {
            System.out.println(A);
            A++;
        }
    }
}

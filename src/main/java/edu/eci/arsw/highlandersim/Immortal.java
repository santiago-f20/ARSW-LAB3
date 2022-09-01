package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;

    private AtomicInteger health = new AtomicInteger();

    private Object block;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private int posi;
    private boolean live = true;
    private boolean pausa = false;

    private final Random r = new Random(System.currentTimeMillis());

    public Immortal(String name, List<Immortal> immortalsPopulation, int h, int defaultDamageValue,
            ImmortalUpdateReportCallback ucb, Object block, int n) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        health.set(h);
        this.defaultDamageValue = defaultDamageValue;
        this.block = block;
        this.posi = n;
    }

    public void run() {

        while (live) {
            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            // avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            if (im.isAlive()) {
                this.fight(im);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (pausa) {
                synchronized (block) {
                    try {
                        block.wait();
                        pausa = false;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    public void fight(Immortal i2) {
        Immortal i1 = this;

        if (i1.getPosi() > i2.getPosi()) {
            Immortal temp = i1;
            i1 = i2;
            i2 = temp;
        }

        synchronized (i1) {
            synchronized (i2) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    health.addAndGet(defaultDamageValue);

                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }

    }

    public void changeHealth(int v) {
        health.set(v);
        if (health.get() == 0) {
            live = false;
        }
    }

    public int getHealth() {
        return health.get();
    }

    public void pausa() {
        pausa = true;
    }

    public void kill() {
        live = false;
    }

    public boolean getlive() {
        return live;
    }

    public int getPosi() {
        return posi;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
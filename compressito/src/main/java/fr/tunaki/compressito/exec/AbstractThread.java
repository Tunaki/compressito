package fr.tunaki.compressito.exec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * This abstract class represents a thread that has listener features. It can be used to notify listeners when the runnable thread has
 * started or has completed.
 * <p>
 * This class implements the Observer pattern.
 * @author gboue
 */
public abstract class AbstractThread extends Thread {

    private List<Runnable> startRunnables = Collections.synchronizedList(new ArrayList<Runnable>());
    private List<Runnable> stopRunnables = Collections.synchronizedList(new ArrayList<Runnable>());

    /**
     * Adds a runnable instance to the list of process that will be run when this thread starts.
     * @param runnable The runnable to add.
     */
    public void addStartRunnable(Runnable runnable) {
        startRunnables.add(runnable);
    }

    /**
     * Adds a runnable instance to the list of process that will be run when this thread stops in success.
     * @param runnable The runnable to add.
     */
    public void addStopRunnable(Runnable runnable) {
        stopRunnables.add(runnable);
    }

    @Override
    public void run() {
        notifyRunnables(startRunnables);
        doRun();
        notifyRunnables(stopRunnables);
    }

    /**
     * Executes the job of this thread. This method follows the same pattern as {@link Thread#run()}
     */
    public abstract void doRun();

    private void notifyRunnables(List<Runnable> runnables) {
        synchronized (runnables) {
            for (Runnable runnable : runnables) {
                SwingUtilities.invokeLater(runnable);
            }
        }
    }

}
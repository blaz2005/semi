import javax.swing.*;
import java.util.Map;

public class EventTimer extends SwingWorker<Void, Void> {
    private int selectedIndex;
    private Map<Integer, Integer> timeLeftMap;
    private EventManager eventManager;

    public EventTimer(int selectedIndex, Map<Integer, Integer> timeLeftMap, EventManager eventManager) {
        this.selectedIndex = selectedIndex;
        this.timeLeftMap = timeLeftMap;
        this.eventManager = eventManager;
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (timeLeftMap.containsKey(selectedIndex) && timeLeftMap.get(selectedIndex) >= 0) {
            publish();
            Thread.sleep(1000);
            timeLeftMap.put(selectedIndex, timeLeftMap.get(selectedIndex) - 1);
        }
        return null;
    }

    @Override
    protected void process(java.util.List<Void> chunks) {
        eventManager.updateTimerDisplay(selectedIndex);
    }

    @Override
    protected void done() {
        timeLeftMap.remove(selectedIndex);
        if (timeLeftMap.isEmpty()) {
            eventManager.timerRunning = false;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    eventManager.startStopButton.setText("Start Timer");
                }
            });
        }
    }
}

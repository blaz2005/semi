import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class EventManager extends JFrame {
    private JTextField dogodekTextField;
    private JTextField casTextField;
    private JButton addButton;
    JButton startStopButton;
    private JButton deleteButton;
    private EventListModel eventsListModel;
    private JList<String> eventsList;
    private EventListModel timesListModel;
    private JList<String> timesList;

    private JComboBox<String> timeUnitComboBox;  // dropdown meni za izbiro ur, minut ali sekund

    boolean timerRunning;
    private Map<Integer, Integer> timeLeftMap;

    public EventManager() {
        setTitle("Upravitelj dogodkov");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dogodekTextField = new JTextField();
        casTextField = new JTextField();
        addButton = new JButton("Dodaj");
        startStopButton = new JButton("Začni štoparico");
        deleteButton = new JButton("Izbriši");
        eventsListModel = new EventListModel();
        eventsList = new JList<>(eventsListModel);
        eventsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        timesListModel = new EventListModel();
        timesList = new JList<>(timesListModel);
        timesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        timerRunning = false;
        timeLeftMap = new HashMap<>();

        // Nastavitev padajočega menija
        String[] timeUnits = {"Ure", "Minute", "Sekunde"};
        timeUnitComboBox = new JComboBox<>(timeUnits);

        addButton.addActionListener(new AddButtonListener());
        startStopButton.addActionListener(new StartStopButtonListener());
        deleteButton.addActionListener(new DeleteButtonListener());

        eventsList.addListSelectionListener(event -> {
            int[] selected = eventsList.getSelectedIndices();
            for (int i : selected) {
                timesList.setSelectedIndices(selected);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));
        panel.add(new JLabel("Dogodek:"));
        panel.add(dogodekTextField);
        panel.add(new JLabel("Čas:"));
        panel.add(casTextField);
        panel.add(timeUnitComboBox);
        panel.add(addButton);

        add(panel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1, 2));

        listPanel.add(new JScrollPane(eventsList));
        listPanel.add(new JScrollPane(timesList));

        add(listPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startStopButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void startTimer() {
        int[] selectedIndices = eventsList.getSelectedIndices();
        for (int selectedIndex : selectedIndices) {
            int timeLeft = Integer.parseInt(timesListModel.getElementAt(selectedIndex).split(":")[0]) * 3600
                    + Integer.parseInt(timesListModel.getElementAt(selectedIndex).split(":")[1]) * 60
                    + Integer.parseInt(timesListModel.getElementAt(selectedIndex).split(":")[2]);
            timeLeftMap.put(selectedIndex, timeLeft);

            EventTimer eventTimer = new EventTimer(selectedIndex, timeLeftMap, this);
            eventTimer.execute();
        }

        timerRunning = true;
        startStopButton.setText("Ustavi štoparico");
        addButton.setEnabled(false); //  gumb za dodajanje med štoparico
    }

    void updateTimerDisplay(int selectedIndex) {
        if (timeLeftMap.containsKey(selectedIndex)) {
            int totalSeconds = timeLeftMap.get(selectedIndex);

            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            timesListModel.setElementAt(String.format("%02d:%02d:%02d", hours, minutes, seconds), selectedIndex);
        }
    }

    private void stopTimer() {
        timeLeftMap.clear();
        timerRunning = false;
        startStopButton.setText("Začni štoparico");
        addButton.setEnabled(true); //  gumb za dodajanje po ustavitvi štoparice
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String dogodekText = dogodekTextField.getText();
            String casText = casTextField.getText();
    
            if (!dogodekText.isEmpty() && !casText.isEmpty() && timeUnitComboBox.getSelectedItem() != null) {
                String timeUnit = (String) timeUnitComboBox.getSelectedItem();
                int timeInSeconds = Integer.parseInt(casText);
    
                if (timeUnit.equals("Minute")) {
                    timeInSeconds *= 60;
                } else if (timeUnit.equals("Ure")) {
                    timeInSeconds *= 3600;
                }
    
                eventsListModel.addElement(dogodekText);
                timesListModel.addElement(formatTime(timeInSeconds));
                dogodekTextField.setText("");
                casTextField.setText("");
            }
        }
    }
    

    private class StartStopButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!timerRunning) {
                startTimer();
            } else {
                stopTimer();
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] selected = eventsList.getSelectedIndices();
            for (int i = selected.length - 1; i >= 0; i--) {
                eventsListModel.removeElementAt(selected[i]);
                timesListModel.removeElementAt(selected[i]);
                timeLeftMap.remove(selected[i]);
            }
        }
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EventManager().setVisible(true);
            }
        });
    }
}

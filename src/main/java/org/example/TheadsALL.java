package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Hashtable;

public class TheadsALL extends JFrame {
    private JSlider slider;
    private JSpinner threadPriorityField1;
    private JSpinner threadPriorityField2;
    private JButton startThreadsBtn;
    private JButton start1;
    private JButton start2;
    private JButton stop1;
    private JButton stop2;
    private Label statusLabel;
    private final GUI GUI = new GUI();
    BlockingVariableThreadFactory blockingVariableThreadFactory = new BlockingVariableThreadFactory();
    private final TreadsB taskAThreadCompetition;
    private final TreadsB taskBThreadCompetition;

    public TheadsALL() {
        super(" ");
        this.taskAThreadCompetition = new SimpleTreadsB();
        this.taskBThreadCompetition = new SimpleTreadsB();
        setSize(640, 480);
        setMinimumSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initGuiManager();
        initializeTopPane();
        initializeCenterPane();

        add(BTAsk(), BorderLayout.SOUTH);

        centralizeFrame();
        setVisible(true);
    }

    void initializeTopPane() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        slider = initializeSlider();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = getWidth();
                int newSliderWidth = frameWidth - 2 * 50;

                slider.setPreferredSize(new Dimension(newSliderWidth, slider.getPreferredSize().height));
                topPanel.revalidate();
            }
        });
        topPanel.add(slider);
        add(topPanel, BorderLayout.NORTH);
    }

    private void centralizeFrame() {
        setLocationRelativeTo(null);
    }

    private JSlider initializeSlider() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100 ,50);
        slider.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 0; i <= 100; i += 10) {
            labelTable.put(i, new JLabel(String.valueOf(i)));
        }
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);

        return slider;
    }

    void initializeCenterPane() {
        JPanel centerPane = new JPanel(new GridBagLayout());

        threadPriorityField1 = initPriorityField();
        threadPriorityField1.addChangeListener(e -> taskAThreadCompetition.changePriority(SimpleTreadsB.UPPER, (int)threadPriorityField1.getValue()));
        threadPriorityField2 = initPriorityField();
        threadPriorityField2.addChangeListener(e -> taskAThreadCompetition.changePriority(SimpleTreadsB.LOWER, (int)threadPriorityField2.getValue()));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        statusLabel = new Label(" ");
        statusLabel.setPreferredSize(new Dimension(215, 30));
        statusLabel.setAlignment(Label.CENTER);
        centerPane.add(statusLabel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPane.add(threadPriorityField1, gbc);

        gbc.gridx = 1;
        centerPane.add(threadPriorityField2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        startThreadsBtn = initStartButton();
        centerPane.add(startThreadsBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton stopAllThreadsBtn = initStopBtn();

        centerPane.add(stopAllThreadsBtn, gbc);

        add(centerPane, BorderLayout.CENTER);
    }

    private JButton initStartButton() {
        JButton button = new JButton("Start");
        button.setPreferredSize(new Dimension(180, 70));

        button.addActionListener(e -> {
            if (taskAThreadCompetition.isRunning()) {
                return;
            }
            setTaskBEnabled(false);
            setTaskAEnabled(false);

            taskAThreadCompetition.setThread(SimpleTreadsB.UPPER, getThread1());
            taskAThreadCompetition.setThread(SimpleTreadsB.LOWER, getThread2());
            taskAThreadCompetition.startAll();

        });
        return button;
    }

    private void setTaskBEnabled(boolean b) {
        start1.setEnabled(b);
        start2.setEnabled(b);
        stop1.setEnabled(b);
        stop2.setEnabled(b);
    }
    private void setTaskAEnabled(boolean b) {
        startThreadsBtn.setEnabled(b);
    }

    private JButton initStopBtn() {
        JButton button = new JButton("Stop");
        button.setPreferredSize(new Dimension(180, 70));

        button.addActionListener(e -> {

            taskAThreadCompetition.stopAll();
            taskBThreadCompetition.stopAll();

            setTaskBEnabled(true);
            setTaskAEnabled(true);

            statusLabel.setText("");

        });
        return button;
    }

    private Thread getThread1() {
        SynchronizedSliderMoveThreadsFactory factory = new SynchronizedSliderMoveThreadsFactory();
        return factory.getUpperThread( 10, (int) threadPriorityField1.getValue());
    }
    private Thread getThread2() {
        SynchronizedSliderMoveThreadsFactory factory = new SynchronizedSliderMoveThreadsFactory();
        return factory.getLowerThread(90, (int) threadPriorityField2.getValue());
    }

    private JSpinner initPriorityField() {
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 10, 1);
        Dimension preferredSize = new Dimension(100, 40);
        JSpinner jSpinner = new JSpinner(model);
        jSpinner.setPreferredSize(preferredSize);
        return jSpinner;
    }

    private JPanel BTAsk() {

        start1 = new JButton("Start 1");
        start1.addActionListener(e -> runUpperThread());

        start2 = new JButton("Start 2");
        start2.addActionListener(e -> runLowerThread());

        stop1 = new JButton("Stop 1");
        stop1.addActionListener(e -> stopUpperThread());

        stop2 = new JButton("Stop 2");
        stop2.addActionListener(e -> stopLowerThread());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(start1);
        panel.add(start2);
        panel.add(stop1);
        panel.add(stop2);

        return panel;
    }

    private void runUpperThread() {
        Thread upperThread = blockingVariableThreadFactory.getUpperThread(10, Thread.MIN_PRIORITY);
        taskBThreadCompetition.setThread(SimpleTreadsB.UPPER, upperThread);
        start1.setEnabled(false);
        stop2.setEnabled(false);
        setTaskAEnabled(false);
        taskBThreadCompetition.start(SimpleTreadsB.UPPER);

    }

    private void runLowerThread() {
        Thread lowerThread = blockingVariableThreadFactory.getLowerThread(90, Thread.MAX_PRIORITY);
        taskBThreadCompetition.setThread(SimpleTreadsB.LOWER, lowerThread);
        start2.setEnabled(false);
        stop1.setEnabled(false);
        setTaskAEnabled(false);
        taskBThreadCompetition.start(SimpleTreadsB.LOWER);
    }

    private void stopUpperThread() {
        taskBThreadCompetition.stop(SimpleTreadsB.UPPER);
    }
    private void stopLowerThread() {
        taskBThreadCompetition.stop(SimpleTreadsB.LOWER);
    }

    private void initGuiManager() {
        GUI.setOnUpperFinished(()-> {
            start1.setEnabled(true);
            stop2.setEnabled(true);

        });
        GUI.setOnLowerFinished(()-> {
            start2.setEnabled(true);
            stop1.setEnabled(true);

        });
        GUI.setOnCompetitionChange(()-> statusLabel.setText(""));
    }
    private class SynchronizedSliderMoveThreadsFactory implements Priority {
        private static final Object obj = new Object();

        private Thread getThread(int target) {
            return new Thread(()->{
                SliderMover sliderMover = new SliderMover();
                while (true) {

                    synchronized (obj) {
                        GUI.onSliderMove();
                        if(sliderMover.moveSliderTowards(target))
                            break;
                    }
                }
            });
        }
        public Thread getUpperThread(int target, int priority) {
            Thread thread = getThread(target);
            thread.setPriority(priority);
            thread.setDaemon(true);
            thread.setName("Thread 1");
            return thread;
        }
        public Thread getLowerThread(int target, int priority) {
            Thread thread = getThread(target);
            thread.setPriority(priority);
            thread.setDaemon(true);
            thread.setName("Thread 2");
            return thread;
        }
    }
    private class BlockingVariableThreadFactory implements Priority {
        public static int semaphore = 1;

        synchronized boolean acquireResource() {
            if (semaphore == 0) {
                return false;
            }
            semaphore--;
            return true;
        }

        synchronized void releaseResource() {
            if (semaphore < 1) {
                semaphore++;
            } else {
                throw new RuntimeException("Semaphore error: no resource to release");
            }
        }

        public Thread getUpperThread(int target, int priority) {
            Thread thread = createThread(target, GUI::onUpperFinished);
            thread.setPriority(priority);
            thread.setDaemon(true);
            thread.setName("Thread 1");
            return thread;
        }
        public Thread getLowerThread(int target, int priority) {
            Thread thread = createThread(target, GUI::onLowerFinished);
            thread.setDaemon(true);
            thread.setPriority(priority);
            thread.setName("Thread 2");
            return thread;
        }

        private Thread createThread(int target, Runnable onComplete) {
            return new Thread(()->{
                if(!acquireResource()) {
                    onComplete.run();

                    return;
                }
                GUI.onSliderMove();
                try {
                    SliderMover sliderMover = new SliderMover();
                    while (sliderMover.canMoveTowards(target)) {
                        if(sliderMover.moveSliderTowards(target))
                            break;
                    }
                } finally {
                    releaseResource();
                    onComplete.run();
                }
            });
        }
    }

    private class SliderMover {
        private static final int DELAY = 100;


        public boolean canMoveTowards(int target) {
            return slider.getValue() != target;
        }

        public boolean moveSliderTowards(int target) {
            if (Thread.interrupted()) {
                return true;
            }
            int prevValue = slider.getValue();

            if (prevValue < target) {
                slider.setValue(prevValue + 1);
            } else if (prevValue > target) {
                slider.setValue(prevValue - 1);
            }
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException ignored) {
                return true;
            }
            return false;
        }
    }


}

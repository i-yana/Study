package ru.nsu.ccfit.zharkova.GUI;

import ru.nsu.ccfit.zharkova.Factory.API;
import ru.nsu.ccfit.zharkova.Factory.System.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Yana on 25.04.15.
 */
public class GUIClient extends JFrame implements Runnable {

    private static final String CLASS_NAME_TAG = "GUI";

    private int DELAY_UPDATING_GUI_DATA = 1;
    private int MAX_DELAY_TIME = 5000;
    private int TIME_OF_LOADING = 500;

    private JSlider accessoryDeliverSlider;
    private JSlider bodyDeliverSlider;
    private JSlider motorDeliverSlider;
    private JSlider workerCreateDelaySlider;
    private JSlider carBoughtDelaySlider;

    private JLabel accessoryStorageValueLabel;
    private JLabel bodyStorageValueLabel;
    private JLabel motorStorageValueLabel;
    private JLabel carStorageValueLabel;
    private JLabel soldCarLabel;
    private JLabel taskForExecution;

    private JTextField accessoryDeliverTextField;
    private JTextField bodyDeliverTextField;
    private JTextField motorDeliverTextField;
    private JTextField workerCreateDelayTextField;
    private JTextField carBoughtDelayTextField;

    private JProgressBar accessoryStorageProgressBar;
    private JProgressBar bodyStoragePrigressBar;
    private JProgressBar motorStorageProgressBar;
    private JProgressBar carStorageProgressBar;


    private Thread intefaceUpdateThread;
    private String factoryName;

    private static Logger logger = Logger.getLogger(CLASS_NAME_TAG);

    public GUIClient(String factoryName) {

        super(factoryName);
        this.factoryName = factoryName;
        API.Instance(factoryName).start();

        addDelaySliders();
        addStorageLabels();
        addClosingEvent();

        setPreferredSize(new Dimension(600, 300));
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        printIntoLogger("initialize");
        intefaceUpdateThread = new Thread(this);
        intefaceUpdateThread.start();
        logger.debug("updating start");
    }

    private void addStorageLabels() {

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());

        accessoryStorageValueLabel = new JLabel(String.valueOf(API.Instance(factoryName).getAccessoryStorage().currentSize()));
        bodyStorageValueLabel = new JLabel(String.valueOf(API.Instance(factoryName).getBodyStorage().currentSize()));
        motorStorageValueLabel = new JLabel(String.valueOf(API.Instance(factoryName).getMotorStorage().currentSize()));
        carStorageValueLabel = new JLabel(String.valueOf(API.Instance(factoryName).getCarStorage().currentSize()));
        soldCarLabel = new JLabel(String.valueOf(API.Instance(factoryName).getDealers().getStatistic().getCount()));
        taskForExecution = new JLabel(String.valueOf(API.Instance(factoryName).getWorkerPool().getThreadPool().getTaskQuantity()));

        accessoryStorageProgressBar = createStorageProgressBar(API.Instance(factoryName).getAccessoryStorage().maxCapacity());
        bodyStoragePrigressBar = createStorageProgressBar(API.Instance(factoryName).getBodyStorage().maxCapacity());
        motorStorageProgressBar = createStorageProgressBar(API.Instance(factoryName).getMotorStorage().maxCapacity());
        carStorageProgressBar = createStorageProgressBar(API.Instance(factoryName).getCarStorage().maxCapacity());

        JPanel accessoryStoragePanel = createOneStorageTitle(accessoryStorageValueLabel, "Accesory Unit");
        JPanel bodyStoragePanel = createOneStorageTitle(bodyStorageValueLabel, "Body Unit");
        JPanel motorStoragePanel = createOneStorageTitle(motorStorageValueLabel, "Motor Unit");
        JPanel carStoragePanel = createOneStorageTitle(carStorageValueLabel, "Car Unit");
        JPanel soldCarPanel = createOneStorageTitle(soldCarLabel, "Sold Cars");
        JPanel taskForExecutionPanel = createOneStorageTitle(taskForExecution, "Task for the execution");

        addItem(panel1, accessoryStoragePanel, 0, 0, 1, 1, GridBagConstraints.EAST);
        addItem(panel1, bodyStoragePanel, 0, 1, 1, 1, GridBagConstraints.EAST);
        addItem(panel1, motorStoragePanel, 0, 2, 1, 1, GridBagConstraints.EAST);
        addItem(panel1, carStoragePanel, 0, 3, 1, 1, GridBagConstraints.EAST);
        addItem(panel1, soldCarPanel, 2, 0, 1, 1, GridBagConstraints.EAST);
        addItem(panel1, taskForExecutionPanel, 2, 1, 1, 1, GridBagConstraints.EAST);


        addItem(panel1, accessoryStorageProgressBar, 1, 0, 2, 1, GridBagConstraints.WEST);
        addItem(panel1, bodyStoragePrigressBar, 1, 1, 1, 1, GridBagConstraints.WEST);
        addItem(panel1, motorStorageProgressBar, 1, 2, 2, 1, GridBagConstraints.WEST);
        addItem(panel1, carStorageProgressBar, 1, 3, 2, 1, GridBagConstraints.WEST);

        add(panel1);
    }

    private void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = width;
        gc.gridheight = height;
        gc.weightx = 100.0;
        gc.weighty = 100.0;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = align;
        gc.fill = GridBagConstraints.NONE;
        p.add(c, gc);
    }

    private void addDelaySliders(){
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

        accessoryDeliverTextField = new JTextField();
        bodyDeliverTextField = new JTextField();
        motorDeliverTextField = new JTextField();
        workerCreateDelayTextField = new JTextField();
        carBoughtDelayTextField = new JTextField();

        accessoryDeliverSlider = new JSlider();
        bodyDeliverSlider = new JSlider();
        motorDeliverSlider = new JSlider();
        workerCreateDelaySlider = new JSlider();
        carBoughtDelaySlider = new JSlider();

        JPanel accesSlider = createOneSliderPanel(accessoryDeliverSlider, accessoryDeliverTextField, "Accesory Delay", API.getAccessorySuppliersDefaultDelay());
        JPanel bodySlider = createOneSliderPanel(bodyDeliverSlider, bodyDeliverTextField, "Body Delay", API.getBodySuppliersDefaultDelay());
        JPanel motorSlider = createOneSliderPanel(motorDeliverSlider, motorDeliverTextField, "Motor Delay", API.getMotorSuppliersDefaultDelay());
        JPanel workerSlider = createOneSliderPanel(workerCreateDelaySlider, workerCreateDelayTextField, "Time of build", API.getWorkersDefaultDelay());
        JPanel dealerSlider = createOneSliderPanel(carBoughtDelaySlider, carBoughtDelayTextField, "Time of sale", API.getDealersDefaultDelay());

        sliderPanel.add(accesSlider);
        sliderPanel.add(bodySlider);
        sliderPanel.add(motorSlider);
        sliderPanel.add(workerSlider);
        sliderPanel.add(dealerSlider);
        add(sliderPanel, BorderLayout.NORTH);
    }
    private void addClosingEvent(){
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                API.Instance(factoryName).stop();
                intefaceUpdateThread.interrupt();
                Thread.currentThread().interrupt();
                if(API.getFactories().isEmpty()) {
                    System.exit(0);
                }
            }
        };
        this.addWindowListener(exitListener);
    }
    private JPanel createOneStorageTitle(JLabel storageLabel, String storageName){
        JPanel storagePanel = new JPanel(new FlowLayout());
        JLabel storageNameLabel = new JLabel(storageName);

        storagePanel.add(storageNameLabel);
        storagePanel.add(storageLabel);

        return storagePanel;
    }

    private JProgressBar createStorageProgressBar(int storageCapacity){
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(storageCapacity);
        progressBar.setValue(0);
        return progressBar;
    }

    private JPanel createOneSliderPanel(final JSlider slider, final JTextField textField, String handlerName, long currentQuantity){
        JPanel sliderPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(handlerName);
        textField.setText(String.valueOf(currentQuantity));
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int newDelay;
                if (key == KeyEvent.VK_ENTER){
                    try {
                        newDelay = Integer.parseInt(textField.getText());
                        changeDelay(slider, textField, newDelay);
                    }
                    catch (NumberFormatException ignored){}
                }
            }
        });
        slider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                changeDelay(slider, textField, slider.getValue());
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        label.setHorizontalAlignment(JLabel.CENTER);
        sliderPanel.add(textField, BorderLayout.SOUTH);
        createDelaySlider(slider, currentQuantity);
        sliderPanel.add(label, BorderLayout.NORTH);
        sliderPanel.add(slider, BorderLayout.CENTER);
        return sliderPanel;
    }

    private void changeDelay(JSlider slider, JTextField textField, int newDelay) {

        if (slider == accessoryDeliverSlider) {
            slider.setValue(newDelay);
            textField.setText(String.valueOf(newDelay));
            API.Instance(factoryName).getAccessorySupplier().setDelay(newDelay);
            printIntoLogger("Accessory Deliver: "+ accessoryDeliverSlider.getValue()+" ms");
            return;
        }
        if (slider == bodyDeliverSlider) {
            slider.setValue(newDelay);
            textField.setText(String.valueOf(newDelay));
            API.Instance(factoryName).getBodySupplier().setDelay(newDelay);
            printIntoLogger("Body Deliver: "+ bodyDeliverSlider.getValue()+" ms");
            return;
        }
        if (slider == motorDeliverSlider) {
            slider.setValue(newDelay);
            textField.setText(String.valueOf(newDelay));
            API.Instance(factoryName).getMotorSupplier().setDelay(newDelay);
            printIntoLogger("Motor Deliver: "+ motorDeliverSlider.getValue()+" ms");
            return;
        }

        if (slider == workerCreateDelaySlider) {
            slider.setValue(newDelay);
            textField.setText(String.valueOf(newDelay));
            API.Instance(factoryName).getWorkerPool().setDelay(newDelay);
            printIntoLogger("Worker Deliver: "+ workerCreateDelaySlider.getValue()+" ms");
            return;
        }

        if (slider == carBoughtDelaySlider) {
            slider.setValue(newDelay);
            textField.setText(String.valueOf(newDelay));
            API.Instance(factoryName).getDealers().setDelay(newDelay);
            printIntoLogger("Dealer Deliver: " + carBoughtDelaySlider.getValue() + " ms");
        }
    }

    private void printIntoLogger(String msg){
        if(API.Instance(factoryName).getParser().isLogSale()){
            logger.info(msg);
        }
    }
    private JSlider createDelaySlider(JSlider slider, long delay) {
        slider.setMaximum(MAX_DELAY_TIME);
        slider.setValue((int) delay);
        slider.setMajorTickSpacing(MAX_DELAY_TIME / 2);
        slider.setMinorTickSpacing(MAX_DELAY_TIME / 10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(TIME_OF_LOADING);
            while (Thread.currentThread().isAlive()) {
                Thread.sleep(DELAY_UPDATING_GUI_DATA);
                accessoryStorageValueLabel.setText(String.valueOf(API.Instance(factoryName).getAccessoryStorage().getAllQuantity()));
                accessoryStorageProgressBar.setValue(API.Instance(factoryName).getAccessoryStorage().currentSize());
                bodyStorageValueLabel.setText(String.valueOf(API.Instance(factoryName).getBodyStorage().getAllQuantity()));
                bodyStoragePrigressBar.setValue(API.Instance(factoryName).getBodyStorage().currentSize());
                motorStorageValueLabel.setText(String.valueOf(API.Instance(factoryName).getMotorStorage().getAllQuantity()));
                motorStorageProgressBar.setValue(API.Instance(factoryName).getMotorStorage().currentSize());
                carStorageValueLabel.setText(String.valueOf(API.Instance(factoryName).getCarStorage().getAllQuantity()));
                carStorageProgressBar.setValue(API.Instance(factoryName).getCarStorage().currentSize());
                soldCarLabel.setText(String.valueOf(API.Instance(factoryName).getDealers().getStatistic().getCount()));
                taskForExecution.setText(String.valueOf(API.Instance(factoryName).getWorkerPool().getThreadPool().getTaskQuantity()));
            }
        } catch (InterruptedException e) {
            logger.debug("End update GUI thread");
        } catch (NullPointerException ignored){}
    }

}


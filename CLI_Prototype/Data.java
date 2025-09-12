import java.util.ArrayList;

public class Data  {
    private ArrayList<Double> tempArray = new ArrayList<>();
    private ArrayList<Double> pressureArray = new ArrayList<>();
    private volatile double temp;
    private volatile double pressure;
    private boolean motorOn;
    private int highTemp = 25;
    private int highPressure = 25;
    private volatile boolean paused = false;
    private volatile boolean running = true;
    private Thread printerThread;

public Data(){
    //Data is hardcoded for prototype sake
    this.temp = 17.0; 
    this.pressure = 12.0;
    this.motorOn = false;
}

public double getTemp(){
    return temp;
}

public double getPressure(){
    return pressure;
}

public boolean getMotorOn(){
    return motorOn;
}

public void update(){ //Simulates machine fluctuation
    temp += (Math.random() - 0.1);
    pressure += (Math.random() - 0.1) * 0.5;

    if (motorOn){
        temp -= 0.5;
    }
    tempArray.add(temp);
    pressureArray.add(pressure);
}


public void setMotorOn(boolean motorOn){
    this.motorOn = motorOn;
}

public boolean turnOnMotor(){
    motorOn = true;
    return motorOn;
}

public boolean turnOffMotor(){
    if (motorOn == true) {
        motorOn = false;
    } else {
        System.out.println("Motor is already off");
    }
    return motorOn;
}

public String toString(){
    return "Temp: " + temp + " C. Pressure: " + pressure + " PSI. Motor Status: " + motorOn;
}

public void startPrinting() {
    running = true;
    printerThread = new Thread(() -> { //Thread designed to update data every second
        while (running) {
            if (!paused) {
                update();
                tempWarning();
                pressureWarning();
                System.out.println(toString());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    });
    printerThread.start();
}

public void pausePrinting() {
    paused = true;
}

public void resumePrinting() {
    paused = false;
}

public void stopPrinting() {
    running = false;
    if (printerThread != null) {
        printerThread.interrupt();
    }
}
public void tempWarning() {
    if (temp >= highTemp){
        System.out.println("*HIGH TEMP WARNING* *SHUTTING DOWN SYSTEM");
        stopPrinting();
    }
}

public void pressureWarning() {
    if (pressure >= highPressure){
        System.out.println("*HIGH PRESSURE WARNING* SHUTTING DOWN SYSTEM");
        stopPrinting();
    }
}

public void report(){ //fix
    System.out.println("Here is a list of your reported values so far:\n TEMPERATURE:");
    for (int i = 0; i < tempArray.size(); i++ ){
        double value = tempArray.get(i);
        System.out.println(value + "C");
    }
    System.out.println("PRESSURE:");
    for (int j = 0; j < pressureArray.size(); j++){
            double value = pressureArray.get(j);
            System.out.println(value + "PSI");
        }
}

}

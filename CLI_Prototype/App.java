import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Data data1 = new Data();
        Scanner scanner = new Scanner(System.in);

        data1.turnOnMotor(); //Motor starts as on
        System.out.println("Type 'p' to pause, 'r' to resume, 'q' to exit:");
        data1.startPrinting(); //Begin printing thread. 1 second in between each print

        while (true) { //Loop that stays alert for user input 
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("p")) {
                data1.pausePrinting();
                System.out.println("Printing paused. (type report for last 10 values.)");
                String motorChange = scanner.nextLine(); //New scanner meant to detects if user turns off/on motor
                if (motorChange.equalsIgnoreCase("off")) {
                    data1.turnOffMotor();   
                    System.out.println("Motor is OFF\n--------------------\n\"Type 'p' to pause, 'r' to resume, 'q' to exit:\"");                 
                } else if (motorChange.equalsIgnoreCase("on")) {
                    data1.turnOnMotor();
                    System.out.println("Motor is ON\n--------------------\n\"Type 'p' to pause, 'r' to resume, 'q' to exit:\"");
                } else if (motorChange.equalsIgnoreCase("report")){
                    data1.report();
                }

            } else if (input.equalsIgnoreCase("r")) {
                data1.resumePrinting();
                System.out.println("Printing resumed.");
            } else if (input.equalsIgnoreCase("q")) {
                data1.stopPrinting();
                System.out.println("Exiting program.");
                break;
            } else {
                System.out.println("Unknown command: " + input);
            }
        }
        scanner.close();
    }

    
}

/*
 * Name     :   Charindu Liyanage
 * UoW ID   :   w1761962
 * "I confirm that I understand what plagiarism / collusion / contract cheating is and have read and understood the
 * section on Assessment Offences in the Essential Information for Students. The work that I have submitted is entirely
 * my own. Any work from other authors is duly referenced and acknowledged."
 */
import java.io.IOException;
import java.util.Scanner;

/**
 * Class to Run CLI for Graphs system.
 */
public class Main {
    /**
     * Main Method.
     * @param args System arguments.
     */
    public static void main(String[] args) {

        // Printing the Welcome message.
        System.out.println(
                "|=========================================================================|\n" +
                "|                   W E L C O M E    T O    G R A P H S                   |\n" +
                "|=========================================================================|"
        );

        // Starting the menu.
        menu();
    }

    /**
     * Method for the creation of menu system.
     */
    public static void menu() {
        // Initialising the scanner for user inputs.
        Scanner sc = new Scanner(System.in);

        // Printing the menu.
        System.out.print(
                "\n|-------------------------------------------------------------------------|" +
                "\n|                                 M E N U                                 |" +
                "\n|-------------------------------------------------------------------------|" +
                "\n|         1:  Run Benchmarks                                              |" +
                "\n|         2:  Run a Manual File                                           |" +
                "\n|         3:  Exit Program                                                |" +
                "\n|-------------------------------------------------------------------------|" +
                "\n              : "
        );

        // Getting user input.
        String option = sc.nextLine().trim();

        // Checking the user input.
        switch (option){
            // User selecting to run benchmarks.
            case "1":
                System.out.println("\nRunning the Benchmark files in 'benchmarks' directory...");

                // Running benchmarks.
                benchmarks();

                System.out.println("\nOutput files are stored in the 'benchmark_output' directory.");

                // Opening the menu again.
                menu();
                break;

            // User selecting to run a manually added file.
            case "2":
                System.out.println("\nRunning a manually added file in 'input' directory...");

                // Running the user added graph.
                userInputFile();

                System.out.println("\nOutput file is stored in the 'output' directory.");

                // Opening the menu again.
                menu();
                break;

            // User selecting to exit the program.
            case "3":
                System.out.println("\nExiting program...");

                // terminating the program.
                System.exit(0);
                break;

            // User entering an invalid input.
            default:
                System.out.println("\nERROR - Invalid Input.");
                menu();
        }
    }

    /**
     * Method to Run the benchmarks.
     */
    public static void benchmarks() {
        System.out.println();

        String[] fileNames = {"bridge_", "ladder_"};

        // Running all benchmark files.
        for (String file : fileNames) {
            for (int i = 1; i <= 9; i++) {

                // Console loading bar progress.
                System.out.print("+");

                // Generating the file name.
                String fileName = file + i;

                // Initializing the graph.
                Graph graph = new Graph();

                try {
                    // Reading the file.
                    graph.readFile("benchmarks/" + fileName + ".txt");
                }
                // When input file not found.
                catch (IOException e) {
                    System.out.println("ERROR - File is not found at location 'benchmarks/" + fileName + ".txt'.");
                    menu();
                    return;
                }
                // When the text file is empty.
                catch (NullPointerException e) {
                    System.out.println("ERROR - File at location 'benchmarks/" + fileName + ".txt' is Empty.");
                    menu();
                    return;
                }
                // When the text file is corrupted.
                catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println("ERROR - File at location 'benchmarks/" + fileName + ".txt' is corrupted.");
                    menu();
                    return;
                }

                // Console loading bar progress.
                System.out.print("#");

                // Calculating the max flow.
                graph.calculateMaxFlow();

                // Console loading bar progress.
                System.out.print("+");

                try {
                    // Writing the output file.
                    graph.writeFile("benchmarks_output/" + fileName + "_result.txt", false);
                }
                // When the saving to the file is interrupted.
                catch (IOException e) {
                    System.out.println("ERROR - Saving was interrupted.");
                    menu();
                    return;
                }

                // Console loading bar progress.
                System.out.print("#");
            }
        }
        // Console loading bar progress.
        System.out.println("+");
    }

    /**
     * Method to run user input file.
     */
    public static void userInputFile() {
        // Initialising the scanner for user inputs.
        Scanner sc = new Scanner(System.in);

        // Prompting the user to add the file name.
        System.out.print("\nEnter the file name: ");
        String fileName = sc.nextLine();

        System.out.println();

        // Initialising the graph.
        Graph graph = new Graph();
        try {
            // Reading the graph from the file.
            graph.readFile("inputs/" + fileName + ".txt");
        }
        // When input file not found.
        catch (IOException e) {
            System.out.println("ERROR - File is not found at location 'inputs/" + fileName + ".txt'.");
            menu();
            return;
        }
        // When the text file is empty.
        catch (NullPointerException e) {
            System.out.println("ERROR - File at location 'inputs/" + fileName + ".txt' is Empty.");
            menu();
            return;
        }
        // When the text file is corrupted.
        catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("ERROR - File at location 'inputs/" + fileName + ".txt' is corrupted.");
            menu();
            return;
        }

        // Calculating the max flow of the graph.
        graph.calculateMaxFlow();

        try {
            // Writing the output file and printing the result to the console.
            graph.writeFile("outputs/" + fileName + "_result.txt", true);
        }
        // When the saving to the file is interrupted.
        catch (IOException e) {
            System.out.println("ERROR - Saving was interrupted.");
            menu();
        }
    }
}


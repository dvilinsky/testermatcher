import models.Tester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Main entry point for this program. Handles user input and displaying results
 */
public class TesterMatcherMain {
    private static final String INPUT_ERROR_MSG = "Error, invalid input, please try again";
    private static Pattern inputRegex = Pattern.compile("^[\\w\\s\\d]+(?:,[\\w\\s]+)*$");

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Map<String, File> files = getFileMap(args);
        TesterRepository testerRepository = new TesterRepository(files);
        QueryExecutor executor = new QueryExecutor(testerRepository);
        userInputLoop(executor);
    }

    private static void userInputLoop(QueryExecutor executor) {
        Scanner input = new Scanner(System.in);
        while (true) {
            String countries = getField("country", input);
            checkQuit(countries);
            if (!isValidInput(countries)){
                System.out.println(INPUT_ERROR_MSG);
                continue;
            }

            String devices = getField("device", input);
            checkQuit(devices);
            if (!isValidInput(devices)){
                System.out.println(INPUT_ERROR_MSG);
                continue;
            }
            List<Tester> result = executor.executeQuery(Arrays.asList(countries.split(",")),
                    Arrays.asList(devices.split(",")));
            System.out.println("Here are ther results of your query");
            System.out.println(ResultFormatter.format(result));
        }
    }

    /**
     * This method provides a template for prompting for a search term
     * @param field The search dimension for which you want to prompt
     * @param input Scanner for reading user input
     * @return The user's input, stripped of whitespace
     */
    private static String getField(String field, Scanner input) {
        System.out.print("Enter a " + field + " or a comma-separated list of each " + field + ", or \"all\" for every " + field);
        System.out.println(" (enter to skip, \\q to quit)");
        System.out.println(field + ": ");
        return input.nextLine().strip().toLowerCase().replace(", ", ",");
    }

    /**
     * Checks to see if user input is "\q" and exits the program if so
     * @param input The user's input
     */
    private static void checkQuit(String input) {
        if (input.equals("\\q")) {
            System.exit(0);
        }
    }

    /**
     * Chekcs that the user input is a valid, comma-separated strng, or empty
     * @param s User input to be valid
     * @return True if valid, false otherwise
     */
    private static boolean isValidInput(String s) {
        return inputRegex.matcher(s).matches() || s.isEmpty();
    }

    /**
     * Creates a mapping from input options to the file names the user has associated with them. It does make a number of,
     * possibly too many, assumptions about user input.
     * @param args Command line arguments
     * @return Mapping from command line options to their corresponding argument
     */
    private static Map<String, File> getFileMap(String[] args) {
        Map<String, File> optionToFile = new HashMap<>();
        for (int i = 0; i < args.length - 1; i++) {
            char first = args[i].charAt(0);
            if (first == '-') {
                if (args[i].length() == 2) {
                    optionToFile.put(args[i], new File(args[i+1]));
                }
            }
        }
        return optionToFile;
    }
}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * @author Rachel Feddersen
 *
 * @since Date Started: 3/02/2016 - Date Finished: 3/26/2016
 *
 * The KenKenPuzzle Class that contains the methods for reading the file chosen by the user and performing
 * backtracking during the search portion of the puzzle solving.
 */
public class KenKenPuzzle {
    KenKenVariables variables;

    private File file;

    /**
     * A constructor for the KenKenPuzzle class
     */
    public KenKenPuzzle() {
        variables = new KenKenVariables();
    }

    /**
     * A constructor for the KenKenPuzzle class
     * @param f - The file that was chosen by the user
     */
    public KenKenPuzzle(File f) {
        variables = new KenKenVariables();

        setFile(f);
    }

    /**
     * This method attempts to open the file
     * @param filename - The location of the file
     * @return - Return the new scanner with the opened file
     */
    private Scanner openFile(File filename) {
        // Open a scanner
        Scanner inputFile = null;

        // Try to create a new Scanner
        try {
            inputFile = new Scanner(filename);
            // If the file cannot be found
        } catch (FileNotFoundException e) {
            System.exit(0);
            e.printStackTrace();
        }

        return inputFile;
    }

    /**
     * This method attempts to read the file
     * @param filename - The location of the file
     * @return - Return the error if the file does not open
     */
    public boolean readFile(File filename) {
        // Open a scanner
        Scanner inputFile;
        // Create a new scanner
        inputFile = openFile(filename);
        boolean fileError = false;

        // Try to read the file
        try {
            // Read the board size and set the variable
            variables.setBoardSize(inputFile.nextInt());
            inputFile.nextLine();

            // Initialize the array for holding the values
            variables.setKenKenArray(new int[variables.getBoardSize()][variables.getBoardSize()]);

            List<String> domainList = new ArrayList<>();
            for (int i = 1; i <= variables.getBoardSize(); i++) {
                domainList.add("" + i);
            }

            // While there are more lines in the file,
            while(inputFile.hasNextLine()) {
                String[] values = inputFile.nextLine().split(" ", -1);
                // Call setInitialDomain method for setting domains for all the cells
                variables.domain.setInitialDomain(values, domainList);
                // Call createMathConst method for setting up the groups of cells based on their math equations
                variables.constraints.createMathConst(values);
            }

            // Call this method to make sure all of the cells that should be in the file are accounted for
            fileError = variables.domain.checkAllCellsThere(variables.getBoardSize());

            // Close the file
            inputFile.close();

        // If the file does not match what the file normally would be like
        } catch (InputMismatchException e) {
            // Set the error to true
            fileError = true;
        // Catch other exceptions
        } catch (Exception e) {
            // Set the error to true
            fileError = true;
        }

        return fileError;
    }

    /**
     * The method for determining if the value for the current cell is valid
     * @param curCell - The current cell
     */
    public void chooseValidDomain(String curCell) {
        // For all of the domains of the current cell
        for (String domainList: variables.domain.getKenKenDomain().get(curCell)) {
            // If the value is valid for the row and column
            if (variables.checkRowsCols(curCell.substring(0, 1), curCell.substring(1, 2), domainList)) {
                // If the value is valid within the math equation constraints
                if (variables.mathCellValid(curCell, domainList)) {
                    // Set it as the current value
                    variables.getKenKenArray()[Integer.parseInt(curCell.substring(0, 1))][Integer.parseInt(curCell.substring(1, 2))]
                            = Integer.parseInt(domainList);
                    break;
                }
            }
        }
    }

    /**
     * The method for performing a piece of the backtracking for the search
     * @param currentVal - The current value for the cell
     * @param smallestDomains - The smallestDomains list so the program knows what cell to look at
     * @return - A boolean showing if more backtracking is needed
     */
    public boolean doBackTracking(int currentVal, List<String> smallestDomains) {
        boolean backTrack = true;

        // For the list of the domains for the first cell in the smallestDomains list
        for (String domainList: variables.domain.getKenKenDomain().get(smallestDomains.get(0))) {
            // If the domain is larger than the current value
            if (Integer.parseInt(domainList) > currentVal) {
                // If the value is valid for the row and column
                if (variables.checkRowsCols(smallestDomains.get(0).substring(0, 1),
                        smallestDomains.get(0).substring(1, 2), domainList)) {
                    // IF the value is valid within the math equation constraints
                    if (variables.mathCellValid(smallestDomains.get(0), domainList)) {
                        backTrack = false;

                        // Set it as the current value
                        variables.getKenKenArray()[Integer.parseInt(smallestDomains.get(0).substring(0, 1))]
                                [Integer.parseInt(smallestDomains.get(0).substring(1, 2))] = Integer.parseInt(domainList);

                        break;
                    }
                }
            }
        }

        return backTrack;
    }

    /**
     * The method for retrieving the file chosen by the user
     * @return - The file
     */
    public File getFile() {
        return file;
    }

    /**
     * The method for setting the file chosen by the user
     * @param file - The file
     */
    private void setFile(File file) {
        this.file = file;
    }
}
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Rachel Feddersen
 *
 * @since Date Started: 3/02/2016 - Date Finished: 3/26/2016
 *
 * The KenKenDriver Class that is the main class and contains the main method through which the program is called.
 */
public class KenKenDriver extends JFrame {
    int win_wid = 250;
    int win_hei = 300;
    boolean solvingWithConst = false;
    boolean solvingWithOutConst = false;
    private List<String> smallestDomains = new LinkedList<>();
    private List<String> assignedOrder = new LinkedList<>();
    private int domainSize = 1;

    KenKenPuzzle puzzle;
    KenKenDisplay display;

    private File file;
    private JMenuItem loadFileItem;
    private JMenuItem exitMenuItem;
    private JMenuItem solveWithConst;
    private JMenuItem solveWithOutConst;

    /**
     * The constructor for the class that initializes the other classes and creates the listeners
     */
    public KenKenDriver() {
        this.setTitle("KenKen Solver");
        this.setSize(win_wid, win_hei);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Call this method to create the end message dialog
                createExitMessage();
            }
        });

        // Initialize the objects
        puzzle = new KenKenPuzzle();
        display = new KenKenDisplay(puzzle);

        // Create the menu bar and put it in the window
        JMenuBar bar = buildPuzzleBar();
        setJMenuBar(bar);

        this.add(display);
        this.setVisible(true);// Set the title and close operation

        // Add a listener to the mouse
        addMouseListener(new MouseAdapter() {
            /**
             * Override the mousePressed class for running the program during a mouse click
             * @param e - The mouseEvent received by the program.
             */
            @Override
            public void mousePressed(MouseEvent e) {
                // If it is chosen by the user to solve with constraints
                if (solvingWithConst) {
                    solveWithConstListener();
                // If it is chosen by the user to solve without constraints
                } else if (solvingWithOutConst) {
                    solveWithOutConstListener();
                }
            }
        });

        // Add a listener to the enter key
        addKeyListener(new KeyAdapter() {
            /**
             * Override the keyReleased class for running the program after an enter key is released
             * @param e - The keyEvent received by the program.
             */
            @Override
            public void keyReleased(KeyEvent e) {
                // If it is chosen by the user to solve with constraints
                if (e.getKeyCode()==KeyEvent.VK_ENTER && solvingWithConst) {
                    solveWithConstListener();
                // If it is chosen by the user to solve without constraints
                } else if (e.getKeyCode()==KeyEvent.VK_ENTER && solvingWithOutConst) {
                    solveWithOutConstListener();
                }
            }
        });

        // Set the menu options to be disabled
        solveWithConst.setEnabled(false);
        solveWithOutConst.setEnabled(false);

        createOpeningMessage();
    }

    /**
     * The method sets up the menu bar that has the different menu options like file and puzzle
     * @return - Return the assembled menu bar
     */
    private JMenuBar buildPuzzleBar(){
        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create and add the menu options for the menu bar
        JMenu fileMenu = buildFileMenu();
        JMenu puzzleMenu = buildPuzzleMenu();
        menuBar.add(fileMenu);
        menuBar.add(puzzleMenu);

        return menuBar;
    }

    /**
     * The method for creating the options for the file menu item
     * @return - The completed fileMenu
     */
    private JMenu buildFileMenu(){
        // Create
        JMenu fileMenu = new JMenu("File");

        // Create the menu items
        solveWithConst = new JMenuItem("Solve Puzzle w/ Constraint");
        solveWithOutConst = new JMenuItem("Solve Puzzle w/out Constraint");
        exitMenuItem = new JMenuItem("Exit");

        // Add these menu items into fileMenu
        fileMenu.add(solveWithConst);
        fileMenu.add(solveWithOutConst);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // Hook up the menu items with the listener
        MyListener listener = new MyListener();
        solveWithConst.addActionListener(listener);
        solveWithOutConst.addActionListener(listener);
        exitMenuItem.addActionListener(listener);

        return fileMenu;
    }

    /**
     * The method for creating the options for the puzzle menu item
     * @return - The completed puzzleMenu
     */
    private JMenu buildPuzzleMenu(){
        // Create
        JMenu puzzleMenu = new JMenu("KenKen Puzzle");

        // Create the menu items
        loadFileItem = new JMenuItem("Load File");

        // Add these menu items into puzzleMenu
        puzzleMenu.add(loadFileItem);

        // Hook up the menu items with the listener
        MyListener listener = new MyListener();
        loadFileItem.addActionListener(listener);

        return puzzleMenu;
    }

    /**
     * This is the private class for the action listener
     * @author Rachel Feddersen
     * It has the actionPerformed method inside of it so it can listen to what the user is doing
     * and respond accordingly
     */
    private class MyListener implements ActionListener {
        /**
         * The actionPerformed method that gets the action that was performed and does the
         * corresponding action
         */
        public void actionPerformed(ActionEvent e) {
            boolean fileError = false;

            // If the user clicks on the exitMenuItem
            if (e.getSource() == exitMenuItem) {
                // Call this method to create the end message dialog
                createExitMessage();
                // If the user clicks on the loadFileItem
            } else if (e.getSource() == loadFileItem) {
                fileError = getFileInformation();

                if (!fileError) {
                    display.setPuzzle(puzzle);
                    display.repaint();

                    // Reset the window size based on the board size
                    win_wid = (puzzle.variables.getBoardSize() + 1) * 50 + 5;
                    win_hei = (puzzle.variables.getBoardSize() + 2) * 50 + 5;
                    setSize(win_wid, win_hei);
                } else {
                    // Create the error message
                    createErrorMessage();
                    // Disable the options that might have been enabled
                    solveWithConst.setEnabled(false);
                    solveWithOutConst.setEnabled(false);
                }

                // Reset values
                solvingWithConst = false;
                solvingWithOutConst = false;
                domainSize = 1;
                smallestDomains = new LinkedList<>();
                assignedOrder = new LinkedList<>();
                // If the user clicks on the solveWithConst option
            } else if (e.getSource() == solveWithConst) {
                solvingWithConst = true;
                domainSize = 1;
                // If the user clicks on the solveWithOutConst option
            } else if (e.getSource() == solveWithOutConst) {
                solvingWithOutConst = true;
                domainSize = 1;
            }

            // Call method to enable options
            setEnabledOptions(e, fileError);
        }
    }

    /**
     * The method for getting the file that the user choose and calling methods to retrieve
     * the files information
     * @return - Return the file error
     */
    public boolean getFileInformation() {
        // Show a dialog to allow the user to choose files
        JFileChooser fc = new JFileChooser("./");  //set starting point
        int status = fc.showOpenDialog(null);
        boolean fileError = false;
        // If the user actually chose a file
        if (status == JFileChooser.APPROVE_OPTION){
            // Get the selected file
            file = fc.getSelectedFile();

            // Create a new routeFinder class and get the TreeMap
            puzzle = new KenKenPuzzle(file);
            fileError = puzzle.readFile(file);
        }

        return fileError;
    }

    /**
     * The method that re-enables the options the user has to pick from on the menu bar
     * @param e - The ActionEvent variable that can be used to determine what action just occured
     */
    public void setEnabledOptions(ActionEvent e, boolean fileError) {
        // If a file has been chosen and a puzzle solving option has not been chosen
        if (file != null && e.getSource() != solveWithConst && e.getSource() != solveWithOutConst
                && !fileError) {
            // Enable all of the options
            solveWithConst.setEnabled(true);
            solveWithOutConst.setEnabled(true);
        } else if (file != null && (e.getSource() == solveWithConst || e.getSource() == solveWithOutConst)) {
            // Disable the options not related to getting the file
            solveWithConst.setEnabled(false);
            solveWithOutConst.setEnabled(false);
        }
    }

    /**
     * The method for setting up the listener's ability to solve the puzzle with constraints
     */
    public void solveWithConstListener() {
        boolean valuesRemoved = doWithConstraint();

        // If there were not any values removed from the constraint search and the puzzle is not solved
        if (!valuesRemoved && !puzzle.variables.solved()) {
            doWithOutConstraint();

            repaint();
            // If there were values removed from the constraint search and the puzzle is not solved
        } else if (valuesRemoved && !puzzle.variables.solved()) {
            repaint();
            // If the puzzle is solved
        } else if (puzzle.variables.solved()) {
            repaint();

            solvingWithConst = false;

            createSolveMessage();
        }
    }

    /**
     * The method for setting up the listener's ability to solve the puzzle without constraints
     */
    public void solveWithOutConstListener() {
        // If the puzzle is still not solved
        if (!puzzle.variables.solved()) {
            doWithOutConstraint();

            repaint();
        } else if (puzzle.variables.solved()) {
            repaint();

            solvingWithOutConst = false;

            createSolveMessage();
        }
    }

    /**
     * The method for solving the puzzle based on constraints
     * @return - Whether any values were removed from the domain or not
     */
    public boolean doWithConstraint() {
        puzzle.variables.removeMathValues();

        return constraintSearch();
    }

    /**
     * The method for solving the puzzle based on a search
     */
    public void doWithOutConstraint() {
        // If there are no cells in the smallestDomains list
        if (smallestDomains.size() == 0) {
            do {
                // Increase the current domainSize
                domainSize += 1;
                for(Map.Entry<String, List<String>> entry : puzzle.variables.domain.getKenKenDomain().entrySet()) {
                    // If the entries domains are of that size
                    if (entry.getValue().size() == domainSize) {
                        smallestDomains.add(entry.getKey());
                    }
                }
            } while (smallestDomains.size() == 0);
        } else {
            performSearch();
        }
    }

    /**
     * The method for doing the constraint search
     * @return - Whether any values were removed from the domain or not
     */
    public boolean constraintSearch() {
        boolean valuesRemoved = false;

        // For all of the cells in the puzzle
        for(Map.Entry<String, List<String>> entry : puzzle.variables.domain.getKenKenDomain().entrySet()) {
            // If they only have a single value in the domain
            if (entry.getValue().size() == 1) {
                // Get their row and column
                String row = entry.getKey().substring(0, 1),
                       col = entry.getKey().substring(1, 2);
                // If there is not already a value assigned to that spot in the puzzle
                if (puzzle.variables.getKenKenArray()[Integer.parseInt(row)][Integer.parseInt(col)] == 0) {
                    // Perform node consistency and assign the value
                    puzzle.variables.performNodeConsistency(row, col, entry.getValue().get(0));
                    puzzle.variables.getKenKenArray()[Integer.parseInt(row)][Integer.parseInt(col)]
                           = Integer.parseInt(entry.getValue().get(0).substring(0, 1));
                    valuesRemoved = true;
                    break;
                }
            }
        }

        return valuesRemoved;
    }

    /**
     * The method for performing the search on the puzzle
     */
    public void performSearch() {
        boolean backTrack;

        // Add the first entry from the smallestDomains list to the assignedOrder list and remove that entry
        assignedOrder.add(smallestDomains.get(0));
        smallestDomains.remove(0);
        String curCell = assignedOrder.get(assignedOrder.size()-1);

        // Make sure to chose a valid domain from the cells current domain
        puzzle.chooseValidDomain(curCell);

        // If there is not anything assigned to the spot in the puzzle
        if (puzzle.variables.getKenKenArray()[Integer.parseInt(curCell.substring(0, 1))][Integer.parseInt(curCell.substring(1, 2))] == 0) {
            // Enable backtracking
            backTrack = true;
            int currentVal;

            // Add the last cell from the assignedOrder list to the beginning of the smallestDomains list
            smallestDomains.add(0, assignedOrder.get(assignedOrder.size()-1));
            assignedOrder.remove(assignedOrder.size()-1);

            // While backTrack is true
            while(backTrack) {
                // Add the last cell from the assignedOrder list to the beginning of the smallestDomains list
                smallestDomains.add(0, assignedOrder.get(assignedOrder.size()-1));
                assignedOrder.remove(assignedOrder.size()-1);

                currentVal = puzzle.variables.getKenKenArray()[Integer.parseInt(smallestDomains.get(0).substring(0, 1))]
                        [Integer.parseInt(smallestDomains.get(0).substring(1, 2))];

                // Reset this cell assignment
                puzzle.variables.getKenKenArray()[Integer.parseInt(smallestDomains.get(0).substring(0, 1))]
                        [Integer.parseInt(smallestDomains.get(0).substring(1, 2))] = 0;

                backTrack = puzzle.doBackTracking(currentVal, smallestDomains);

                // If backTrack is not longer true
                if (!backTrack) {
                    // Add the first entry from the smallestDomains list to the assignedOrder list and remove that entry
                    assignedOrder.add(smallestDomains.get(0));
                    smallestDomains.remove(0);
                }
            }
        }
    }

    /**
     * The method creates the messageDialog for when the user runs the program
     */
    private void createOpeningMessage() {
        // Open table in message dialog
        JOptionPane.showMessageDialog(null,
                "This is my KenKenSolver. To use this program, go to KenKenPuzzle and load a file for the game.\n" +
                        "Then go to File. You can either choose to solve the puzzle with or without constraints.\n" +
                        "After that, just click the mouse or press enter to run the solver. Have fun!",
                "Greeting Message",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * The method creates the messageDialog for when the user picks an incorrect file
     */
    private void createErrorMessage() {
        // Open table in message dialog
        JOptionPane.showMessageDialog(null,
                "The file you chose is either not a valid file for this program or contains invalid information.\n" +
                        "Please pick a new file and try again.",
                "Error Message",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * The method creates the messageDialog for when the user solves the problem
     */
    private void createSolveMessage() {
        // Open table in message dialog
        JOptionPane.showMessageDialog(null,
                "The puzzle has been solved! Please pick a new puzzle to try again or exit the program.",
                "Solved Message",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * The method creates the messageDialog for when the user exits the program
     */
    private void createExitMessage() {
        // Open table in message dialog
        JOptionPane.showMessageDialog(null,
                "You are now exiting the program. Thanks for using my KenKen Solver!",
                "Exit Message",
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /**
     * The main method that calls the KenKenDriver
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new KenKenDriver();
    }
}
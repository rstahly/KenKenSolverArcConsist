import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Rachel Feddersen
 *
 * @since Date Started: 3/02/2016 - Date Finished: 3/26/2016
 *
 * The KenKenDisplay Class that consists of the methods for creating the puzzle display based on the constraints
 * and the current solution to the board that is retrieved from the puzzle class.
 */
public class KenKenDisplay extends JPanel {
    KenKenPuzzle puzzle;
    int cellSize = 50;
    int divWidX = 2;
    int divWidY = 2;
    int start_X = 8;
    int start_Y = 11;
    int letterOffSet_Y = 43;
    int letterOffSet_X = 20;
    int numberOffSet_Y = 20;
    int numberOffSet_X = 10;

    Color[] colors = {Color.white};

    Font bigFont = new Font("Arial", Font.PLAIN, 30);
    Font smallFont = new Font("Arial", Font.BOLD, 15);

    /**
     * The constructor for the KenKenDisplay Class
     * @param p - The puzzle object
     */
    public KenKenDisplay(KenKenPuzzle p) {
        puzzle = p;
    }

    /**
     * The method for setting the current state of the puzzle
     * @param p - The current state of the puzzle object
     */
    public void setPuzzle(KenKenPuzzle p) {
        puzzle = p;
    }

    /**
     * The method for painting the puzzle
     * @param g - The Graphics component
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(start_X,
                start_Y,
                (cellSize + divWidX) * puzzle.variables.getBoardSize() + divWidX + 8,
                (cellSize + divWidY) * puzzle.variables.getBoardSize() + divWidY + 8);

        // For all of the cells on the board
        for (int row = 0; row < puzzle.variables.getBoardSize(); row++) {
            for (int col = 0; col < puzzle.variables.getBoardSize(); col++) {
                g.setColor(colors[0]);
                int fromStartX = start_X;
                int fromStartY = start_Y;

                // If it is in the first row
                if (row == 0) {
                    divWidY = 6;
                    fromStartY += divWidY;
                } else {
                    divWidY = 2;
                    fromStartY += divWidY + 4;
                }

                // If it is in the first column
                if (col == 0) {
                    divWidX = 6;
                    fromStartX += divWidX;
                } else {
                    divWidX = 2;
                    fromStartX += divWidX + 4;
                }

                g.fillRect(fromStartX + (cellSize+divWidX) * col,
                        fromStartY + (cellSize+divWidY) * row,
                        cellSize,
                        cellSize);

                // If there is an assignment in the puzzle
                if (puzzle.variables.getKenKenArray()[row][col] != 0) {
                    g.setColor(Color.BLACK);
                    g.setFont(bigFont);
                    g.drawString("" + puzzle.variables.getKenKenArray()[row][col],
                            start_X + divWidX + (cellSize + divWidX) * col + letterOffSet_X,
                            start_Y + divWidY + (cellSize + divWidY) * row + letterOffSet_Y);
                }
            }
        }

        // If a file has been chosen by the user
        if (puzzle.getFile() != null) {
            createInitialBoardDisplay(g);
        }
    }

    /**
     * The method for creating the initial puzzle display
     * @param g - The Graphics component
     */
    public void createInitialBoardDisplay(Graphics g) {
        TreeSet<String> cellGroup;
        TreeSet<String> farthestRow = new TreeSet<>();
        TreeSet<String> farthestCol = new TreeSet<>();
        TreeSet<String> mostNorthCell = new TreeSet<>();

        // For all of the cell groupings in the mathConstraints TreeMap
        for(Map.Entry<String,String[]> entry : puzzle.variables.constraints.getMathConstraints().entrySet()) {
            cellGroup = new TreeSet<>();
            String[] value = entry.getValue();

            cellGroup.add(entry.getKey());
            for (int i = 0; i < value.length-1; i++) {
                cellGroup.add(value[i]);
            }

            // Add to the farthestCol list
            farthestCol.addAll(setMathBorders(cellGroup, farthestCol, 0, 1, 1, 2));

            // Add to the farthestRow list
            farthestRow.addAll(setMathBorders(cellGroup, farthestRow, 1, 2, 0, 1));

            // Get the first cell as the mostNorthCell
            mostNorthCell.add(cellGroup.first());
        }

        g.setColor(Color.BLACK);

        // For all of the cells in the farthestCol list
        for (String colDivides: farthestCol) {
            g.fillRect(start_X + 2 + (cellSize+divWidX) + (cellSize+divWidX) * Integer.parseInt(colDivides.substring(1,2)),
                    start_Y + 2 + (cellSize+divWidY) * Integer.parseInt(colDivides.substring(0,1)),
                    6,
                    56);
        }

        // For all of the cells in the farthestRow list
        for (String rowDivides: farthestRow) {
            g.fillRect(start_X + 2 + (cellSize+divWidX) * Integer.parseInt(rowDivides.substring(1,2)),
                    start_Y + 2 + (cellSize+divWidY) + (cellSize+divWidY) * Integer.parseInt(rowDivides.substring(0,1)),
                    56,
                    6);
        }

        // Call the method for adding the math equation in the corner cell
        addCornerValue(g, mostNorthCell);
    }

    /**
     * The method for adding the math equation in the corner cell
     * @param g - The Graphics component
     * @param mostNorthCell - The TreeSet list containing the mostNorthCells
     */
    private void addCornerValue(Graphics g, TreeSet<String> mostNorthCell) {
        // For the number of cells in the list
        for (String numberCells: mostNorthCell) {
            g.setFont(smallFont);
            String mathEquation = "";

            // For the number of cell groupings in the mathConstraints TreeMap
            for(Map.Entry<String,String[]> entry : puzzle.variables.constraints.getMathConstraints().entrySet()) {
                // If the key equals the mostNorthCell
                if (entry.getKey().equals(numberCells)) {
                    for (String cells: entry.getValue()) {
                        String endChar = cells.substring(cells.length()-1, cells.length());
                        if (endChar.equals("+") || endChar.equals("-")) {
                            mathEquation = cells;
                        } else if (endChar.equalsIgnoreCase("x")) {
                            mathEquation = cells.substring(0, cells.length()-1) + "x";
                        } else if (endChar.equals("/")) {
                            mathEquation = cells.substring(0, cells.length()-1) + "\u00F7";
                        } else if (endChar.equals("=")) {
                            mathEquation = cells.substring(0, 1);
                        }
                    }
                } else {
                    for (String testCells: entry.getValue()) {
                        if (testCells.equals(numberCells)) {
                            for (String cells: entry.getValue()) {
                                String endChar = cells.substring(cells.length()-1, cells.length());
                                if (endChar.equals("+") || endChar.equals("-")) {
                                    mathEquation = cells;
                                } else if (endChar.equalsIgnoreCase("x")) {
                                    mathEquation = cells.substring(0, cells.length()-1) + "x";
                                } else if (endChar.equals("/")) {
                                    mathEquation = cells.substring(0, cells.length()-1) + "\u00F7";
                                } else if (endChar.equals("=")) {
                                    mathEquation = cells.substring(0, 1);
                                }
                            }
                        }
                    }
                }
            }

            // Draw the math equation in the corner of the box
            g.drawString(mathEquation,
                    (start_X + (cellSize + divWidX) * Integer.parseInt(numberCells.substring(1,2)) + numberOffSet_X),
                    (start_Y + (cellSize + divWidY) * Integer.parseInt(numberCells.substring(0,1)) + numberOffSet_Y));
        }
    }

    /**
     * The method for finding the boarders around the individual cell groupings
     * @param cellGroup - The group of cells with the same math constraint
     * @param farthestCell - The list with the farthestCell
     * @param mb - The beginning value for the substring method
     * @param me - The ending value for the substring method
     * @param b - The beginning value for the second substring method
     * @param e - The ending value for the second substring method
     * @return - The TreeSet with the newest farthest value in it
     */
    public TreeSet<String> setMathBorders(TreeSet<String> cellGroup, TreeSet<String> farthestCell, int mb, int me, int b, int e) {
        String tempFarthest;

        for (String initialCell: cellGroup) {
            tempFarthest = "00";
            for (String otherCell: cellGroup) {
                // If either the row of column of the cell equals the other cell's row or column
                if (initialCell.substring(mb, me).equals(otherCell.substring(mb, me))) {
                    // If the initial cell is greater than the current one
                    if (initialCell.substring(b,e).compareTo(otherCell.substring(b,e)) > 0
                    && initialCell.substring(b,e).compareTo(tempFarthest.substring(b,e)) > 0) {
                        tempFarthest = initialCell;
                    // If the other cell is greater than the current one
                    } else if (otherCell.substring(b,e).compareTo(initialCell.substring(b,e)) > 0
                    && otherCell.substring(b,e).compareTo(tempFarthest.substring(b,e)) > 0) {
                        tempFarthest = otherCell;
                    }
                }
            }

            // If no farthest cell was found
            if (tempFarthest.equals("00")) {
                tempFarthest = initialCell;
            }

            // If the cell is not already in the farthestCell TreeSet
            if (!farthestCell.contains(tempFarthest)) {
                farthestCell.add(tempFarthest);
            }
        }
        return farthestCell;
    }
}

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Rachel Feddersen
 *
 * @since Date Started: 3/08/2016 - Date Finished: 3/26/2016
 *
 * The KenKenVariables Class that contains the board with its temporary assignments and contains the methods for
 * node consistency and other domain reductions.
 */
public class KenKenVariables {
    KenKenConstraints constraints;
    KenKenDomain domain;

    private int[][] kenKenArray = new int[4][4];

    private int boardSize = 4;

    public KenKenVariables () {
    }

    /**
     * The method for returning the KenKenArray's current state
     * @return - The KenKenArray's current state
     */
    public int[][] getKenKenArray() {
        return kenKenArray;
    }

    /**
     * The method for setting the KenKenArray's current state and the domains and constraints
     * @param kenKenArray - The KenKenArray's current state
     */
    public void setKenKenArray(int[][] kenKenArray) {
        this.kenKenArray = kenKenArray;

        domain = new KenKenDomain();
        constraints = new KenKenConstraints(domain, getBoardSize());
    }

    /**
     * The method for retrieving the boardSize
     * @return - The board size
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * The method for setting the boardSize
     * @param boardSize - The board size
     */
    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    /**
     * The method for removing values from the domain if they are not consistent with the math equations in the
     * constraints
     */
    public void removeMathValues() {
        // Remove all of the values in cells that are equal to one number
        constraints.removeEqualsDomain();

        // For all of the constraint sets
        for(Map.Entry<String,String[]> entry : constraints.getMathConstraints().entrySet()) {
            LinkedList<String> cellGroup = new LinkedList<>();
            String[] value = entry.getValue();

            cellGroup.add(entry.getKey());
            for (int i = 0; i < value.length-1; i++) {
                cellGroup.add(value[i]);
            }

            // For all of the cells in the current constraint
            for (String cells: entry.getValue()) {
                // If it is an addition problem
                if (cells.substring(cells.length()-1, cells.length()).equals("+")) {
                    if (cellGroup.size() == 2) {
                        constraints.addArcConsistency(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                    } else if (cellGroup.size() == 3) {
                        constraints.ternaryConsistency(cellGroup, cells.substring(0, cells.length() - 1),
                        cells.substring(cells.length()-1, cells.length()), getKenKenArray());
                    } else {
                        constraints.removeAddValues(cellGroup, cells.substring(0, cells.length() - 1), getKenKenArray());
                    }
                // If it is a subtraction problem
                } else if (cells.substring(cells.length()-1, cells.length()).equals("-")) {
                    constraints.subArcConsistency(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                // If it is a multiplication problem
                } else if (cells.substring(cells.length()-1, cells.length()).equalsIgnoreCase("x")) {
                    if (cellGroup.size() == 2) {
                        constraints.multArcConsistency(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                    } else if (cellGroup.size() == 3) {
                        constraints.ternaryConsistency(cellGroup, cells.substring(0, cells.length() - 1),
                        cells.substring(cells.length()-1, cells.length()), getKenKenArray());
                    } else {
                        constraints.removeMultValues(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                    }
                // If it is a division problem
                } else if (cells.substring(cells.length()-1, cells.length()).equals("/")) {
                    constraints.divArcConsistency(cellGroup, cells.substring(0, cells.length()-1), getKenKenArray());
                }
            }
        }
    }

    /**
     * The method for performing node consistency on the puzzle
     * @param row - The row of the cell with the domain of one
     * @param col - The col of the cell with the domain of one
     * @param value - The value of the cell with the domain of one
     */
    public void performNodeConsistency(String row, String col, String value) {
        List<String> newDomain;

        // For all of the cells in the puzzle
        for(Map.Entry<String,List<String>> entry : domain.getKenKenDomain().entrySet()) {
            newDomain = new LinkedList<>();
            // If the cell is in the same row but not the same column
            if (entry.getKey().substring(0, 1).equals(row) && !entry.getKey().substring(1, 2).equals(col)) {
                for (int i = 0; i < domain.getKenKenDomain().get(entry.getKey()).size(); i++) {
                    // If the value is not equal to the value sent into the program
                    if (!entry.getValue().get(i).equals(value)) {
                        newDomain.add("" + entry.getValue().get(i));
                    }
                }
                // Replace the domain
                domain.getKenKenDomain().replace(entry.getKey(), newDomain);
            }

            // If the cell is in the same column but no the same row
            if (entry.getKey().substring(1, 2).equals(col) && !entry.getKey().substring(0, 1).equals(row)) {
                for (int i = 0; i < domain.getKenKenDomain().get(entry.getKey()).size(); i++) {
                    // If the value is not equal to the value sent into the program
                    if (!entry.getValue().get(i).equals(value)) {
                        newDomain.add("" + entry.getValue().get(i));
                    }
                }
                // Replace the domain
                domain.getKenKenDomain().replace(entry.getKey(), newDomain);
            }
        }
    }

    /**
     * The method for checking the rows and columns and making sure that the value is not already assigned
     * @param row - The row of the cell being checked
     * @param col - The column of the cell being checked
     * @param value - The value of the cell being checked
     * @return - A boolean the states whether or not the assignment is valid
     */
    public boolean checkRowsCols(String row, String col, String value) {
        boolean validValue = true;

        // For the all of the cells in the array
        for (int i = 0; i < getKenKenArray().length; i++) {
            // If the cells are in the same row
            if (i == Integer.parseInt(row)) {
                for (int j = 0; j < getKenKenArray().length; j++) {
                    // If the cells are not in the same column
                    if (j != Integer.parseInt(col)) {
                        // If the value has already been assigned in the row
                        if (getKenKenArray()[i][j] == Integer.parseInt(value)) {
                            validValue = false;
                        } else if (getKenKenArray()[i][j] == 0) {
                            // If the domain contains the value
                            if (domain.getKenKenDomain().get(""+i+j).contains(value)) {
                                // If the domain size without the value is equal to zero
                                if (domain.getKenKenDomain().get(""+i+j).size()-1 < 1) {
                                    validValue = false;
                                }
                            }
                        }
                    }
                }
            }

            // the cells are in the same column
            if (i == Integer.parseInt(col)) {
                for (int j = 0; j < getKenKenArray().length; j++) {
                    // If the cells are not in the same row
                    if (j != Integer.parseInt(row)) {
                        // If the value has already been assigned in the column
                        if (getKenKenArray()[j][i] == Integer.parseInt(value)) {
                            validValue = false;
                        } else if (getKenKenArray()[j][i] == 0) {
                            // If the domain contains the value
                            if (domain.getKenKenDomain().get(""+j+i).contains(value)) {
                                // If the domain size without the value is equal to zero
                                if (domain.getKenKenDomain().get(""+j+i).size()-1 < 1) {
                                    validValue = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return validValue;
    }

    /**
     * The method for determining if the value is consistent with the main constraints on the cell
     * @param cell - The cell being examined
     * @param value - The value of the cell
     * @return - A boolean depending on whether the value is valid for the cell
     */
    public boolean mathCellValid(String cell, String value) {
        boolean validValue = false;

        // For all of the constraints
        for(Map.Entry<String,String[]> entry : constraints.getMathConstraints().entrySet()) {
            TreeSet<String> cellGroup = new TreeSet<>();
            String[] valueArray = entry.getValue();

            cellGroup.add(entry.getKey());

            for (int i = 0; i < valueArray.length-1; i++) {
                cellGroup.add(valueArray[i]);
            }

            // Get the value the math equation has to equal
            String eqTotal = valueArray[valueArray.length-1].substring(0, valueArray[valueArray.length-1].length()-1);

            // Get the sign of the math equation
            String sign = valueArray[valueArray.length-1].substring(valueArray[valueArray.length-1].length()-1,
                    valueArray[valueArray.length-1].length());

            for (String cells: cellGroup) {
                if (cells.equals(cell)) {
                    // Check the validation for the math equation
                    validValue = doMathValidation(cellGroup, cell, sign, eqTotal, value);
                }
            }
        }

        return validValue;
    }

    /**
     * The method for checking the validation of the value for the math constraints
     * @param cellGroup - The TreeSet of the cells for the constraint
     * @param cell - The cell being examined
     * @param sign - The sign of the cell's math equation
     * @param eqTotal - The value that the cells's values have to equal
     * @param value - The potential value for the cell
     * @return - A boolean to indicate if any values were removed
     */
    private boolean doMathValidation(TreeSet<String> cellGroup, String cell, String sign, String eqTotal, String value) {
        boolean validValue = false;
        boolean allCellsFilled = true;
        int cellsTotal = 0;

        // For all of the cells in the cell group
        for (String cells: cellGroup) {
            if (!cells.equals(cell)) {
                // If the sign for the cell is addition or subtraction
                if (sign.equals("+") || sign.equals("-")) {
                    if (getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] != 0) {
                        cellsTotal += getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))];
                    } else {
                        allCellsFilled = false;
                    }
                // If the sign for hte cell is multiplication or division
                } else if (sign.equalsIgnoreCase("x") || sign.equals("/")) {
                    if (cellsTotal == 0) {
                        cellsTotal += 1;
                    }
                    if (getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] != 0) {
                        cellsTotal *= getKenKenArray()[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))];
                    } else {
                        allCellsFilled = false;
                    }
                }
            }
        }

        // If the sign is an equals sign
        if (sign.equals("=")) {
            // If the value is equal to what the cell should be equal to
            if (Integer.parseInt(value) == Integer.parseInt(eqTotal)) {
                validValue = true;
            }
        // IF the sign is an addition sign
        } else if (sign.equals("+")) {
            validValue = checkAddition(value, eqTotal, cellsTotal, allCellsFilled);
        // If the sign is a subtraction sign
        } else if (sign.equals("-")) {
            validValue = checkSubtraction(value, eqTotal, cellsTotal, allCellsFilled);
        // If the sign is a multiplication sign
        } else if (sign.equalsIgnoreCase("x")) {
            validValue = checkMultiplication(value, eqTotal, cellsTotal, allCellsFilled);
        // If the sign is a division sign
        } else if (sign.equals("/")) {
            validValue = checkDivision(value, eqTotal, cellsTotal, allCellsFilled);
        }

        return validValue;
    }

    /**
     * The method for checking if the cell's potential value is valid with the addition constraints
     * @param value - The potential value
     * @param eqTotal - The value the cells's values have to equal
     * @param cellsTotal - The total amount entered into the cells in the group already
     * @param allCellsFilled - A boolean determining if all the cells except the current have been assigned
     * @return - A boolean determining if the potential value is valid
     */
    public boolean checkAddition(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if ((Integer.parseInt(eqTotal) - cellsTotal) >= Integer.parseInt(value)) {
                validValue = true;
            }
        } else {
            if ((Integer.parseInt(eqTotal) - cellsTotal) == Integer.parseInt(value)) {
                validValue = true;
            }
        }

        return validValue;
    }

    /**
     * The method for checking if the cell's potential value is valid with the subtraction constraints
     * @param value - The potential value
     * @param eqTotal - The value the cells's values have to equal
     * @param cellsTotal - The total amount entered into the cells in the group already
     * @param allCellsFilled - A boolean determining if all the cells except the current have been assigned
     * @return - A boolean determining if the potential value is valid
     */
    public boolean checkSubtraction(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if (Integer.parseInt(value) > cellsTotal) {
                if ((Integer.parseInt(value) - cellsTotal) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal - Integer.parseInt(value)) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
            if (cellsTotal == 0) {
                validValue = true;
            }
        } else {
            if (Integer.parseInt(value) > cellsTotal) {
                if ((Integer.parseInt(value) - cellsTotal) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal - Integer.parseInt(value)) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
        }

        return validValue;
    }

    /**
     * The method for checking if the cell's potential value is valid with the multiplication constraints
     * @param value - The potential value
     * @param eqTotal - The value the cells's values have to equal
     * @param cellsTotal - The total amount entered into the cells in the group already
     * @param allCellsFilled - A boolean determining if all the cells except the current have been assigned
     * @return - A boolean determining if the potential value is valid
     */
    public boolean checkMultiplication(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if ((Integer.parseInt(eqTotal) / cellsTotal) >= Integer.parseInt(value)) {
                validValue = true;
            }
        } else {
            if ((Integer.parseInt(eqTotal) / cellsTotal) == Integer.parseInt(value)) {
                validValue = true;
            }
        }

        return validValue;
    }

    /**
     * The method for checking if the cell's potential value is valid with the division constraints
     * @param value - The potential value
     * @param eqTotal - The value the cells's values have to equal
     * @param cellsTotal - The total amount entered into the cells in the group already
     * @param allCellsFilled - A boolean determining if all the cells except the current have been assigned
     * @return - A boolean determining if the potential value is valid
     */
    public boolean checkDivision(String value, String eqTotal, int cellsTotal, boolean allCellsFilled) {
        boolean validValue = false;

        if (!allCellsFilled) {
            if ((Integer.parseInt(value) > cellsTotal)) {
                if ((Integer.parseInt(value) / cellsTotal) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                } else if (Integer.parseInt(value) * cellsTotal <= getBoardSize()) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal / Integer.parseInt(value)) >= Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
            // If the totals for the cells only equals 1, the value is true
            if (cellsTotal == 1) {
                validValue = true;
            }
        } else {
            if (Integer.parseInt(value) > cellsTotal) {
                if ((Integer.parseInt(value) / cellsTotal) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            } else {
                if ((cellsTotal / Integer.parseInt(value)) == Integer.parseInt(eqTotal)) {
                    validValue = true;
                }
            }
        }

        return validValue;
    }

    /**
     * The method for determining if the puzzle has been solved
     * @return - A boolean indicating that the puzzle has been solved
     */
    public boolean solved() {
        boolean solved = true;
        // For all of the cells in the puzzle
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                // If there is a cell not assigned
                if (getKenKenArray()[row][col] == 0) {
                    solved = false;
                }
                // If a cell assignment is not valid
                if (!checkRowsCols("" + row, "" + col, "" + getKenKenArray()[row][col])) {
                    solved = false;
                }
            }
        }

        return solved;
    }
}

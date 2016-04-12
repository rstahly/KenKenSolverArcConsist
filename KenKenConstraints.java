import java.util.*;

/**
 * @author Rachel Feddersen
 *
 * @since Date Started: 3/08/2016 - Date Finished: 3/26/2016
 *
 * The KenKenConstraints Class that contains the TreeMap holding the constraints and has methods for limiting
 * the domain.
 */
public class KenKenConstraints {
    KenKenDomain domain;

    private TreeMap<String, String[]> mathConstraints = new TreeMap<>();
    private int boardSize;

    /**
     * The constructor for the KenKenConstraints Class
     * @param d - The domain of the puzzle
     * @param b - The board size
     */
    public KenKenConstraints(KenKenDomain d, int b) {
        domain = d;
        boardSize = b;
    }

    /**
     * The method for creating the TreeMap of the constraints
     * @param values - The array of values of the constraints
     */
    public void createMathConst(String[] values) {
        String keyConst = values[0] + values[1];

        // Get the number of cells based on how many values were sent in the array
        int numOfCells = (int) Math.floor(values.length/2);
        String[] valueConst = new String[numOfCells];

        // For the number of number of cells
        for (int i = 1; i < numOfCells; i++) {
            valueConst[i-1] = values[i*2] + values[(i*2)+1];
        }

        // Get the last cell to put in the array
        valueConst[numOfCells-1] = values[(numOfCells*2)].substring(1, values[(numOfCells*2)].length());

        // Put the key and the values in the MathConstraint TreeMap
        getMathConstraints().put(keyConst, valueConst);
    }

    /**
     * The method for removing all of the domains if they are not equal to the value of the cell
     */
    public void removeEqualsDomain() {
        for(Map.Entry<String,String[]> entry : getMathConstraints().entrySet()) {
            if (entry.getValue().length == 1) {
                List<String> values = new ArrayList<>();
                values.add(entry.getValue()[0].substring(0, 1));
                domain.getKenKenDomain().replace(entry.getKey(), values);
            }
        }
    }

    /**
     * The method for removing values of the domain that are not possible
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value that the cells should add up to
     * @param kenKenArray - The current state of the board
     */
    public void removeAddValues(LinkedList<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;
        int compareValue;

        if (Integer.parseInt(value) < boardSize) {
            compareValue = Integer.parseInt(value);
        } else {
            compareValue = boardSize + 1;
        }

        for (String cells: cellGroup) {
            newDomain = new LinkedList<>();
            for (String domainList: domain.getKenKenDomain().get(cells)) {
                if (domain.getKenKenDomain().get(cells).size() != 1 &&
                kenKenArray[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] == 0) {
                    // If the domain is less than or equal to the size of the board
                    if (Integer.parseInt(domainList) < compareValue) {
                        newDomain.add(domainList);
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cells, newDomain);
            }
        }
    }

    /**
     * The method for removing values of the domain that are not possible
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param kenKenArray - The current state of the board
     */
    public void removeMultValues(LinkedList<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;

        for (String cells: cellGroup) {
            newDomain = new LinkedList<>();
            for (String domainList: domain.getKenKenDomain().get(cells)) {
                if (domain.getKenKenDomain().size() != 1 &&
                        kenKenArray[Integer.parseInt(cells.substring(0, 1))][Integer.parseInt(cells.substring(1, 2))] == 0) {
                    // If the value modulus divided by the domain is zero
                    if ((Integer.parseInt(value) % Integer.parseInt(domainList)) == 0) {
                        newDomain.add(domainList);
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cells, newDomain);
            }
        }
    }

    /**
     * The method for performing arc consistency on cells with addition equations
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param kenKenArray - The current state of the board
     */
    public void addArcConsistency(LinkedList<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;
        boolean domainValid;

        // For the cells in the cell group
        for (int i = 0; i < cellGroup.size(); i++) {
            newDomain = new LinkedList<>();
            // If the domain is not already size one and a value has not been assigned
            if (domain.getKenKenDomain().get(cellGroup.get(i)).size() != 1 &&
                    kenKenArray[Integer.parseInt(cellGroup.get(i).substring(0, 1))]
                            [Integer.parseInt(cellGroup.get(i).substring(1, 2))] == 0) {
                // For the list of domains for one cell
                for (String domainList : domain.getKenKenDomain().get(cellGroup.get(i))) {
                    domainValid = false;
                    // For the list of domains for the other cell
                    for (String otherDomains : domain.getKenKenDomain().get(cellGroup.get(1 - i))) {
                        // If the domains are not equal
                        if (!domainList.equals(otherDomains)) {
                            // If the domains add up to the correct value
                            if ((Integer.parseInt(domainList) + Integer.parseInt(otherDomains)) == Integer.parseInt(value)) {
                                domainValid = true;
                                break;
                            }
                        }
                    }
                    // If the domain was valid
                    if (domainValid) {
                        // Add it to the list
                        newDomain.add(domainList);
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cellGroup.get(i), newDomain);
            }
        }
    }

    /**
     * The method for performing arc consistency on cells with subtraction equations
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param kenKenArray - The current state of the board
     */
    public void subArcConsistency(LinkedList<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;
        boolean domainValid;

        // For the cells in the cell group
        for (int i = 0; i < cellGroup.size(); i++) {
            newDomain = new LinkedList<>();
            // If the domain is not already size one and a value has not been assigned
            if (domain.getKenKenDomain().get(cellGroup.get(i)).size() != 1 &&
                    kenKenArray[Integer.parseInt(cellGroup.get(i).substring(0, 1))]
                            [Integer.parseInt(cellGroup.get(i).substring(1, 2))] == 0) {
                // For the list of domains for one cell
                for (String domainList : domain.getKenKenDomain().get(cellGroup.get(i))) {
                    domainValid = false;
                    // For the list of domains for the other cell
                    for (String otherDomains : domain.getKenKenDomain().get(cellGroup.get(1 - i))) {
                        // If the domains are not equal
                        if (!domainList.equals(otherDomains)) {
                            // If the domains add up or subtract to the correct value
                            if ((Integer.parseInt(domainList) + Integer.parseInt(value)) == Integer.parseInt(otherDomains)) {
                                domainValid = true;
                                break;
                            } else if ((Integer.parseInt(domainList) - Integer.parseInt(value)) == Integer.parseInt(otherDomains)) {
                                domainValid = true;
                                break;
                            }
                        }
                    }
                    // If the domain was valid
                    if (domainValid) {
                        // Add it to the list
                        newDomain.add(domainList);
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cellGroup.get(i), newDomain);
            }
        }
    }

    /**
     * The method for performing arc consistency on cells with multiplication equations
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param kenKenArray - The current state of the board
     */
    public void multArcConsistency(LinkedList<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;
        boolean domainValid;

        // For the cells in the cell group
        for (int i = 0; i < cellGroup.size(); i++) {
            newDomain = new LinkedList<>();
            // If the domain is not already size one and a value has not been assigned
            if (domain.getKenKenDomain().get(cellGroup.get(i)).size() != 1 &&
                    kenKenArray[Integer.parseInt(cellGroup.get(i).substring(0, 1))]
                            [Integer.parseInt(cellGroup.get(i).substring(1, 2))] == 0) {
                // For the list of domains for one cell
                for (String domainList : domain.getKenKenDomain().get(cellGroup.get(i))) {
                    domainValid = false;
                    // For the list of domains for the other cell
                    for (String otherDomains : domain.getKenKenDomain().get(cellGroup.get(1 - i))) {
                        // If the domains are not equal
                        if (!domainList.equals(otherDomains)) {
                            // If the domains multiply to the correct value
                            if ((Integer.parseInt(domainList) * Integer.parseInt(otherDomains)) == Integer.parseInt(value)) {
                                domainValid = true;
                                break;
                            }
                        }
                    }
                    // If the domain was valid
                    if (domainValid) {
                        // Add it to the list
                        newDomain.add(domainList);
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cellGroup.get(i), newDomain);
            }
        }
    }

    /**
     * The method for performing arc consistency on cells with division equations
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param kenKenArray - The current state of the board
     */
    public void divArcConsistency(LinkedList<String> cellGroup, String value, int[][] kenKenArray) {
        List<String> newDomain;
        boolean domainValid;

        // For the cells in the cell group
        for (int i = 0; i < cellGroup.size(); i++) {
            newDomain = new LinkedList<>();
            // If the domain is not already size one and a value has not been assigned
            if (domain.getKenKenDomain().get(cellGroup.get(i)).size() != 1 &&
                    kenKenArray[Integer.parseInt(cellGroup.get(i).substring(0, 1))]
                            [Integer.parseInt(cellGroup.get(i).substring(1, 2))] == 0) {
                // For the list of domains for one cell
                for (String domainList : domain.getKenKenDomain().get(cellGroup.get(i))) {
                    domainValid = false;
                    // For the list of domains for the other cell
                    for (String otherDomains : domain.getKenKenDomain().get(cellGroup.get(1 - i))) {
                        // If the domains are not equal
                        if (!domainList.equals(otherDomains)) {
                            // If the domains divide or multiply to the correct value
                            if (((Integer.parseInt(domainList) % Integer.parseInt(value)) == 0)
                            && ((Integer.parseInt(domainList) / Integer.parseInt(value)) == Integer.parseInt(otherDomains))) {
                                domainValid = true;
                                break;
                            } else if ((Integer.parseInt(domainList) * Integer.parseInt(value)) == Integer.parseInt(otherDomains)) {
                                domainValid = true;
                                break;
                            }
                        }
                    }
                    // If the domain was valid
                    if (domainValid) {
                        // Add it to the list
                        newDomain.add(domainList);
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cellGroup.get(i), newDomain);
            }
        }
    }

    /**
     * The method for performing ternary constraint on a group of 3 cells
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param sign - The sign for the equation
     * @param kenKenArray - The current state of the board
     */
    public void ternaryConsistency(LinkedList<String> cellGroup, String value, String sign, int[][] kenKenArray) {
        boolean allInARowCol = false;

        if (cellGroup.get(0).substring(0, 1).equals(cellGroup.get(1).substring(0, 1)) &&
        cellGroup.get(1).substring(0, 1).equals(cellGroup.get(2).substring(0, 1))) {
            allInARowCol = true;
        } else if (cellGroup.get(0).substring(1, 2).equals(cellGroup.get(1).substring(1, 2)) &&
        cellGroup.get(1).substring(1, 2).equals(cellGroup.get(2).substring(1, 2))) {
            allInARowCol = true;
        }

        if (allInARowCol) {
            allInARowConstraint(cellGroup, value, sign, kenKenArray);
        } else {
            allNotInRowConstraint(cellGroup, value, sign, kenKenArray);
        }
    }

    /**
     * The method for performing ternary constraint on cells with addition equations
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param sign - The sign for the equation
     * @param kenKenArray - The current state of the board
     */
    private void allInARowConstraint(LinkedList<String> cellGroup, String value, String sign, int[][] kenKenArray) {
        List<String> newDomain;
        boolean domainValid;

        // For the cells in the cell group
        for (int i = 0; i < cellGroup.size(); i++) {
            newDomain = new LinkedList<>();
            // If the domain is not already size one and a value has not been assigned
            if (domain.getKenKenDomain().get(cellGroup.get(i)).size() != 1 &&
                    kenKenArray[Integer.parseInt(cellGroup.get(i).substring(0, 1))]
                            [Integer.parseInt(cellGroup.get(i).substring(1, 2))] == 0) {
                // For the list of domains for one cell
                for (String domains1 : domain.getKenKenDomain().get(cellGroup.get(i))) {
                    domainValid = false;
                    // For the cells in the cell group
                    for (int j = 0; j < cellGroup.size(); j++) {
                        // If we are looking at a different element than the first
                        if (j != i) {
                            // For the list of domains for one cell
                            for (String domains2 : domain.getKenKenDomain().get(cellGroup.get(j))) {
                                // If the domains are not equal
                                if (!domains1.equals(domains2)) {
                                    // For the cells in the cell group
                                    for (int k = 0; k < cellGroup.size(); k++) {
                                        // If we are looking at a different element than the first or second
                                        if (i != k && j != k) {
                                            // For the list of domains for the other cell
                                            for (String domains3 : domain.getKenKenDomain().get(cellGroup.get(k))) {
                                                if (!domains1.equals(domains3) && !domains2.equals(domains3)) {
                                                    // If the sign was an addition sign
                                                    if (sign.equalsIgnoreCase("+")) {
                                                        domainValid = checkTernaryAddition(Integer.parseInt(domains1),
                                                                Integer.parseInt(domains2), Integer.parseInt(domains3),
                                                                Integer.parseInt(value));
                                                        // If the sign was a multiplication sign
                                                    } else if (sign.equalsIgnoreCase("x")) {
                                                        domainValid = checkTernaryMultiplication(Integer.parseInt(domains1),
                                                                Integer.parseInt(domains2), Integer.parseInt(domains3),
                                                                Integer.parseInt(value));
                                                    }
                                                    if (domainValid) {
                                                        break;
                                                    }
                                                }
                                            }
                                            // If the domain was valid
                                            if (domainValid) {
                                                // Add it to the list
                                                newDomain.add(domains1);
                                                break;
                                            }
                                        }
                                    }
                                    // If the domain was valid
                                    if (domainValid) {
                                        break;
                                    }
                                }
                            }
                        }
                        // If the domain was valid
                        if (domainValid) {
                            break;
                        }
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cellGroup.get(i), newDomain);
            }
        }
    }

    /**
     * The method for performing ternary constraint on cells not in a row
     * @param cellGroup - The group of cells for a constraint
     * @param value - The value of the constraint for the math equation
     * @param sign - The sign for the equation
     * @param kenKenArray - The current state of the board
     */
    private void allNotInRowConstraint(LinkedList<String> cellGroup, String value, String sign, int[][] kenKenArray) {
        List<String> newDomain;
        boolean domainValid;

        // For the cells in the cell group
        for (int i = 0; i < cellGroup.size(); i++) {
            newDomain = new LinkedList<>();
            // If the domain is not already size one and a value has not been assigned
            if (domain.getKenKenDomain().get(cellGroup.get(i)).size() != 1 &&
                    kenKenArray[Integer.parseInt(cellGroup.get(i).substring(0, 1))]
                            [Integer.parseInt(cellGroup.get(i).substring(1, 2))] == 0) {
                // For the list of domains for one cell
                for (String domains1 : domain.getKenKenDomain().get(cellGroup.get(i))) {
                    domainValid = false;
                    // For the cells in the cell group
                    for (int j = 0; j < cellGroup.size(); j++) {
                        // If we are looking at a different element than the first
                        if (j != i) {
                            // For the list of domains for one cell
                            for (String domains2 : domain.getKenKenDomain().get(cellGroup.get(j))) {
                                // For the cells in the cell group
                                for (int k = 0; k < cellGroup.size(); k++) {
                                    // If we are looking at a different element than the first or second
                                    if (i != k && j != k) {
                                        // For the list of domains for the other cell
                                        for (String domains3 : domain.getKenKenDomain().get(cellGroup.get(2-i))) {
                                            // If the sign was an addition sign
                                            if (sign.equalsIgnoreCase("+")) {
                                                domainValid = checkTernaryAddition(Integer.parseInt(domains1),
                                                        Integer.parseInt(domains2), Integer.parseInt(domains3),
                                                        Integer.parseInt(value));
                                            // If the sign was a multiplication sign
                                            } else if (sign.equalsIgnoreCase("x")) {
                                                domainValid = checkTernaryMultiplication(Integer.parseInt(domains1),
                                                        Integer.parseInt(domains2), Integer.parseInt(domains3),
                                                        Integer.parseInt(value));
                                            }
                                            if (domainValid) {
                                                break;
                                            }
                                        }
                                        // If the domain was valid
                                        if (domainValid) {
                                            // Add it to the list
                                            newDomain.add(domains1);
                                            break;
                                        }
                                    }
                                }
                                // If the domain was valid
                                if (domainValid) {
                                    break;
                                }
                            }
                        }
                        // If the domain was valid
                        if (domainValid) {
                            break;
                        }
                    }
                }
            }

            // If values are added to the newDomain
            if (newDomain.size() > 0) {
                domain.getKenKenDomain().replace(cellGroup.get(i), newDomain);
            }
        }
    }

    /**
     * The method for checking the addition of three cells for the ternary constraint
     * @param domains1 - The value of the first domain
     * @param domains2 - The value of the second domain
     * @param domains3 - The value of the third domain
     * @param value - The total that needs to be reached
     * @return - A boolean to show if the equation was valid
     */
    private boolean checkTernaryAddition(int domains1, int domains2, int domains3, int value) {
        // Return if the domains add up to the correct value
        return ((domains1 + domains2 + domains3) == value);
    }

    /**
     * The method for checking the multiplication of three cells for the ternary constraint
     * @param domains1 - The value of the first domain
     * @param domains2 - The value of the second domain
     * @param domains3 - The value of the third domain
     * @param value - The total that needs to be reached
     * @return - A boolean to show if the equation was valid
     */
    private boolean checkTernaryMultiplication(int domains1, int domains2, int domains3, int value) {
        // Return if the domains multiply up to the correct value
        return ((domains1 * domains2 * domains3) == value);
    }

    /**
     * The method for retrieving the TreeMap containing the constraints
     * @return - The mathConstraints TreeMap
     */
    public TreeMap<String, String[]> getMathConstraints() {
        return mathConstraints;
    }
}

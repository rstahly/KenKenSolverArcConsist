import java.util.List;
import java.util.TreeMap;

/**
 * @author Rachel Feddersen
 *
 * @since Date Started: 3/08/2016 - Date Finished: 3/26/2016
 *
 * The KenKenDomain Class that contains the TreeMap holding all of the cells and their current domains.
 */
public class KenKenDomain {
    private TreeMap<String, List<String>> kenKenDomain = new TreeMap<>();

    /**
     * The method for setting the initial domain of the kenKenDomain
     * @param values - The list of cells and rows to be added to the TreeMap
     * @param domain - The list of domains that are possible for the cells
     */
    public void setInitialDomain(String[] values, List<String> domain) {

        int numOfCells = (int) Math.floor(values.length/2);

        for (int j = 0; j < numOfCells; j++) {
            String cell = values[j*2] + values[(j*2)+1];
            getKenKenDomain().put(cell, domain);
        }
    }

    /**
     * The method is used for making sure that there are cells for every spot in the puzzle. If there is not
     * a cell in the file for every spot, the file is invalid
     * @return - A boolean states whether or not the file is valid
     */
    public boolean checkAllCellsThere(int boardSize) {
        boolean invalidFile = false;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (!getKenKenDomain().containsKey(""+row+col)) {
                    invalidFile = true;
                }
            }
        }

        return invalidFile;
    }

    /**
     * The method for retrieving the current state of the kenKenDomain
     * @return - The current state of the kenKenDomain
     */
    public TreeMap<String, List<String>> getKenKenDomain() {
        return kenKenDomain;
    }
}

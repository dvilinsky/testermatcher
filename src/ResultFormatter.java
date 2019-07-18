import models.Tester;

import java.util.*;

/**
 * This class formats the results of a query. Since the results are essentially a table, the
 * output is formatted as such. The output will look like:
 * "First Name"   "Last Name"   "Experience"
 * "----------"   "---------"   "----------"
 * followed by the rows, if any, of data
 */

public class ResultFormatter {
    private static final String FORMAT = "%-10s %-10s %-10s\n";

    /**
     *
     * @param testers List of testers that are the result of a query
     * @return Formatted string of results in the form described in the class-level comment
     */
    public static String format(List<Tester> testers)  {
        List<TesterDisplayObject> testerDisplayObjects = collapseResults(testers);
        StringBuilder result = new StringBuilder();
        result.append(String.format(FORMAT, "First Name", "Last Name", "Experience"));
        result.append(String.format(FORMAT, "----------", "---------", "----------"));
        for (TesterDisplayObject tdo : testerDisplayObjects) {
            result.append(String.format(FORMAT, tdo.firstName, tdo.lastName, tdo.numBugs));
        }
        return result.toString();
    }

    /**
     * Due to the way the data is structured, a result set may have two testers with identical names, but different
     * bug lists. However, we don't want to display the "same" tester twice, each time with some number of bugs. We
     * want to display that tester only once, with  the total number of bugs that tester has identified. This method
     * accomplishes that.
     * @param testers List of testers wth potential duplicates to collapse
     * @return List of TesterDisplayObjects, sorted by number of bugs identified
     */
    private static List<TesterDisplayObject> collapseResults(List<Tester> testers) {
        Map<String, Integer> trueBugCount = new HashMap<>();
        for (Tester tester : testers) {
            String name = tester.getFirstName() + " " + tester.getLastName();
            if (trueBugCount.containsKey(name)) {
                trueBugCount.put(name, trueBugCount.get(name) + tester.getBugs().size());
            } else {
                trueBugCount.put(name, tester.getBugs().size());
            }
        }
        List<TesterDisplayObject> testerDisplayObjects = new ArrayList<>();
        trueBugCount.forEach((name, numBugs) -> {
            String[] names = name.split(" ");
            testerDisplayObjects.add(new TesterDisplayObject(names[0], names[1], numBugs));
        });
        Collections.sort(testerDisplayObjects);
        return testerDisplayObjects;
    }

    /**
     * This class models the data we want to display to the end user; that is, first name, last name, and number of
     * bugs identified
     */
    private static class TesterDisplayObject implements Comparable<TesterDisplayObject>{
        private String firstName;
        private String lastName;
        private int numBugs;

        public TesterDisplayObject(String firstName, String lastName, int numBugs) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.numBugs = numBugs;
        }

        @Override
        public int compareTo(TesterDisplayObject o) {
            return o.numBugs - numBugs;
        }
    }

}

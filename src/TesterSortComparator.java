import models.Tester;

import java.util.Comparator;

/**
 * This class defines an ordering between two testers such that one tester is "greater than" another if that tester
 * has identified more bugs than the other
 */
public class TesterSortComparator implements Comparator<Tester> {
    @Override
    public int compare(Tester o1, Tester o2) {
        return o2.getBugs().size() - o1.getBugs().size();
    }
}

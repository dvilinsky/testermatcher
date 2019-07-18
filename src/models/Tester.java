package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a tester in this application.
 */
public class Tester {
    private int id;
    private String firstName;
    private String lastName;
    //Bugs this tester has identified
    private List<Bug> bugs;

    /**
     * Constructor for this class
     * @param id The tester's id
     * @param firstName The tester's firstName
     * @param lastName The tester's lastName
     */
    public Tester(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        bugs = new ArrayList<>();
    }

    public void addBug(Bug bug) {
        bugs.add(bug);
    }

    /**
     *
     * @return A copy of this Tester- different memory locations, same value
     */
    public Tester copy() {
        return new Tester(id, firstName, lastName);
    }

    public List<Bug> getBugs() {
        return bugs;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Two testers are equal if they have the same id and the same bug list. To see why I chose to define equality in this
     * way, see the comments at TesterRepository::retainAll
     * @param obj The tester to which to compare this tester
     * @return True if this tester and the other have the same id and bug list, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Tester)) {
            return false;
        }
        Tester other = (Tester) obj;
        return other.getId() == this.id && other.getBugs().equals(this.bugs);
    }
}

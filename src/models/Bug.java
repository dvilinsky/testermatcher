package models;

/**
 * Models a bug in the application
 */
public class Bug {
    //The tester who identified this bug
    private Tester tester;
    private int id;
    //Device on which this bug was identified
    private String deviceName;

    /**
     * Constructor for this class
     * @param id The bug id
     * @param tester The tester who identified the bug
     * @param deviceName The device on which the bug was identified
     */
    public Bug(int id, Tester tester, String deviceName) {
        this.id = id;
        this.tester = tester;
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Tester getTester() {
        return tester;
    }

    public int getId() {
        return id;
    }

    /**
     * Two bugs are equal if their ids are equal
     * @param obj Bug to which to compare this bug
     * @return True if this bug and the othe bug have the same id, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Bug)) {
            return false;
        }
        Bug other = (Bug) obj;
        return other.getId() == this.id;
    }
}

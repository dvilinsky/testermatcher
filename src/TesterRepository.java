import models.Bug;
import models.Tester;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class handles the storage of tester objects and provides a few means to access them based on
 * certain search criteria. All results are returned as a list of Tester objects, sorted on the number of bugs that
 * tester has identified
 */
public class TesterRepository {
    //Mapping from each country to a list of testers based in that country
    private Map<String, List<Tester>> countryToTester;

    //Mapping from each device to a list of testers who have identified bugs on that device
    private Map<String, List<Tester>> deviceToTester; //to build loop over tester_device file

    //Mapping from a testerId to a corresponding tester object. Mainly used for the "find all" case
    private Map<Integer, Tester> testers;

    //Mapping from a device's id to its name
    private Map<Integer, String> deviceMap;

    /**
     * Constructor for this class
     * @param fileMap Mapping of command line options to that option's file parameter
     * @throws FileNotFoundException Thrown when file that user inputs does not exist
     */
    public TesterRepository(Map<String, File> fileMap) throws FileNotFoundException {
        countryToTester = new HashMap<>();
        deviceToTester = new HashMap<>();
        testers = new HashMap<>();
        deviceMap = new HashMap<>();
        buildMappings(fileMap);
    }

    /**
     * Finds a list of testers either by a list of countries, or devices, but not both
     * @param params List of countries or devices by which to search for tester
     * @param country If true then the input list "params" is a list of countries, list of devices otherwise
     * @return A list of testers matching the given search criteria
     */
    public List<Tester> findByCountryOrDevice(List<String> params, boolean country) {
        List<Tester> result = new ArrayList<>();
        for (String param : params) {
            if (country) {
                List<Tester> l = countryToTester.get(param);
                if (l != null) {
                    result.addAll(l);
                }
            } else {
                List<Tester> l = deviceToTester.get(param);
                System.out.println();
                if (l != null) {
                    result.addAll(l);
                }
            }
        }
        result.sort(new TesterSortComparator());
        return result;
    }

    /**
     * Finds all testers in this repository
     * @return A list of all testers in this repository, sorted by the number of bugs identified
     */
    public List<Tester> findAll() {
        List<Tester> result = new ArrayList<>(testers.values());
        result.sort(new TesterSortComparator());
        return result;
    }

    /**
     * Finds testers in this repository by both country and device. This works by finding a set of testers by the
     * given countries, a separate set by the given devices, and then taking the intersection of this set.
     * @param devices List of devices by which to search
     * @param countries List of countries by which to search
     * @return List of testers matching both the devices and countries search criteria
     */
    public List<Tester> findByCountryAndDevice(List<String> devices, List<String> countries) {
        Set<Tester> testersInCountry = new HashSet<>(findByCountryOrDevice(countries, true));
        Set<Tester> testersInDevice = new HashSet<>(findByCountryOrDevice(devices, false));
        testersInDevice = retainAll(testersInDevice, testersInCountry);
        List<Tester> result = new ArrayList<Tester>(testersInDevice);
        result.sort(new TesterSortComparator());
        return result;
    }

    /**
     * Implements set intersection for two sets of testers. The reason we need a custom function and cannot use
     * the buitlin Set::retainAll has to do with the way Tester::equals is implemented. Tester::equals defines two
     * testers to be equal iff their ids are equal and their bug lists are equal. This allows to add multiple "Taybin Rutkin"s,
     * for example, to a Set, one for each device he has identified bugs on; even though they are the same tester in the
     * real world, they are modeled as different testers in this application due to having separate bug lists. If we were
     * to call Set::retainAll on them, they would not be equal, and so not retained.
     * @param retaining "Outer" set in the intersection operation
     * @param others "Inner" set in the intersection operation
     * @return Set containing all elements in retaining that are also in others
     */
    private Set<Tester> retainAll(Set<Tester> retaining, Set<Tester> others) {
        Set<Tester> result = new HashSet<>();
        for (Tester tester : retaining) {
            for (Tester other : others) {
                if (tester.getId() == other.getId()) {
                    result.add(tester);
                }
            }
        }
        return result;
    }

    /**
     * Builds the data structures this repository uses to store the data
     * @param fileMap Mapping from user input options to the corresponding files that were entered
     * @throws FileNotFoundException Thrown when a given file does not exist
     */
    private void buildMappings(Map<String, File> fileMap) throws FileNotFoundException {
        buildCountryToTesterAndTesterMap(fileMap);
        buildDeviceToTester(fileMap);
        addBugs(fileMap);
    }

    /**
     * Creates the deviceMap and the deviceToTester mapping.
     * @param fileMap Mapping from user input options to the corresponding files that were entered
     * @throws FileNotFoundException Thrown when an inputted file does not exist
     */
    private void buildDeviceToTester(Map<String, File> fileMap) throws FileNotFoundException {
        Scanner deviceScanner = new Scanner(fileMap.get("-d"));
        deviceScanner.nextLine(); //advance past column line
        while (deviceScanner.hasNextLine()) {
            List<String> line = splitLine(deviceScanner.nextLine());
            deviceToTester.put(line.get(1).toLowerCase(), new ArrayList<>());
            deviceMap.put(Integer.parseInt(line.get(0)), line.get(1).toLowerCase());
        }
        Scanner testerDeviceScanner = new Scanner(fileMap.get("-m"));
        testerDeviceScanner.nextLine();
        while (testerDeviceScanner.hasNextLine()) {
            List<String> line = splitLine(testerDeviceScanner.nextLine());
            String deviceName = deviceMap.get(Integer.parseInt(line.get(1)));
            //We need a copy of the tester object here so that when we call filterDeviceToTester, only the testers
            //in the deviceToTester map have their bugs filtered
            deviceToTester.get(deviceName).add(testers.get(Integer.parseInt(line.get(0))).copy());
        }
    }

    /**
     * Creates the countryToTester and tester mappings. I chose to make this one method because even though it is
     * really doing two things, this way, we only have to read the testers.csv file once.
     * @param fileMap Mapping from user input options to their corresponding values.
     * @throws FileNotFoundException Thrown when given file does not exist
     */
    private void buildCountryToTesterAndTesterMap(Map<String, File> fileMap) throws FileNotFoundException{
        Scanner testerScanner = new Scanner(fileMap.get("-t"));
        testerScanner.nextLine(); //advance past column line
        while (testerScanner.hasNextLine()) {
            List<String> line = splitLine(testerScanner.nextLine());
            Tester tester = new Tester(Integer.parseInt(line.get(0)), line.get(1), line.get(2));
            testers.put(Integer.parseInt(line.get(0)), tester);
            String country = line.get(3);
            if (countryToTester.containsKey(country.toLowerCase())) {
                List<Tester> temp = countryToTester.get(country.toLowerCase());
                temp.add(tester);
                countryToTester.put(country.toLowerCase(), temp);
            } else {
                countryToTester.put(country.toLowerCase(), new ArrayList<Tester>(Arrays.asList(tester)));
            }
        }
    }

    /**
     * After creating the mappings from countries and devices to the right testers, each tester does not have any
     * bugs in its bug list. This method reads bugs.csv and adds each bug to the correct tester
     * @param fileMap Mapping from user input options to the corresponding file for that option
     * @throws FileNotFoundException Thrown when a given file does not exist
     */
    private void addBugs(Map<String, File> fileMap) throws FileNotFoundException{
        Scanner bugScanner = new Scanner(fileMap.get("-b"));
        bugScanner.nextLine();  //advance past column line
        while (bugScanner.hasNextLine()) {
            List<String> line = splitLine(bugScanner.nextLine());
            Tester countryTester = testers.get(Integer.parseInt(line.get(2)));
            Bug bug = new Bug(Integer.parseInt(line.get(0)), countryTester, deviceMap.get(Integer.parseInt(line.get(1))));
            countryTester.addBug(bug);
            List<Tester> deviceTesters = deviceToTester.get(bug.getDeviceName());
            deviceTesters.forEach(tester -> {
                //This check ensures that a bug identified on a given device by a given tester is only assigned to that tester
                if (tester.getId() == bug.getTester().getId()) {
                    tester.addBug(bug);
                }
            });
        }
    }

    /**
     * Splits a line in a csv file  removes any quotes around the fields
     * @param line Line in a csv file
     * @return List of the form [col1, col2, ...], where each col is a value in a csv file
     */
    private List<String> splitLine(String line) {
        return Arrays.stream(line.split(",")).map(s -> s.replace("\"", ""))
                .collect(Collectors.toList());
    }

}
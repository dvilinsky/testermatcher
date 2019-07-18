import models.Tester;

import java.util.List;

/**
 * This class handles the routing of a query to the correct repository operation
 */
public class QueryExecutor {
    private TesterRepository testerRepository;

    /**
     * Constructor for this class
     * @param testerRepository Repository on which to perform the queries
     */
    public QueryExecutor(TesterRepository testerRepository) {
        this.testerRepository = testerRepository;
    }

    /**
     * Routes a query, executes the query, returns the result
     * @param countries List of countries by which to search
     * @param devices List of devices by which to search
     * @return Result of executing the query on the given search fields, sorted by the number of bugs each tester has identified
     */
    public List<Tester> executeQuery(List<String> countries, List<String> devices) {
        String country0 = countries.get(0);
        String device0 = devices.get(0);
        List<Tester> resultSet;
        //When either country0 or device0 are empty or all, that means the end user has not chosen to search for
        //testers by country or device
        if ((country0.isEmpty() || country0.equals("all")) && (device0.isEmpty() || device0.equals("all"))) {
            return testerRepository.findAll();
        } else if (country0.isEmpty() || country0.equals("all")) {
            return testerRepository.findByCountryOrDevice(devices, false);
        } else if (device0.isEmpty() || device0.equals("all")) {
            return testerRepository.findByCountryOrDevice(countries, true);
        } else {
            return testerRepository.findByCountryAndDevice(devices, countries);
        }
    }
}

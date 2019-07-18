# Running the code
1. Clone the repository 
2. Navigate to the directory into which you cloned the repository
3. Run `javac src/*.java`
4. Run `java TesterMatcherMain`. It expects four command line arguments:

    1. -b: Bug csv file
    2. -t Tester csv file
    3. -d Device csv file
    4. -m Tester_Device csv file 
      
5. Enjoy

# Discussion
## Using the program
This client operates as a simple REPL. When you run the code, you will be presented with the following two statements:

`Enter a country or a comma-separated list of each country, or all for every country (enter to skip, \q to quit)
 `
 
 `country:`
 
 
 `Enter a device or a comma-separated list of each device, or all for every device (enter to skip, \q to quit)`
 
` device:`

A couple of notes on this:
1. When you hit enter, the code interprets this as meaning you do not intend to search by that field, and is equivalent to 
entering "all" for that field
2. The comma-separated list of values accepts a pretty broad range of input forms. So, queries like `GB,US` or `GB, US` or 
`gb,us` will work. Please do not include quotes around each country.

## Returned data
Provided you have inputted the data correctly, the REPL will return a table-like view with
three columns: First Name, Last Name, and Bugs. Bugs refers to the number of bugs this tester has identified on the given devices,
and is the key on which the view is sorted.

Note that if you incorrectly enter a country or device, for example "GBB" when you want "GB", the program does
not consider that an error. It would simply return the testers in that country, which in this case is none, because there are 
no testers in "GBB".

## Implementation details
The way I understood the requirements, there are four cases for the kind of search a user can perform:
1. Country=ALL and Device=All
2. Country=ALL and Device=[SOME DEVICE(s)]
3. Country=[SOME COUNTRY(s)] and Device=All
4. Country=[SOME COUNTRY(s)] and Device[SOME DEVICE(s)]

With that in mind, `TesterRepository` exposes three public methods, each 
corresponding to one or more of the above cases:
    
    1. findAll: case 1
    2. findByCountryOrDevice: case 2 or 3
    3. findByCountryAndDevice: case 4
    
### Structure of the Data
The most important thing to understand about the way the data is structured is how this program conceives of a tester. Let's
take the tester "Taybin Rutkin." In the real world, he is only one tester. Internally, however, 
there are multiple tester objects associated with this tester - one containing a list of every bug he has identified,
and one for every device on which he has identified bugs. 

I chose this approach because it allows for direct lookup of a tester, without any post processing. For example, for the search 
query "Device=iPhone 4," we can immediately perform a lookup for the testers on that device in `TesterRepository`. We do not 
need to filter the entire set of testers, and, since each internal tester maintains a list of bugs they have identified on that 
device, we can immediately return this object for display. This does mean, however, that
we need to pre-filter every tester for every device. I contend that this is advantageous to filtering
on every search because this only happens once, on starting the program, and will
ultimately lead to a faster program.

The downside of this approach is that it is less space efficient; one real-world tester
is mapped onto multiple internal testers. There is also bug duplication; a bug that shows up in the mapping from country
to tester would also show up in the mapping between that bug's device and that device's list of testers. Down the road,
if this app ever allows for updating testers, this could lead to data inconsistency. For now, 
as a write-only application, though, it works.

## Areas for improvement
The `TesterRepository` suffers from an extensibility problem. Right now, it (basically) exposes 
separate endpoints for each of the cases described above. But what if we wanted to add a third 
search dimension, say age? There are then 8 cases. Following the pattern, we would need to 
expose additional endpoints for these cases. There would also need to be additional mappings. This problem 
only gets worse as we continue to allow for new search criteria. 

Outside of this, the code could stand to do more input validation, especially around the command line 
arguments

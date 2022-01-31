# Housie

### Tickets:
*	Each Player has ticket
*	Ticket: N rows and M columns -  default (3*9)
*	Each Ticket should have unique numbers
*	Values (1-n) default: 1-90
*	Mark a number if Dealer picks the number.
*	Always look for early five / first line / Full House.

### Dealer:
*	Calls number one at a time.
*	Each number should be called only once. (1-n , default 90)
*	Should pick a Number if input is N or quit game if input is Q or any  player get a Full House.

###  Wining Combos:
*	Full House : mark all number
*	Early Five : Mark any first 5 numbers
*	First Line : Tickets with First line

###  Inputs:
* Number range: (1-n) default 1-90
* Number of players: N – default 2
* Tickets Size: N rows and M cols:  default: 3*9
* Game starts: Enter “N” / “Q“ to generate a number or quit game.

*Singleton Pattern* – As we need only one Dealer to start the game and moderate it. We can restrict creation of multiple moderator objects in the application by following Singleton Pattern.

*Observer Pattern* – To create an interaction between the dealer and n-players, as dealer resets the status and must notifies the players to respond for the changes. Hence, we can say that Players are the observers here.

### Package Structure:
 
**Player Interface**: game entity
* HousiePlayer implements the Player Interface and implements the methods as per the requirements.
* Player Interface can also be used for implementing different type of Ticketing games such as Lottery, Bingo etc.,

**Ticket Interface:** game entity
* HousieTicket which is associated with all players is the type of ticket used in Housie. We can also use this Interface to create different tickets as per the game strategy.
* This interface has two method declarations:

```java
   void setId(int id); // to set the TicketId.
   void setTicketValue(int[][] ticket); // To set the ticket values.
```

**HousieTicket:**  HousieTicket is the implementation of Ticket Interface. It implements the method setId and setTicketValue

```java
    private int id;					// ticketId
    private int[][] ticket;		// housieTicket
    private int[] numbersEachRow;// numbers per each row – user input
    private int totalNumbers;    // total numbers in the generated ticket
    private int matchedNums;		// Announced Numbers matched by dealer
````

````
Ticket Structure:
|=======================================|
|  2|   |   |   |   |   | 54| 64| 70| 76|
|=======================================|
|   | 10|   |   | 31|   | 56| 65|   | 82|
|=======================================|
|   |   | 20| 24|   |   | 60|   | 73| 84|
|=======================================|

````

**TicketGenerator:**

TicketGenerator class has two methods:

*createHousieTickets* : public method
**Inputs:**
*	Row and Columns
*	Minimum and maximum possible numbers (range)
*	numbersPerRow(assumption) 
*	ticketCount (number of Tickets that needs to be generated) equal to number of players.
**Output:**
   Array of Tickets.

*generateHousieTicketMatrix:* private method used in the createHousieTickets to arrange the values in the ticket.
**Inputs:**
*	rows and columns in the Ticket
*	Minimum and maximum possible numbers (range)
*	NumbersPerRow.
**Output:**
*	Two-dimensional Integer array.

*Implementation Details:*
Generates a distinct element between the given min and max number(inclusive) to fill the all the values in ticket in a sequence that follows as:
Ascending order from left -> right and top -> bottom to make our search easier while looking for the number.

**HousieExchange:**
This class object is shared between Players and Dealer, as it contains all the data related to the game.

``` Java
	private char nextAction;
	private boolean gameOver;
	private boolean hasWinner;
	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private Lock writeLock = lock.writeLock(); //writeLock can be acquired only by one thread.
	private Lock readLock = lock.readLock(); // readLock can be acquired by multiple readers at a time.
	private boolean[] playingStatus; //for tracking player status every round, player updates it after looking for number and Dealer resets it after announcing the number indicating that dealer picked a number.

	private List<Integer> numbersAnnounced; // list of announced Numbers in sequence as they were picked.
	private int currentNumber; // number picked at the current round.

	// details above early Five, FirstRow and Full house. Can be extended to add another combination as well.
	private List<Integer> winnerValue;
	private List<String> winners;
	private List<String> winningCombinations;

	// when an instance of this class is created, all the integers are generated between the min(default 1) and maxPossibleNumber and shuffle using the collections method: shuffle() and save the values in this list in the constructor.
	private List<Integer> availableNumbers;
```

**HousieDealer:**
We have only one instance of dealer in the game:

Algorithm:
* If the game is not over:

	o	Acquire the writeLock ( only one thread can have a writeLock)
	o	Announce a number
	o	Reset all player’s status to false
	o	Unlocks the writeLock.
	o	Sleeps until all the players finished updating the status to true.
	o	Repeat.

**HousiePlayer:**
We have multiple players in the game (1- N) which all share the same HousieExchange object (also dealer)
Algorithm:
*	If the game is not over:

	o	Check if it the turn to play
	o	If the current player game status is true, sleep until the Dealer updates it
	o	If false, acquire the read lock (multiple threads can access the readLock in parallel)
	o	Search the announcedNumber which is updated in housieExhangeData.
	o	If any match with the winningCombinations, update in housieExchange (only one player can update it, hence synchronized that method).
	o	Set the PlayergameStatus to True.
	o	Repeat.

**HousieMain:** 
This is the driver class that gathers information and instantiates all the objects required for the game. And start the dealer and player threads.

*Inputs from console:*
*	Number of players :  Default is 2 (assumption)
*	Maximum number in the number: default is 90 (minimum always 1)
*	Number of Rows (default : 3)
*	Number of Columns (default: 10)
*	Number of elements to be present in a row: (assumption, which should be always between (1-columns))

*After collecting user input:*
*	We generate 1 ticket for each player using TicketGenerator class method: createHousieTickets
*	Create and start each Player threads.
*	Create and start Dealer thread.
*	And Wait until the all the threads die gracefully.

## Assumptions:
*	Minimum number players required to play: 2
*	User should provide the non-empty numbers in each row.
*	Max N in the game should be greater than 20 (for a valid game)
*	Ticket values should be more than Number of rows*cols 
* 	Total count of visible numbers should be greater than or >= 5 to get all kinds of Winning Combinations.
*	All the values in the ticket are arranged in the ascending order from left-> right and top -> bottom for easier search using an algorithm.

### Steps To Execute:

* Make sure to have JDK and/or JRE installed
* Execute the Following Commands -

````
$ git clone https://github.com/stirunagaru/Housie.git
`````

* Using any IDE such as Eclipse, Intellij, STS etc.., import the project in IDE.
* Run the HousieMain.java class or in package test/com/game/housie: there are two test classes: HousieMainTest.java and HousieMainQuitBeforeGameEnd.java.
 these two classes can be run without giving any user inputs from console, by modifying the hard-coded values.

## Test Runs:
**Test-run-01.txt:**

*	Number of players: 20
*	Maximum number(n):  30
*	Number of Rows: 3
*	Number of Columns: 8
*	Number of non-empty cells for each row, between (1-8): 6

**Test-run-02.txt:**

*	Number of players: 2
*	Maximum number(n):  15
*	Number of Rows: 2
*	Number of Columns: 7
*	Number of non-empty cells for each row, between (1-8): 7

**Test-run-03.txt:**

*	Number of players: 100
*	Maximum number(n):  90
*	Number of Rows: 3
*	Number of Columns: 10
*	Number of non-empty cells for each row, between (1-8): 5

For the test-runs above, hard-coded : N in the main method for testing.

For the below test-run, added the user-input scenario in which we enter Q before ending game gracefully.

**Test-run-04.txt:**

*	Number of players: 1200
*	Maximum number(n):  30
*	Number of Rows: 3
*	Number of Columns: 9
*	Number of non-empty cells for each row, between (1-8): 5

Game ends before we have winners for First Row and Full House.

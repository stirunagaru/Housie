package com.game.housie;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.game.entity.Player;
import com.game.entity.Ticket;
import com.game.housie.common.HousieExchange;
import com.game.housie.dealer.HousieDealer;
import com.game.housie.player.HousiePlayer;
import com.game.housie.ticket.TicketGenerator;

public class HousieMain {
	public static void main(String args[]) {
		String welcomeString = 
						  ",--.  ,--.                          ,--.\n"
						+ "|  '--'  |  ,---.  ,--.,--.  ,---.  `--'  ,---.  \n"
						+ "|  .--.  | | .-. | |  ||  | (  .-'  ,--. | .-. : \n"
						+ "|  |  |  | ' '-' ' '  ''  ' .-'  `) |  | \\   --. \n"
						+ "`--'  `--'  `---'   `----'  `----'  `--'  `----' \n";
		System.out.println(welcomeString);
		System.out.println("\n\n");
		Scanner input = new Scanner(System.in);

		System.out.println(">> Enter the number of players participate in the game (greater than 1)");
		int numOfPlayers = 2;
		while(!input.hasNextInt() || (numOfPlayers = input.nextInt()) < 2) {
			System.out.println("*** Please enter a valid players, between (2-25)");
			if(!input.hasNextInt()) input.nextLine();
		}

		System.out.println(">> Enter the maximum number(n) in the ticket with range(1-n) where n is greater than 9");
		int maxN = 16;
		if(input.hasNextLine() && !input.hasNextInt()) {
			System.out.println("*** Maximum number defaulted: 90, range: (1-90)");
			maxN = 90;
			input.nextLine();
		} else {
			while((maxN = input.nextInt()) < 10) {
				System.out.println("*** Please enter a valid range, greater than 20");	
			}
		}

		System.out.println(">> Enter number of rows on the ticket, between (1-5)");
		int rows = 3;
		while(!input.hasNextInt() || (rows = input.nextInt()) <= 0) {
			System.out.println("*** Please enter a valid row, between (1-5)");
			input.nextLine();
		}

		System.out.println(">> Enter number of columns for the tickets, between (6-10)");
		int cols = 5;
		while(!input.hasNextInt() || (cols = input.nextInt()) <= 5) {
			System.out.println("*** Please enter a valid column, between (6-10)");
			input.nextLine();
		}

		//assumption
		System.out.println(">> Enter the number of non empty cells for each row, between (1-" + cols + ")");
		int numbersPerRow = 3;
		while(!input.hasNextInt() || (numbersPerRow = input.nextInt()) > cols || numbersPerRow <= 0) {
			System.out.println("*** please enter a valid number, between (1-" + cols + ")");
			input.nextLine();
		}

		if(maxN <= rows * cols) {
			System.out.println("**Please enter maximum ticket numbers greater than total numbers possile i.e. [(" + rows + "*" + cols + ")+1 = " + (rows * cols + 1) + "], And Try Again!!");
			input.close();
			return;
		}

		HousieExchange exchange = new HousieExchange(numOfPlayers, rows, cols, numbersPerRow, maxN);
		Ticket[] tickets = TicketGenerator.createHousieTickets(rows, cols, 1 , maxN, numbersPerRow, numOfPlayers);
		List<Player> players= new ArrayList<>();
		for(int i = 0; i < numOfPlayers; i++) {
			Player player = new HousiePlayer(exchange);
			player.setId(i);
			player.setTicket(tickets[i]);
			player.participate();
			players.add(player);
		}

		HousieDealer dealer = HousieDealer.getDealer(exchange);
		dealer.beginGame();

		while(!exchange.isGameOver()) {
			if(exchange.getNextAction() == 'X' && !exchange.isPlaying()) {
				try {
					exchange.getWriteLock().lock();
					System.out.println("\n>> Enter the `N` to continue, `Q` to quit");
					while( !(exchange.getNextAction() == 'N' || exchange.getNextAction() == 'Q')) {
						String action = input.nextLine(); // "N";
						if(action == null || action.isEmpty() || action.length() > 1 || !(action.equals("Q") ||action.equals("N"))) {
							System.out.println("*** please enter `Q` or `N`");
							continue;
						}
						exchange.setNextAction(action.charAt(0));
					}
				} finally {
					exchange.getWriteLock().unlock();
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					System.err.println("Exception in Main Thread: " + e.getMessage());
				}
			}
		}

		input.close();
		dealer.endGame();
		for(Player player : players) {
			player.quitGame();
		}
	}
}
package com.game.housie;

import java.util.ArrayList;
import java.util.List;

import com.game.entity.Player;
import com.game.entity.Ticket;
import com.game.housie.common.HousieExchange;
import com.game.housie.dealer.HousieDealer;
import com.game.housie.player.HousiePlayer;
import com.game.housie.ticket.TicketGenerator;

public class HousieMainTest {
	public static void main(String args[]) {
		int numOfPlayers = 2;
		int maxN = 30;
		int rows = 3;
		int cols = 10;
		int numbersPerRow = 5;
		System.out.println("Inputs: ");
		System.out.println(">> Number of players participate in the game : "  + numOfPlayers);
		System.out.println(">> The maximum number(n) in the ticket with range(1-n) where n is greater than 9: " + maxN);
		System.out.println(">> Number of rows on the ticket, between (1-5): "  + rows);
		System.out.println(">> Number of cols the ticket, between (6-9): "  + cols);
		System.out.println(">> Number of non empty cells for each row, between (1-" + cols + ") : " + numbersPerRow);

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
						String action = "N";
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

		dealer.endGame();
		for(Player player : players) {
			player.quitGame();
		}
	}
}

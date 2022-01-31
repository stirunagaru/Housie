package com.game.housie.ticket;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import com.game.entity.Ticket;

public class TicketGenerator {

	public static Ticket[] createHousieTickets(int row, int col, int min, int max, int numbersPerRow, int ticketCount) {
		Ticket[] tickets  = new Ticket[ticketCount];
		for(int i = 0; i < ticketCount; i++) {
			tickets[i] = new HousieTicket();
			tickets[i].setId(i+1);
			tickets[i].setTicketValue(generateHousieTicketMatrix(row, col, min, max, numbersPerRow));
			((HousieTicket)tickets[i]).setTotalNumbers((col) * numbersPerRow);
			int[] numEachRow = new int[row];
			((HousieTicket)tickets[i]).setNumbersEachRow(numEachRow);
		}
		return tickets;
	}

	private static int[][] generateHousieTicketMatrix(int row, int col,int min, int max, int numbersPerRow) {
		Set<Integer> set = ThreadLocalRandom.current().ints(min, max+1).distinct().limit(row*col).collect(TreeSet::new, TreeSet::add, TreeSet::addAll);
		int[][] ticketValues = new int[row][col];
		int i = 0;
		int j = 0;
		// Insert all possible numbers into the ticket - arranged in ascending order from left -> right, top -> bottom, for searching easy.
		for(int val : set) {
			ticketValues[i][j] = val;
			i = i + 1 == row ? 0 : i+1;
			j = i == 0 ? j + 1 : j;
		}

		if(numbersPerRow == col) 
			return ticketValues;

		// Delete extra numbers to make each row has same size(numbersPerRow)
		for(i= 0; i < row; i++) {
			Set<Integer> colIndexes = ThreadLocalRandom.current().ints(0, col-1).distinct().limit(col - numbersPerRow).collect(HashSet::new, HashSet::add, HashSet::addAll);
			for(int colIndex : colIndexes) {
				ticketValues[i][colIndex] *= -1; // marking negative to make the search easy in the later steps.
			}
		}
		return ticketValues;
	}
}
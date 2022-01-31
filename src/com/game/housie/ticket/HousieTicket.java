package com.game.housie.ticket;

import java.util.Arrays;

import com.game.entity.Ticket;

public class HousieTicket implements Ticket {
	private int id;
	private int[][] ticket;
	private int[] numbersEachRow;
	private int totalNumbers;
	private int matchedNums;

	@Override
	public void setId(int id) {       
		this.id = id;
	}

	@Override
	public void setTicketValue(int[][] ticket) {
		if(ticket == null || ticket.length == 0)
			throw new RuntimeException("Ticket can't be undefined!");
		this.ticket = ticket;
	}

	public void setNumbersEachRow(int[] numbersEachRow) {
		this.numbersEachRow = numbersEachRow;
	}

	public void setTotalNumbers(int totalNumbers) {
		this.totalNumbers = totalNumbers;
	}

	public void setMatchedNums(int matchedNums) {
		this.matchedNums = matchedNums;
	}

	public int getId() {
		return this.id;
	}

	public int[][] getTicketValue() {
		return this.ticket;
	}

	public int[] getNumbersEachRow() {
		return numbersEachRow;
	}

	public int getTotalNumbers() {
		return totalNumbers;
	}

	public int getMatchedNums() {
		return matchedNums;
	}

	@Override
	public String toString() {
		StringBuilder currentTicket =  new StringBuilder();
		currentTicket.append(" Ticket ID: " + this.id + "\n");
		StringBuilder line = new StringBuilder();
		for( int j = 0; j < this.ticket[0].length; j++) {
			line.append("====");
		}
		line.deleteCharAt(0);
		currentTicket.append("|" + line + "|\n");

		for( int i = 0; i < this.ticket.length; i++ ) {
			for( int j = 0; j < this.ticket[0].length; j++ ) {
				if(this.ticket[i][j] <= 0) {
					currentTicket.append("|   ");
				} else {
					if(ticket[i][j] < 10 ) {
						currentTicket.append("|  ").append(ticket[i][j]);
					} else {
						currentTicket.append("| ").append(ticket[i][j]);
					}
				}}
			currentTicket.append("|\n");
			currentTicket.append("|" + line + "|\n");
		}
		currentTicket.append("Matched::" + this.matchedNums +"\n");
		currentTicket.append("Mathced Rows::" + Arrays.toString(this.numbersEachRow) + "\n");
		return currentTicket.toString();
	}
}
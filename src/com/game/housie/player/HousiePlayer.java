package com.game.housie.player;

import com.game.entity.Player;
import com.game.entity.Ticket;
import com.game.housie.common.HousieExchange;
import com.game.housie.ticket.HousieTicket;

public class HousiePlayer extends Thread implements Player  {
	private int id;
	private String name;
	private HousieExchange exchangeData;
	private HousieTicket ticket;
	Thread thread;

	public HousiePlayer(HousieExchange sharedData) {
		this.exchangeData = sharedData;
		thread = new Thread(this);
	}

	@Override
	public void setId(int id) {
		this.id = id;
		this.name = "Player#" + (this.id+1);
	}

	@Override
	public void setTicket(Ticket ticket) {
		if(!(ticket instanceof HousieTicket)) throw new RuntimeException("Invalid Ticket");
		this.ticket = (HousieTicket) ticket;
	}

	@Override
	public String toString() {
		StringBuilder player =  new StringBuilder();
		player.append(" Housie Player ID: " + this.name + "\n");
		player.append(ticket.toString());
		return player.toString();
	}

	@Override
	public void participate() {
		System.out.println(this.name + " Joined the Contest");
		System.out.println(this);
		super.start();
	}

	@Override
	public void quitGame() {
		try {
			this.join();
		} catch (InterruptedException e) {
			System.err.println("Player QuitGame Error: " + e.getMessage());
		}
	}   

	@Override
	public void run() {
		while(!exchangeData.isGameOver()) {
			waitForNextTurn();
			if(!exchangeData.isGameOver()) {
				try {
					exchangeData.getReadLock().lock();
					updateTicket();
				} finally {               
					exchangeData.getReadLock().unlock();
				}
			}
		}
	}

	private void waitForNextTurn() {
		while(exchangeData.hasPlayed(this.id)) {
			if(exchangeData.isGameOver()) return;
			try {
				Thread.sleep(1000); //not player's Turn, hence sleeping
			} catch (InterruptedException e) {
				System.err.println("Player Error thread interrupted while waiting for the turn : " + e.getMessage());
			}
		}
	}

	private void updateTicket() {
		if(!exchangeData.isGameOver()) {
			int row = 0;
			int col = ticket.getTicketValue()[0].length-1;
			boolean matched = false;
			//starting search at top-right corner of the ticket.
			while(col >= 0 && row <= ticket.getTicketValue().length-1) { // searching for the number in ticket O(log n)
				if(exchangeData.getRoundNumber() == Math.abs(ticket.getTicketValue()[row][col])) {
					matched = exchangeData.getRoundNumber() == ticket.getTicketValue()[row][col]; // positive means its the actual number.
					break;
				} else if(exchangeData.getRoundNumber() < Math.abs(ticket.getTicketValue()[row][col])) { // if the roundNumber is < ticket number, move left.
					col--;
				} else if(exchangeData.getRoundNumber() > Math.abs(ticket.getTicketValue()[row][col])) { // if the roundNumber is < ticket number, move below.
					row++;
				}
			}

			if(matched) {
				ticket.getTicketValue()[row][col] = -ticket.getTicketValue()[row][col]; // marking the number.
				ticket.setMatchedNums(ticket.getMatchedNums() + 1); 
				int[] numEachRow = ticket.getNumbersEachRow();
				numEachRow[row]++;
				ticket.setNumbersEachRow(numEachRow);
			}
			if(ticket.getMatchedNums() ==  exchangeData.getWinnerValue().get(0)) { // earlyFive
				exchangeData.setWinner(0, this.name);
			}
			if(ticket.getNumbersEachRow()[0] ==  exchangeData.getWinnerValue().get(1)) { // firstRow
				exchangeData.setWinner(1, this.name);
			}
			if(ticket.getMatchedNums() ==  exchangeData.getWinnerValue().get(2)) { // fullHouse
				exchangeData.setWinner(2, this.name);
			}
			exchangeData.playedRound(this.id);
		}
	}
}

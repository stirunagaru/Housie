package com.game.housie.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HousieExchange {
	private char nextAction;
	private boolean gameOver;
	private boolean hasWinner;

	// locks - readLock can be acquired by multiple readers at a time.
	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private Lock writeLock = lock.writeLock();
	private Lock readLock = lock.readLock();
	private boolean[] playingStatus;
	private List<Integer> numbersAnnounced;
	private int currentNumber;
	private List<Integer> winnerValue;
	private List<String> winners;
	private List<String> winningCombinations;
	private List<Integer> availableNumbers;

	public HousieExchange(int playerSize, int ticketRows, int ticketCols, int numbersPerTicketRow, int maxPossible) {
		this.numbersAnnounced = new ArrayList<>();
		this.playingStatus = new boolean[playerSize];
		this.winnerValue = Arrays.asList(5, numbersPerTicketRow, numbersPerTicketRow * ticketRows); // earlyFive, firstRow, fullHouse
		this.winners = Arrays.asList("", "", "");
		this.winningCombinations = Arrays.asList("Early Five", "Top Line", "Full House");
		availableNumbers = IntStream.range(1, maxPossible + 1).parallel().boxed().collect(Collectors.toList()); //generate all the numbers
		Collections.shuffle(availableNumbers); // shuffle the numbers.
		nextAction = 'X';
		hasWinner = false;
		gameOver = false;
	}

	public boolean isGameOver() {
		gameOver = hasWinner || this.nextAction == 'Q' || gameOver || (this.availableNumbers.isEmpty() && this.currentNumber == 0);
		return gameOver;
	}

	public void quitGame() {
		this.gameOver = true;
	}

	public synchronized Lock getWriteLock() {
		return writeLock;
	}

	public synchronized Lock getReadLock() {
		return readLock;
	}

	public void resetPlayingStatus() {
		Arrays.fill(playingStatus, false);
	}

	public boolean hasPlayed(int playerId) {
		return playingStatus[playerId] && !gameOver;
	}

	public void playedRound(int playerId) {
		this.playingStatus[playerId] = true;
	}

	public boolean isPlaying() {
		boolean result = true;
		for(boolean b : playingStatus) {
			result = b && result;
		}
		return !result && !gameOver;
	}

	public int getRoundNumber() {
		return this.currentNumber;
	}

	public List<Integer> getAllAnnouncedNumber() {
		return this.numbersAnnounced;
	}

	public void announcedNumber() {
		if(availableNumbers.isEmpty()) {
			this.currentNumber = 0;
			return;
		}
		Collections.shuffle(availableNumbers);
		int num = availableNumbers.remove(0);
		this.currentNumber = num;
		//StringBuilder sb = new StringBuilder("Round No : " + (numbersAnnounced.size() + 1)  + "\nNext Number: " + num  + "\n Numbers Played Till Now: ");
		//sb.append(numbersAnnounced.toString());
		//System.out.println(sb);
		System.out.println(">>> Next Number is: " + num);
		this.numbersAnnounced.add(num);
	}

	public List<Integer> getWinnerValue() {
		return winnerValue;
	}

	public List<String> getWinners() {
		return winners;
	}

	public List<String> getWinningCombinations() {
		return winningCombinations;
	}

	public synchronized boolean setWinner(int id, String name) {
		if(winners.get(id) == null || winners.get(id).isEmpty()) {
			this.winners.set(id, name);
			System.out.println("We have a Winner : " + name + " has won '" + winningCombinations.get(id) + "' winning combination.");
			boolean tempWinner = true;
			for(String s : winners) {
				tempWinner = tempWinner && s != null && !s.isEmpty();
			}
			this.hasWinner = tempWinner;
			return true;
		}
		return false;
	}

	public boolean isHavingWinner() {
		return hasWinner;
	}

	public char getNextAction() {
		return nextAction;
	}

	public void setNextAction(char nextAction) {
		this.nextAction = nextAction;
	}

	public void getSummary() {
		Map<String, List<String>> summary = new HashMap<>();
		for(int i = 0; i < winners.size(); i++) {
			List<String> combinations = summary.containsKey(winners.get(i)) ? summary.get(winners.get(i)) : new ArrayList<>();
			combinations.add(winningCombinations.get(i));
			summary.put(winners.get(i), combinations);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\n   ***** Game Over *****\n").append("==========================\n").append("\tSummary: \n");
		for(int i = 0; i < playingStatus.length; i++) {
			String name = "Player#" +(i+1);
			if(summary.containsKey(name)) {
				sb.append(name).append(" : ").append(summary.get(name));
			} else {
				sb.append(name).append(" : Nothing");
			}
			sb.append("\n");
		}
		sb.append("==========================\n");
		System.out.println(sb);
	}
}

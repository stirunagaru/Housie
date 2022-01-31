package com.game.entity;

/**
 * Player Interface for different type of Ticketing games
 */
public interface Player {
	void setId(int id);
	void setName(String name);
	void setTicket(Ticket ticket);
	void participate();
	void quitGame();
}
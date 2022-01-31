package com.game.housie.common;

public class HousieExchangeTest {
	public static void main(String[] args) {

		HousieExchange exchange = new HousieExchange(1, 3, 6, 5, 20);
		exchange.announcedNumber();
		System.out.println(exchange.getRoundNumber());
		System.out.println(exchange.getAllAnnouncedNumber().size() ==  1);
		System.out.println(exchange.isGameOver() == false);
		exchange.setNextAction('Q');
		System.out.println(exchange.isGameOver() == true);
	

	}
}

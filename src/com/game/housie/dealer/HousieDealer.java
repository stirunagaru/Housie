package com.game.housie.dealer;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import com.game.housie.common.HousieExchange;

public class HousieDealer extends Thread  {
    private static final String welcomeString = 
    ",--.              ,--.   ,--.           ,------.  ,--.                     \n"
  + "|  |     ,---.  ,-'  '-. |  |  ,---.    |  .--. ' |  |  ,--,--. ,--. ,--.  \n"
  + "|  |    | .-. : '-.  .-' `-'  (  .-'    |  '--' | |  | ' ,-.  |  \\  '  /  \n"
  + "|  '--. \\   --.   |  |        .-'  `)   |  | --'  |  | \\ '-'  |   \\   ' \n"
  +  "`-----'  `----'   `--'        `----'    `--'      `--'  `--`--' .-'  /     \n";

    private static HousieDealer dealer;
    private HousieExchange exchangeData;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private HousieDealer(HousieExchange commonData) {
        this.exchangeData = commonData;
    }
    
    /**
     * Singleton instance for HousieDealer
     * @param commonData
     * @return HousieDealer
     */
     public static HousieDealer getDealer (HousieExchange commonData) { 
          if(dealer == null) {
               synchronized(HousieDealer.class) {
                    if(dealer == null) {
                         dealer = new HousieDealer(commonData);
                    }
               }
          }
          return dealer;
     }

    public void beginGame() {
        System.out.println("\n\n");
        System.out.println(welcomeString);
        System.out.println("\n\n");
        super.start();
    }

    public void endGame() {
        try {
               this.join();
          } catch (InterruptedException ex) {
               System.err.println("Dealer EndGame Error: " + ex.getMessage());
          }
    }

     @Override
     public void run() {
          while(!exchangeData.isGameOver()) {
               try {
                while(exchangeData.getNextAction() == 'X') {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                }
                    exchangeData.getWriteLock().lock();
                char action = exchangeData.getNextAction();
                if(action == 'N')
                     exchangeData.announcedNumber();
                if(action == 'Q')
                    exchangeData.quitGame();
                    exchangeData.resetPlayingStatus(); // Reset Playing Status for Players to start
               } finally {
                    exchangeData.getWriteLock().unlock();
               }                
               while(exchangeData.isPlaying()) {
                    try {
                         Thread.sleep(2000);
                    } catch(Exception e) {
                         System.err.println("Error occurred while Dealer waiting:" + e.getMessage());
                    }
               }
               if(exchangeData.isGameOver()) break;
            exchangeData.setNextAction('X');
          }
          exchangeData.getSummary();
     }
}

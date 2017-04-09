package main;

import main.players.BryanAI;
import net.*;

public class GameThread implements Runnable{

    GameBoard game;

    NetClient c;

    String gameID;
    boolean isMyTurn;
    boolean gameOver;

    BryanAI AI;
    Player Opponent;

    //TODO: add client to constructor args
    public GameThread(String gameNumber, boolean weGoFirst, NetClient c){
        game = new GameBoard();

        this.c = c;

        gameID = gameNumber;
        gameOver = false;

        AI = new BryanAI(game,1);
        Opponent = new Player(game,2);

        isMyTurn = weGoFirst;

    }

    @Override
    public void run() {

        //server will tell us when game is over
        while (!gameOver) {
            System.out.println("Game " + gameID +": " + (isMyTurn ? "AI":"Opponent") + "'s turn");


            if(isMyTurn){
                AI.determineTilePlacementByAI();
                AI.determineBuildByAI();
            }
            else { //its opponents turn
                synchronized (this) {
                    while (!isMyTurn) {
                        try {
                            //client will notify when it receives move from server
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //simulate opponents move
                    applyOpponentsMoves();
                }
            }

            //game.printBoard();

            isMyTurn = !isMyTurn;

        }
    }

    //these will change once client is visible
    public void applyOpponentsMoves(){
        Opponent.playTilePhase();
        Opponent.playBuildPhase();
    }

    public void gameIsOver(){
        gameOver = true;
    }

}

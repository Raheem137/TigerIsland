package main;

import java.util.HashMap;
import java.util.Scanner;

import main.enums.Direction;
import main.enums.OccupantType ;

public class Player {

    public int designator ;

    public int score;
    private int meeples;
    private int totoro;
    private boolean isFinished;

    private GameBoard game;
    private Tile tileHeld ;
    private HashMap<Integer, Integer> playerSettlements;

    public Player(GameBoard game, int designator) {
        this.designator = designator ;
        score = 0;
        meeples = 20;
        totoro = 3;
        isFinished = false;

        this.game = game;
        this.tileHeld = null ;
        playerSettlements = new HashMap<>() ;
    }

    public void drawTile() {
        tileHeld = game.releaseTopTile() ;
    }

    public ProjectionPack projectTilePlacement(Tile tileBeingPlaced, Point desiredPoint){
        ProjectionPack projections = new ProjectionPack(desiredPoint) ;

        switch(tileBeingPlaced.rotation){
            case 0:
                projections.projectPoint(projections.hex_a, Direction.UP, Direction.RIGHT);
                projections.projectPoint(projections.hex_b, Direction.DOWN, Direction.RIGHT);
                break;

            case 60:
                projections.projectPoint(projections.hex_a, Direction.DOWN, Direction.RIGHT);
                projections.projectPoint(projections.hex_b, Direction.DOWN, Direction.NONE);
                break;

            case 120:
                projections.projectPoint(projections.hex_a, Direction.DOWN, Direction.NONE);
                projections.projectPoint(projections.hex_b, Direction.DOWN, Direction.LEFT);
                break;

            case 180:
                projections.projectPoint(projections.hex_a, Direction.DOWN, Direction.LEFT);
                projections.projectPoint(projections.hex_b, Direction.UP, Direction.LEFT);
                break;

            case 240:
                projections.projectPoint(projections.hex_a, Direction.UP, Direction.LEFT);
                projections.projectPoint(projections.hex_b, Direction.UP, Direction.NONE);
                break;

            case 300:
                projections.projectPoint(projections.hex_a, Direction.UP, Direction.NONE);
                projections.projectPoint(projections.hex_b, Direction.UP, Direction.RIGHT);
                break;

            default:
                System.out.println("Something just fucked up");
                break;
        }

        return projections ;
    }

    public void playFirstTile(){
        drawTile();
        ProjectionPack projection = projectTilePlacement(tileHeld, new Point(game.BOARD_CENTER, game.BOARD_CENTER));
        projection.projectedLevel = game.getProjectedHexLevel(projection) ;
        game.setTile(tileHeld, projection);
        tileHeld = null ;
    }

    public Point determineTilePositionByHuman(){
        int row, column, rotation ;

        System.out.println("Row choice");
        row = chooseOption();

        System.out.println("Col choice");
        column = chooseOption();

        System.out.println("Rotation choice");
        rotation = chooseOption();

        tileHeld.setRotation(rotation);

        return (new Point(row, column));
    }

    public Point determinePiecePositionByHuman(){
        int row, column ;

        System.out.println("Row choice");
        row = chooseOption();

        System.out.println("Col choice");
        column = chooseOption();

        return (new Point(row, column));
    }

    public void placeTile(){
        Point selectedPoint ;
        ProjectionPack projection ;

        do{
            selectedPoint = determineTilePositionByHuman();
            projection = projectTilePlacement(tileHeld, selectedPoint);
            projection.projectedLevel = game.getProjectedHexLevel(projection) ;
        } while(!game.isValidTilePlacement(projection)) ;

        game.setTile(tileHeld, projection);
        tileHeld = null ;
    }

    public void playTilePhase(){
        drawTile();
        placeTile();
    }

    public int chooseOption() {
        Scanner input = new Scanner(System.in);
        return input.nextInt();
    }

    public void foundNewSettlement() {
        Point selectedPoint ;

        do{
            selectedPoint = determinePiecePositionByHuman();
        } while(!game.isValidSettlementPosition(selectedPoint)) ;
        // TODO: The board currently does not/cannot check if it is an underhanded expansion

        Settlement settlement = new Settlement();
        settlement.createNewSettlement(selectedPoint) ;
        settlement.owner = this ;
        settlement.size += 1 ;

        playerSettlements.put(game.coordinatesToKey(selectedPoint.row, selectedPoint.column), 1);
        game.setPiece(selectedPoint, OccupantType.MEEPLE, settlement);
    }

    public void expandSettlementMeeple() {
        //should use place meeple method
    }

    public void expandSettlementTotoro() {

    }

    public void build() {
        System.out.println("Build options 1-3");
        int buildOption = chooseOption();

        switch(buildOption) {
            case 1:
                foundNewSettlement();
                System.out.println("Successfully Founded");
                break;

            case 2:
                expandSettlementMeeple();
                break;

            case 3:
                expandSettlementTotoro();
                break;

            default:
                System.out.println("Invalid choice");
        }
    }

    public boolean isOutOfPieces(){
        return (meeples == 0 && totoro == 0) ;
    }

    public boolean hasLost() throws Exception {
        return false;
    }
}
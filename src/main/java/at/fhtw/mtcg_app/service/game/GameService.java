package at.fhtw.mtcg_app.service.game;

import at.fhtw.dataAccessLayer.repositories.CardsRepo;
import at.fhtw.dataAccessLayer.repositories.GameRepo;
import at.fhtw.dataAccessLayer.repositories.TradingRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mtcg_app.model.User;
import at.fhtw.mtcg_app.service.trading.TradeController;

import java.util.*;

public class GameService implements Service {

    private final GameController gameController;

    //lists all open battle IDS
    private List<String> openBattles=new ArrayList<>();


    //maps battleID and the resulting response
    private Map<String, Response> battleTracker=new HashMap<>();


    //maps battleID and player that is waiting in this battle
    private Map<String, String> waitingPlayers=new HashMap<>();

    public GameService(){
        this.gameController= new GameController(new GameRepo(), new UserRepo(), new CardsRepo());
    }


    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.GET && request.getPathParts().get(0).equals("stats")){
            return this.gameController.getUserStats(request);
        }else if(request.getMethod() == Method.GET && request.getPathParts().get(0).equals("scoreboard")){
            return this.gameController.getScoreboard(request);
        }else if(request.getMethod() == Method.POST && request.getPathParts().get(0).equals("battles")){

            //get the current player
            String currentPlayer =request.getTokenUser();
            if(currentPlayer.equals("")){
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "Authentication information is missing or invalid"
                );
            }
            String battleID;
            Response response;

            //check if there are open battles to join, if there are none, generate one
            // TODO synchronize this
            if(openBattles.isEmpty()){
                do{
                    battleID=generateBattleID(12);
                }while(battleIDExists(battleID));

                openBattles.add(battleID);
                //adds the current player
                waitingPlayers.put(battleID, currentPlayer);
                battleTracker.put(battleID, null);
                while (!checkIfBattleFinished(battleID)){
                    try{
                        Thread.sleep(500);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                response=battleTracker.get(battleID);
            } //if there is a battle to join, join the battle and run it
            else{
                battleID=openBattles.get(0);
                openBattles.remove(0);
                List<String> playersInBattle=new ArrayList<>();
                playersInBattle.add(waitingPlayers.get(battleID));
                playersInBattle.add(currentPlayer);

                //only the tread of the second player joining the battle simulates the battle, because there is no need to simulate the battle twice
                response=this.gameController.battle(playersInBattle.get(0), playersInBattle.get(1));
                battleTracker.put(battleID, response);
            }
            return response;
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

    private boolean battleIDExists(String battleID) {
        Set<String> allKeys = battleTracker.keySet();
        return allKeys.contains(battleID);
    }

    private boolean checkIfBattleFinished(String battleID) {
        if(battleTracker.get(battleID)==null){
            return false;
        }else{
            return true;
        }
    }

    private String generateBattleID(int length) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

}

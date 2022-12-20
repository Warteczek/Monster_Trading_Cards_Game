package at.fhtw.mtcg_app.service.game;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.dataAccessLayer.repositories.CardsRepo;
import at.fhtw.dataAccessLayer.repositories.GameRepo;
import at.fhtw.dataAccessLayer.repositories.TradingRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.Stats;
import at.fhtw.mtcg_app.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameController extends Controller {

    private GameRepo gameRepo;
    private UserRepo userRepo;
    private CardsRepo cardsRepo;

    public GameController(GameRepo gameRepo, UserRepo userRepo, CardsRepo cardsRepo){
        this.gameRepo=gameRepo;
        this.userRepo=userRepo;
        this.cardsRepo=cardsRepo;
    }
    public Response getUserStats(Request request) {

        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        try {
            boolean userExists = this.userRepo.checkUserExists(username, newUnit);
            if (!userExists) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            Stats stats = this.userRepo.getUserStats(username, newUnit);
            String userStatsJSON = this.getObjectMapper().writeValueAsString(stats);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userStatsJSON
            );




        }catch(Exception e){
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    public Response getScoreboard(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        try {
            boolean userExists = this.userRepo.checkUserExists(username, newUnit);
            if (!userExists) {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            List<Stats> scoreboardList = new ArrayList<>();
            scoreboardList=this.userRepo.getScoreboard(username, newUnit);
            String scoreboard = this.getObjectMapper().writeValueAsString(scoreboardList);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    scoreboard
            );


        }catch(Exception e){
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }


    public Response battle(String firstPlayer, String secondPlayer) {
        UnitOfWork newUnit = new UnitOfWork();
        String responseContent="";

        List<Card> firstPlayerDeck=new ArrayList<>();
        List<Card> secondPlayerDeck=new ArrayList<>();


        try{
            //get decks
            firstPlayerDeck=this.cardsRepo.showDeckFromUser(firstPlayer, newUnit);
            secondPlayerDeck=this.cardsRepo.showDeckFromUser(secondPlayer, newUnit);

            if(firstPlayerDeck.isEmpty() || secondPlayerDeck.isEmpty()){
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "Players have to configure their decks"
                );
            }
            List<Card> updateFirstPlayerDeck=firstPlayerDeck;
            List<Card> updateSecondPlayerDeck=secondPlayerDeck;
            int roundsPlayed=0;
            String newContent="";
            String roundWinner="";
            do{
                roundWinner="";
                //select random cards
                Card player1Card = selectRandomCardFromDeck(updateFirstPlayerDeck);
                Card player2Card = selectRandomCardFromDeck(updateSecondPlayerDeck);

                updateFirstPlayerDeck.remove(player1Card);
                updateSecondPlayerDeck.remove(player2Card);


                //TODO specialities


                if(player1Card.getType().equals("monster") && player2Card.getType().equals("monster")){
                    if(player1Card.getDamage()>player2Card.getDamage()){
                        roundWinner="player1";
                        newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Card.getName()+" defeats "+player2Card.getName();
                    }else if(player1Card.getDamage()<player2Card.getDamage()){
                        roundWinner="player2";
                        newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player2Card.getName()+" defeats "+player1Card.getName();
                    }else{
                        roundWinner="draw";
                        newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => draw";
                    }

                }else{
                    // TODO player loses with 1 card left
                    if(player1Card.getElement().equals("water") && player2Card.getElement().equals("fire")){

                        double player1Damage=2*player1Card.getDamage();
                        double player2Damage=0.5*player2Card.getDamage();
                        if(player1Damage>player2Damage){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Damage<player2Damage){
                            roundWinner="player2";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player2Card.getName()+" defeats "+player1Card.getName();
                        }else{
                            roundWinner="draw";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => draw";
                        }

                    }else if(player1Card.getElement().equals("fire") && player2Card.getElement().equals("water")){

                        double player1Damage=0.5*player1Card.getDamage();
                        double player2Damage=2*player2Card.getDamage();
                        if(player1Damage>player2Damage){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Damage<player2Damage){
                            roundWinner="player2";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player2Card.getName()+" defeats "+player1Card.getName();
                        }else{
                            roundWinner="draw";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => draw";
                        }
                    }else if(player1Card.getElement().equals("fire") && player2Card.getElement().equals("normal")){

                        double player1Damage=2*player1Card.getDamage();
                        double player2Damage=player2Card.getDamage();
                        if(player1Damage>player2Damage){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Damage<player2Damage){
                            roundWinner="player2";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player2Card.getName()+" defeats "+player1Card.getName();
                        }else{
                            roundWinner="draw";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => draw";
                        }
                    }else if(player1Card.getElement().equals("normal") && player2Card.getElement().equals("fire")){

                        double player1Damage=player1Card.getDamage();
                        double player2Damage=2*player2Card.getDamage();
                        if(player1Damage>player2Damage){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Damage<player2Damage){
                            roundWinner="player2";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player2Card.getName()+" defeats "+player1Card.getName();
                        }else{
                            roundWinner="draw";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => draw";
                        }
                    }else if(player1Card.getElement().equals("normal") && player2Card.getElement().equals("water")){

                        double player1Damage=2*player1Card.getDamage();
                        double player2Damage=player2Card.getDamage();
                        if(player1Damage>player2Damage){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Damage<player2Damage){
                            roundWinner="player2";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player2Card.getName()+" defeats "+player1Card.getName();
                        }else{
                            roundWinner="draw";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => draw";
                        }
                    }else if(player1Card.getElement().equals("water") && player2Card.getElement().equals("normal")){

                        double player1Damage=player1Card.getDamage();
                        double player2Damage=2*player2Card.getDamage();
                        if(player1Damage>player2Damage){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Damage<player2Damage){
                            roundWinner="player2";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => "+player2Card.getName()+" defeats "+player1Card.getName();
                        }else{
                            roundWinner="draw";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Damage+" vs "+player2Damage+" => draw";
                        }
                    }else{
                        if(player1Card.getDamage()>player2Card.getDamage()){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Card.getDamage()<player2Card.getDamage()){
                            roundWinner="player2";
                            newContent=firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player2Card.getName()+" defeats "+player1Card.getName();
                        }else{
                            roundWinner="draw";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => draw";
                        }
                    }
                }


                // card of loser to winner
                if(roundWinner.equals("player1")){
                    updateFirstPlayerDeck.add(player2Card);
                }else if(roundWinner.equals("player2")){
                    updateSecondPlayerDeck.add(player1Card);
                }else{
                    updateFirstPlayerDeck.add(player1Card);
                    updateSecondPlayerDeck.add(player2Card);
                }

                responseContent= responseContent + newContent;

                if(updateFirstPlayerDeck.isEmpty()){
                    responseContent= responseContent + "\n\n"+secondPlayer+" wins!!!";

                    break;
                }
                if(updateSecondPlayerDeck.isEmpty()){
                    responseContent= responseContent + "\n\n"+firstPlayer+" wins!!!";

                    break;
                }

                roundsPlayed++;
            }while(roundsPlayed<100 || updateFirstPlayerDeck.isEmpty() || updateSecondPlayerDeck.isEmpty());

            // TODO cards of loser to winner

            //TODO update ELO



            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "Battle successful\n" + responseContent
            );
        }catch(Exception e){
            e.printStackTrace();
        }




        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    private Card selectRandomCardFromDeck(List<Card> deck) {
        int randomNum;
        if(deck.size()<2){
            randomNum=0;
        }else{
            randomNum = ThreadLocalRandom.current().nextInt(0, deck.size()-1);
        }

        return deck.get(randomNum);
    }
}

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
            newUnit.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        try {
            boolean userExists = this.userRepo.checkUserExists(username, newUnit);
            if (!userExists) {
                newUnit.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            Stats stats = this.userRepo.getUserStats(username, newUnit);
            String userStatsJSON = this.getObjectMapper().writeValueAsString(stats);

            newUnit.close();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userStatsJSON
            );

        }catch(Exception e){
            e.printStackTrace();
        }

        newUnit.close();
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
            newUnit.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        try {
            boolean userExists = this.userRepo.checkUserExists(username, newUnit);
            if (!userExists) {
                newUnit.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            List<Stats> scoreboardList = new ArrayList<>();
            scoreboardList=this.userRepo.getScoreboard(username, newUnit);
            String scoreboard = this.getObjectMapper().writeValueAsString(scoreboardList);

            newUnit.close();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    scoreboard
            );


        }catch(Exception e){
            e.printStackTrace();
        }

        newUnit.close();

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
                newUnit.close();
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "Players have to configure their decks"
                );
            }
            List<Card> updateFirstPlayerDeck=new ArrayList<>(firstPlayerDeck);
            List<Card> updateSecondPlayerDeck=new ArrayList<>(secondPlayerDeck);
            int roundsPlayed=0;
            String newContent="";
            String specialityContent="";
            String roundWinner="";
            String winner="";
            String loser="";

            //variables for unique feature(winner gets bonus for next round if the difference of damage is greater than 30)
            int bonusPlayer1=0, bonusPlayer2=0;
            do{
                roundWinner="";
                specialityContent="";
                //select random cards
                Card player1Card = selectRandomCardFromDeck(updateFirstPlayerDeck);
                Card player2Card = selectRandomCardFromDeck(updateSecondPlayerDeck);


                updateFirstPlayerDeck.remove(player1Card);
                updateSecondPlayerDeck.remove(player2Card);


                // specialities
                if(player1Card.getName().contains("Goblin") && player2Card.getName().contains("Dragon")){
                    player1Card.setDamage(0);
                    specialityContent="\nGoblin is to afraid to attack Dragon => ";
                }else if(player2Card.getName().contains("Goblin") && player1Card.getName().contains("Dragon")){
                    player2Card.setDamage(0);
                    specialityContent="\nGoblin is to afraid to attack Dragon => ";
                }
                else if(player1Card.getName().contains("Wizzard") && player2Card.getName().contains("Orks")){
                    player2Card.setDamage(0);
                    specialityContent="\nWizzard controls Ork => ";
                }
                else if(player2Card.getName().contains("Wizzard") && player1Card.getName().contains("Orks")){
                    player1Card.setDamage(0);
                    specialityContent="\nWizzard controls Ork => ";
                }
                else if(player1Card.getName().contains("Knight") && player2Card.getElement().equals("water") && player2Card.getType().equals("spell")){
                    player1Card.setDamage(0);
                    specialityContent="\nKnight drowns => ";
                }
                else if(player2Card.getName().contains("Knight") && player1Card.getElement().equals("water") && player1Card.getType().equals("spell")){
                    player2Card.setDamage(0);
                    specialityContent="\nKnight drowns => ";
                }else if(player1Card.getName().contains("Kraken") && player2Card.getType().equals("spell")){
                    player2Card.setDamage(0);
                    specialityContent="\nKraken is immune against spells => ";
                }else if(player2Card.getName().contains("Kraken") && player1Card.getType().equals("spell")){
                    player1Card.setDamage(0);
                    specialityContent="\nKraken is immune against spells => ";
                }else if(player1Card.getName().contains("Elves") && player1Card.getElement().equals("fire") && player2Card.getName().contains("Dragon")){
                    player2Card.setDamage(0);
                    specialityContent="\nFireElve evades dragon => ";
                }else if(player2Card.getName().contains("Elves") && player2Card.getElement().equals("fire") && player1Card.getName().contains("Dragon")){
                    player1Card.setDamage(0);
                    specialityContent="\nFireElve evades dragon => ";
                }


                // battle logic

                double player1Damage;
                double player2Damage;

                if(player1Card.getType().equals("monster") && player2Card.getType().equals("monster")){
                    player1Damage=player1Card.getDamage();
                    player2Damage=player2Card.getDamage();

                    player1Damage=player1Damage+bonusPlayer1;
                    player2Damage=player2Damage+bonusPlayer2;
                    if(player1Damage>player2Damage){
                        roundWinner="player1";
                        newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Card.getName()+" defeats "+player2Card.getName();
                    }else if(player1Damage<player2Damage){
                        roundWinner="player2";
                        newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player2Card.getName()+" defeats "+player1Card.getName();
                    }else{
                        roundWinner="draw";
                        newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => draw";
                    }

                }else{
                    if(player1Card.getElement().equals("water") && player2Card.getElement().equals("fire")){

                        player1Damage=2*player1Card.getDamage();
                        player2Damage=0.5*player2Card.getDamage();
                        player1Damage=player1Damage+bonusPlayer1;
                        player2Damage=player2Damage+bonusPlayer2;
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

                        player1Damage=0.5*player1Card.getDamage();
                        player2Damage=2*player2Card.getDamage();
                        player1Damage=player1Damage+bonusPlayer1;
                        player2Damage=player2Damage+bonusPlayer2;
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

                        player1Damage=2*player1Card.getDamage();
                        player2Damage=player2Card.getDamage();
                        player1Damage=player1Damage+bonusPlayer1;
                        player2Damage=player2Damage+bonusPlayer2;
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

                        player1Damage=player1Card.getDamage();
                        player2Damage=2*player2Card.getDamage();
                        player1Damage=player1Damage+bonusPlayer1;
                        player2Damage=player2Damage+bonusPlayer2;
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

                        player1Damage=2*player1Card.getDamage();
                        player2Damage=player2Card.getDamage();
                        player1Damage=player1Damage+bonusPlayer1;
                        player2Damage=player2Damage+bonusPlayer2;
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

                        player1Damage=player1Card.getDamage();
                        player2Damage=2*player2Card.getDamage();
                        player1Damage=player1Damage+bonusPlayer1;
                        player2Damage=player2Damage+bonusPlayer2;
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
                        player1Damage=player1Card.getDamage();
                        player2Damage=player2Card.getDamage();
                        player1Damage=player1Damage+bonusPlayer1;
                        player2Damage=player2Damage+bonusPlayer2;
                        if(player1Damage>player2Damage){
                            roundWinner="player1";
                            newContent="\n"+firstPlayer+": "+player1Card.getName()+" ("+player1Card.getDamage()+" Damage) vs "+secondPlayer+": "+player2Card.getName()+" ("+player2Card.getDamage()+" Damage)  => "+player1Card.getName()+" defeats "+player2Card.getName();
                        }else if(player1Damage<player2Damage){
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
                    updateFirstPlayerDeck.add(player1Card);
                }else if(roundWinner.equals("player2")){
                    updateSecondPlayerDeck.add(player1Card);
                    updateSecondPlayerDeck.add(player2Card);
                }else{
                    updateFirstPlayerDeck.add(player1Card);
                    updateSecondPlayerDeck.add(player2Card);
                }

                //when a player wins with more than 30 points more than the other one, he receives a bonus of 10 points for the next round
                String nextRoundBonusContent="";
                if(player1Damage-player2Damage>30){
                    nextRoundBonusContent="\n"+firstPlayer+" absolutely destroyed "+secondPlayer+" and will therefore get 10 bonus points in the next round!\n\n";
                    bonusPlayer1=10;
                    bonusPlayer2=0;
                }else if(player2Damage-player1Damage>30){
                    nextRoundBonusContent="\n"+secondPlayer+" absolutely destroyed "+firstPlayer+" and will therefore get 10 bonus points in the next round!\n\n";
                    bonusPlayer2=10;
                    bonusPlayer1=0;
                }
                else{
                    bonusPlayer1=0;
                    bonusPlayer2=0;
                }

                responseContent= responseContent + specialityContent + newContent+ nextRoundBonusContent;


                if(updateFirstPlayerDeck.isEmpty()){
                    winner=secondPlayer;
                    loser=firstPlayer;
                    responseContent= responseContent + "\n\n"+secondPlayer+" wins!!!";

                    break;
                }
                if(updateSecondPlayerDeck.isEmpty()){
                    winner=firstPlayer;
                    loser=secondPlayer;
                    responseContent= responseContent + "\n\n"+firstPlayer+" wins!!!";

                    break;
                }

                roundsPlayed++;
                if(roundsPlayed==100){
                    responseContent= responseContent + "\n\n IT ENDS IN A DRAW!!!";
                }


            }while(roundsPlayed<100 || updateFirstPlayerDeck.isEmpty() || updateSecondPlayerDeck.isEmpty());



            if(!winner.equals("")){
                if(loser.equals(secondPlayer)){
                    this.gameRepo.awardCardsToWinner(winner,loser, secondPlayerDeck, newUnit);
                }else{
                    this.gameRepo.awardCardsToWinner(winner,loser, firstPlayerDeck, newUnit);
                }

                this.gameRepo.updateELO(winner,loser, newUnit);
            }

            newUnit.commit();

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "Battle successful\n" + responseContent
            );
        }catch(Exception e){
            e.printStackTrace();
        }

        newUnit.rollback();


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

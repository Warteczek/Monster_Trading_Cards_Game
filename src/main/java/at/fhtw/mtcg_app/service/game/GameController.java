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
import at.fhtw.mtcg_app.model.Stats;
import at.fhtw.mtcg_app.model.User;

import java.util.ArrayList;
import java.util.List;

public class GameController extends Controller {

    private GameRepo gameRepo;
    private UserRepo userRepo;

    public GameController(GameRepo gameRepo, UserRepo userRepo){
        this.gameRepo=gameRepo;
        this.userRepo=userRepo;
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
        System.out.println(firstPlayer +" "+ secondPlayer);


        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}

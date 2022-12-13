package at.fhtw.mtcg_app.service.cards;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.dataAccessLayer.repositories.CardsRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.Card;

import java.util.ArrayList;
import java.util.List;

public class CardsController extends Controller {
    private CardsRepo cardsRepo;
    private UserRepo userRepo;

    public CardsController(CardsRepo cardsRepo, UserRepo userRepo) {
        this.cardsRepo = cardsRepo;
        this.userRepo=userRepo;
    }


    public Response showCards(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }

        try{
            boolean userExists=this.userRepo.checkUserExists(username, newUnit);
            if(!userExists){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            List<Card> cards = this.cardsRepo.getCards(username, newUnit);

            if(cards.isEmpty()){
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "The request was fine, but the user doesn't have any cards"
                );
            }
            String cardsJSON = this.getObjectMapper().writeValueAsString(cards);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    cardsJSON
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

    public Response configureDeck(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        try{
            boolean userExists=this.userRepo.checkUserExists(username, newUnit);
            if(!userExists){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            List<String> cardIDs = new ArrayList<String>();
            String requestBody=request.getBody();

            String[] splitRequest = requestBody.split(", \"");
            if(splitRequest.length!=4){
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "The provided deck did not include the required amount of cards"
                );
            }
            for(int i=0; i<4; i++){
                splitRequest[i] = splitRequest[i].replace("[", "");
                splitRequest[i] = splitRequest[i].replace("]", "");
                splitRequest[i] = splitRequest[i].replace("\"", "");
                cardIDs.add(splitRequest[i]);
            }

            if(!this.cardsRepo.checkIfCardsBelongToUser(username, cardIDs, newUnit)){
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "At least one of the provided cards does not belong to the user or is not available."
                );
            }

            this.cardsRepo.addCardsToDeck(username, cardIDs, newUnit);


            newUnit.commit();

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "The deck has been successfully configured"
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

    public Response showDeck(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        try{
            boolean userExists=this.userRepo.checkUserExists(username, newUnit);
            if(!userExists){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            List<Card> cards = this.cardsRepo.showDeckFromUser(username, newUnit);

            if(cards.isEmpty()){
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "The request was fine, but the deck doesn't have any cards"
                );
            }
            String cardsJSON = this.getObjectMapper().writeValueAsString(cards);
            if(request.hasParams() && request.getParams().equals("format=plain")){
                return new Response(
                        HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        cardsJSON
                );
            }else{
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        cardsJSON
                );
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}

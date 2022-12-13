package at.fhtw.mtcg_app.service.trading;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.dataAccessLayer.repositories.CardsRepo;
import at.fhtw.dataAccessLayer.repositories.TradingRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.Trade;

import java.util.ArrayList;
import java.util.List;

public class TradeController extends Controller {
    private TradingRepo tradingRepo;
    private UserRepo userRepo;
    private CardsRepo cardsRepo;

    public TradeController(TradingRepo tradingRepo, UserRepo userRepo, CardsRepo cardsRepo){
        this.tradingRepo=tradingRepo;
        this.userRepo=userRepo;
        this.cardsRepo=cardsRepo;
    }

    public Response deleteTradingDeal(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        // TODO deleteTradingDeal


        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    public Response executeTradingDeal(Request request) {

        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        // TODO executeTradingDeal


        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    public Response createTradingDeal(Request request) {
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
            Trade tradeDeal = this.getObjectMapper().readValue(request.getBody(), Trade.class);

            if(this.tradingRepo.tradingDealAlreadyExists(tradeDeal.getId(), newUnit)){
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "A deal with this deal ID already exists."
                );
            }

            List<String> tradeCard=new ArrayList<>();
            tradeCard.add(tradeDeal.getCardToTrade());


            if((!this.cardsRepo.checkIfCardsBelongToUser(username, tradeCard, newUnit)) || this.tradingRepo.checkCardIsLockedInDeck(tradeDeal.getCardToTrade(), newUnit)){
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "The deal contains a card that is not owned by the user or locked in the deck."
                );
            }


            this.tradingRepo.addTradingDeal(tradeDeal, newUnit);

            newUnit.commit();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    "Trading deal successfully created"
            );

        }catch (Exception e){
            e.printStackTrace();
        }

        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );

    }

    public Response getTradingDeals(Request request) {
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

            List<Trade> tradeDeals = this.tradingRepo.getTradingDeals(newUnit);

            if(tradeDeals.isEmpty()){
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "The request was fine, but the user doesn't have any cards"
                );
            }
            String cardsJSON = this.getObjectMapper().writeValueAsString(tradeDeals);

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
}

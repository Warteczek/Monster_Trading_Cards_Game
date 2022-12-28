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
            newUnit.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }
        try{
            boolean userExists=this.userRepo.checkUserExists(username, newUnit);
            if(!userExists){
                newUnit.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            String dealID=request.getPathParts().get(1);

            if(!this.tradingRepo.tradingDealAlreadyExists(dealID, newUnit)){
                newUnit.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "The provided deal ID was not found."
                );
            }

            String cardToTrade= this.tradingRepo.getCardToTrade(dealID, newUnit);
            if(cardToTrade.equals("")){
                throw new Exception("Could not get card");
            }
            //List of strings is required, because otherwise the already existing method could not be used
            List<String> tradeCard=new ArrayList<String>();
            tradeCard.add(cardToTrade);


            if(!this.cardsRepo.checkIfCardsBelongToUser(username, tradeCard, newUnit)){
                newUnit.close();
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "The deal contains a card that is not owned by the user."
                );
            }


            this.tradingRepo.deleteTradingDeal(dealID, newUnit);

            newUnit.commit();

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "Trading deal successfully deleted"
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

    public Response executeTradingDeal(Request request) {

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

            String dealID = request.getPathParts().get(1);

            //if deal does not exist
            if (!this.tradingRepo.tradingDealAlreadyExists(dealID, newUnit)) {
                newUnit.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "The provided deal ID was not found."
                );
            }



            // checks if both cards have the correct user, if the requirements of the trade are met and if the cards are locked in the deck
            //if something is wrong with the card from the creator of the deal, the deal gets deleted
            if(checkIfTradeIsPossible(dealID, request.getBody(), username, newUnit)){

                String newOwnerDealCard=username;
                String newOwnerOfferCard=this.tradingRepo.getCreatorFromDeal(dealID, newUnit);
                String offerCard=request.getBody();
                String dealCard=this.tradingRepo.getCardToTrade(dealID, newUnit);


                this.tradingRepo.executeTrade(dealID, newOwnerDealCard, newOwnerOfferCard, dealCard, offerCard, newUnit);

                newUnit.commit();

                return new Response(
                        HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        "Trading deal successfully executed."
                );
            }else{
                newUnit.commit();

                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "The offered card is not owned by the user, or the requirements are not met (Type, MinimumDamage), or the offered card is locked in the deck."
                );
            }

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



    public Response createTradingDeal(Request request) {
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

        try{
            boolean userExists=this.userRepo.checkUserExists(username, newUnit);
            if(!userExists){
                newUnit.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }
            Trade tradeDeal = this.getObjectMapper().readValue(request.getBody(), Trade.class);

            if(this.tradingRepo.tradingDealAlreadyExists(tradeDeal.getId(), newUnit)){
                newUnit.close();
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "A deal with this deal ID already exists."
                );
            }

            List<String> tradeCard=new ArrayList<>();
            tradeCard.add(tradeDeal.getCardToTrade());


            if((!this.cardsRepo.checkIfCardsBelongToUser(username, tradeCard, newUnit)) || this.tradingRepo.checkCardIsLockedInDeck(tradeDeal.getCardToTrade(), newUnit)){
                newUnit.close();
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "The deal contains a card that is not owned by the user or locked in the deck."
                );
            }


            this.tradingRepo.addTradingDeal(tradeDeal, username, newUnit);

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
            newUnit.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }

        try{
            boolean userExists=this.userRepo.checkUserExists(username, newUnit);
            if(!userExists){
                newUnit.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            List<Trade> tradeDeals = this.tradingRepo.getTradingDeals(newUnit);

            if(tradeDeals.isEmpty()){
                newUnit.close();
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "The request was fine, but the user doesn't have any cards"
                );
            }
            String cardsJSON = this.getObjectMapper().writeValueAsString(tradeDeals);

            newUnit.close();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    cardsJSON
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

    private boolean checkIfTradeIsPossible(String dealID, String cardFromOfferID, String userFromOffer, UnitOfWork newUnit) throws Exception {

        try{
            String cardFromDealID=this.tradingRepo.getCardToTrade(dealID, newUnit);



            if(this.tradingRepo.checkCardIsLockedInDeck(cardFromOfferID, newUnit)){
                return false;
            }


            if(this.tradingRepo.checkCardIsLockedInDeck(cardFromDealID, newUnit)){
                this.tradingRepo.deleteTradingDeal(dealID, newUnit);
                return false;
            }



            List<String> cardFromDealList =new ArrayList<>();
            cardFromDealList.add(cardFromDealID);
            List<String> cardFromOfferList =new ArrayList<>();
            cardFromOfferList.add(cardFromOfferID);

            String creatorOfDeal=this.tradingRepo.getCreatorFromDeal(dealID, newUnit);

            if(creatorOfDeal.equals(userFromOffer)){
                return false;
            }


            if(!this.cardsRepo.checkIfCardsBelongToUser(userFromOffer, cardFromOfferList, newUnit)){
                return false;
            }


            if(!this.cardsRepo.checkIfCardsBelongToUser(creatorOfDeal, cardFromDealList, newUnit)){
                this.tradingRepo.deleteTradingDeal(dealID, newUnit);
                return false;
            }



            Trade trade=this.tradingRepo.getTrade(dealID, newUnit);
            Card card=this.cardsRepo.getCard(cardFromOfferID, newUnit);
            String requiredType= trade.getType(), actualType=card.getType();
            int requiredDamage= trade.getMinDamage(), actualDamage=card.getDamage();

            if(requiredDamage>actualDamage || !requiredType.equals(actualType)){
                return false;
            }

        }catch(Exception e){
            throw new Exception("Could not check if trade is possible");
        }

        return true;
    }
}

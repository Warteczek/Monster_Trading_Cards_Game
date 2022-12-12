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
    }

    public Response executeTradingDeal(Request request) {
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

            //TODO checkCardsBelongsToUser and checkIfCardIsLockedInDeck

            if(!this.cardsRepo.checkIfCardsBelongToUser(username, tradeDeal.getCardToTrade(), newUnit)){
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "The deal contains a card that is not owned by the user or locked in the deck."
                );
            }

            this.tradingRepo.addTradingDeal(tradeDeal, newUnit);



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Response getTradingDeals(Request request) {

    }
}

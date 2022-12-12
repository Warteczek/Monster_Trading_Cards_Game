package at.fhtw.mtcg_app.service.trading;

import at.fhtw.dataAccessLayer.repositories.CardsRepo;
import at.fhtw.dataAccessLayer.repositories.TradingRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class TradeService implements Service {

    private final TradeController tradeController;

    public TradeService(){
        this.tradeController= new TradeController(new TradingRepo(), new UserRepo(), new CardsRepo());
    }
    @Override
    public Response handleRequest(Request request) {
        if(request.getPathParts().size()==2){
            if(request.getMethod() == Method.DELETE) {
                return this.tradeController.deleteTradingDeal(request);
            }
            else if(request.getMethod() == Method.POST) {
                return this.tradeController.executeTradingDeal(request);
            }
        }else if(request.getPathParts().size()==1){
            if (request.getMethod() == Method.POST && request.getPathParts().get(0).equals("tradings")) {
                return this.tradeController.createTradingDeal(request);
            }
            else if (request.getMethod() == Method.GET && request.getPathParts().get(0).equals("tradings")) {
                return this.tradeController.getTradingDeals(request);
            }
        }


        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}

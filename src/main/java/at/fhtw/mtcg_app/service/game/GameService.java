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

import java.util.ArrayList;
import java.util.List;

public class GameService implements Service {

    private final GameController gameController;
    private List<String> players=new ArrayList<>();

    public GameService(){
        this.gameController= new GameController(new GameRepo(), new UserRepo());
    }


    @Override
    public Response handleRequest(Request request) {
        if(request.getMethod() == Method.GET && request.getPathParts().get(0).equals("stats")){
            return this.gameController.getUserStats(request);
        }else if(request.getMethod() == Method.GET && request.getPathParts().get(0).equals("scoreboard")){
            return this.gameController.getScoreboard(request);
        }else if(request.getMethod() == Method.POST && request.getPathParts().get(0).equals("battles")){
            String player =request.getTokenUser();
            players.add(player);

            do{
                try{
                    Thread.sleep(3000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }while (players.size()<2);
            String player1=players.get(0), player2=players.get(1);
            for(int i=0; i<2; i++){
                players.remove(0);
            }
            return this.gameController.battle(player1, player2);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}

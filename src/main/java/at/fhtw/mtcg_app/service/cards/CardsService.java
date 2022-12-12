package at.fhtw.mtcg_app.service.cards;

import at.fhtw.dataAccessLayer.repositories.CardsRepo;
import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mtcg_app.service.packages.PackageController;

public class CardsService implements Service {

    private final CardsController cardsController;

    public CardsService() {
        this.cardsController = new CardsController(new CardsRepo(), new UserRepo());
    }
    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET && request.getPathParts().get(0).equals("cards")) {
            return this.cardsController.showCards(request);
        }else if(request.getMethod() == Method.GET && request.getPathParts().get(0).equals("deck")){
            return this.cardsController.showDeck(request);
        }else if(request.getMethod() == Method.PUT && request.getPathParts().get(0).equals("deck")){
            return this.cardsController.configureDeck(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}

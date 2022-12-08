package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;

import java.util.ArrayList;
import java.util.List;


public class CardsRepo {
    public List<Card> getCards(String username, UnitOfWork newUnit) {
        List<Card> allCards=new ArrayList<>();
        // TODO show Cards

        return allCards;
    }
}

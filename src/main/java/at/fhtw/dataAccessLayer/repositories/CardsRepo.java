package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CardsRepo {
    public List<Card> getCards(String username, UnitOfWork newUnit) throws Exception {
        List<Card> allCards=new ArrayList<>();

        String name="",  type="", id="", element_type="", package_id="";
        int damage = 0;

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM cards WHERE id IN (SELECT card_id FROM stack WHERE username=?)");
            statement.setString(1, username);

            ResultSet resultSet= statement.executeQuery();
            while (resultSet.next()){
                name=resultSet.getString("name");
                type =resultSet.getString("type");
                id=resultSet.getString("id");
                element_type=resultSet.getString("element_type");
                package_id=resultSet.getString("package_id");
                damage=resultSet.getInt("damage");


                Card card = new Card();

                card.setName(name);
                card.setType(type);
                card.setId(id);
                card.setElement(element_type);
                card.setPackageID(package_id);
                card.setDamage(damage);


                allCards.add(card);
            }

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get cards");
        }

        return allCards;
    }

    public void addCardsToDeck(String username, List<String> cardIDs, UnitOfWork newUnit) throws Exception {

        //TODO add cards to deck

        try{
            PreparedStatement statement= newUnit.getStatement("UPDATE decks SET firstCard_ID=?, secondCard_ID=?, thirdCard_ID=?, fourthCard_ID=? WHERE owner_id=?");
            statement.setString(1, cardIDs.get(0));
            statement.setString(2, cardIDs.get(1));
            statement.setString(3, cardIDs.get(2));
            statement.setString(4, cardIDs.get(3));
            statement.setString(5, username);

            statement.execute();

        }catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Could not add cards to deck");
        }
    }
}

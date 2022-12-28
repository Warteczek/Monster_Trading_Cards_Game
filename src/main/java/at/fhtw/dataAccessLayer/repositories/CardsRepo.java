package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;

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

            //gets a list of all cards in the resultSet
            allCards=getListOfCards(resultSet);

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get cards");
        }

        return allCards;
    }

    public void addCardsToDeck(String username, List<String> cardIDs, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("UPDATE decks SET \"firstCard_ID\"=?, \"secondCard_ID\"=?, \"thirdCard_ID\"=?, \"fourthCard_ID\"=? WHERE owner_id=?");
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

    public boolean checkIfCardsBelongToUser(String username, List<String> cardIDs, UnitOfWork newUnit) throws Exception {
        try{
            for(String cardID : cardIDs){

                cardID=cardID.replaceAll("\"", "");

                PreparedStatement statement= newUnit.getStatement("SELECT * FROM stack WHERE username=? AND card_id=?");
                statement.setString(1, username);
                statement.setString(2, cardID);

                ResultSet resultSet= statement.executeQuery();
                if(!resultSet.next()){
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Not able to check if cards belong to user");
        }
        return true;
    }

    public List<Card> showDeckFromUser(String username, UnitOfWork newUnit) throws Exception {
        List<Card> deckCards=new ArrayList<>();

        String name="",  type="", id="", element_type="", package_id="";
        int damage = 0;

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM decks WHERE owner_id=?");
            statement.setString(1, username);

            ResultSet resultSet= statement.executeQuery();
            if (resultSet.next()){
                //selects all card IDs
                String firstCard_ID=resultSet.getString("firstCard_ID");
                String secondCard_ID =resultSet.getString("secondCard_ID");
                String thirdCard_ID=resultSet.getString("thirdCard_ID");
                String fourthCard_ID=resultSet.getString("fourthCard_ID");

                //if deck is not configured
                if(firstCard_ID==null || secondCard_ID==null || thirdCard_ID==null || fourthCard_ID==null){
                    return deckCards;
                }


                //selects all data from cards
                PreparedStatement statementCards= newUnit.getStatement("SELECT * FROM cards WHERE id=? OR id=? OR id=? OR id=?");
                statementCards.setString(1, firstCard_ID);
                statementCards.setString(2, secondCard_ID);
                statementCards.setString(3, thirdCard_ID);
                statementCards.setString(4, fourthCard_ID);

                ResultSet resultSetCards= statementCards.executeQuery();

                //gets a list of all cards in the resultSet
                deckCards=getListOfCards(resultSetCards);
            }

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get cards from deck");
        }

        return deckCards;
    }

    private List<Card> getListOfCards(ResultSet resultSet) throws Exception {
        List<Card> cards = new ArrayList<>();
        String name = "", type = "", id = "", element_type = "", package_id = "";
        int damage = 0;
        try{
            while (resultSet.next()) {
                name = resultSet.getString("name");
                type = resultSet.getString("type");
                id = resultSet.getString("id");
                element_type = resultSet.getString("element_type");
                package_id = resultSet.getString("package_id");
                damage = resultSet.getInt("damage");


                Card card = new Card();

                card.setName(name);
                card.setType(type);
                card.setId(id);
                card.setElement(element_type);
                card.setPackageID(package_id);
                card.setDamage(damage);


                cards.add(card);
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Could not get cards from deck");
        }

        return cards;
    }

    public Card getCard(String cardID, UnitOfWork newUnit) throws Exception {


        cardID=cardID.replaceAll("\"", "");
        Card card=new Card();

        String name="",  type="", id="", element_type="", package_id="";
        int damage = 0;

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM cards WHERE id=?");
            statement.setString(1, cardID);

            ResultSet resultSet= statement.executeQuery();

            if(resultSet.next()){
                name = resultSet.getString("name");
                type = resultSet.getString("type");
                id = resultSet.getString("id");
                element_type = resultSet.getString("element_type");
                package_id = resultSet.getString("package_id");
                damage = resultSet.getInt("damage");



                card.setName(name);
                card.setType(type);
                card.setId(id);
                card.setElement(element_type);
                card.setPackageID(package_id);
                card.setDamage(damage);
            }




        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get cards");
        }

        return card;
    }

}


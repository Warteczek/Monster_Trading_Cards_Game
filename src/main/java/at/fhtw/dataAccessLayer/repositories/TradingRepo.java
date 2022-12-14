package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Trade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TradingRepo {
    public void addTradingDeal(Trade tradeDeal, String username, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("INSERT INTO \"tradingDeals\"(\"Id\", \"CardToTrade\", \"Type\", \"MinimumDamage\", \"creator\") VALUES(?,?,?,?,?)");
            statement.setString(1, tradeDeal.getId());
            statement.setString(2, tradeDeal.getCardToTrade());
            statement.setString(3, tradeDeal.getType());
            statement.setInt(4, tradeDeal.getMinDamage());
            statement.setString(5, username);

            statement.execute();

        }catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Could not add new Trading Deal");
        }
    }

    public boolean checkCardIsLockedInDeck(String id, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM decks WHERE \"firstCard_ID\"=? OR \"secondCard_ID\"=? OR \"thirdCard_ID\"=? OR \"fourthCard_ID\"=?");
            for(int i=1; i<5; i++){
                statement.setString(i, id);
            }



            ResultSet resultSet= statement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Could not add new Trading Deal");
        }
    }

    public boolean tradingDealAlreadyExists(String id, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT \"Id\" FROM \"tradingDeals\" WHERE \"Id\"=?");

            statement.setString(1, id);

            ResultSet resultSet= statement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }

        }catch(SQLException e){
            e.printStackTrace();
            throw new Exception("Could not add new Trading Deal");
        }
    }


    public List<Trade> getTradingDeals(UnitOfWork newUnit) throws Exception {
        List<Trade> allTradingDeals=new ArrayList<>();

        String Id="",  CardToTrade="", Type="";
        int MinimumDamage = 0;

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM \"tradingDeals\"");

            ResultSet resultSet= statement.executeQuery();

            while(resultSet.next()){
                Id = resultSet.getString("Id");
                CardToTrade = resultSet.getString("CardToTrade");
                Type = resultSet.getString("Type");
                MinimumDamage = resultSet.getInt("MinimumDamage");



                Trade trade = new Trade();

                trade.setId(Id);
                trade.setCardToTrade(CardToTrade);
                trade.setType(Type);
                trade.setMinDamage(MinimumDamage);

                allTradingDeals.add(trade);
            }

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get cards");
        }

        return allTradingDeals;
    }

    public void deleteTradingDeal(String dealID, UnitOfWork newUnit) throws Exception {
        try{
            PreparedStatement statement= newUnit.getStatement("DELETE FROM \"tradingDeals\" WHERE \"Id\"=?");
            statement.setString(1, dealID);

            statement.execute();
        }catch (SQLException e){
            e.printStackTrace();
            throw new Exception("could not delete trading deal");
        }
    }

    public String getCardToTrade(String dealID, UnitOfWork newUnit) throws Exception {
        String card="";

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT \"CardToTrade\" FROM \"tradingDeals\" WHERE \"Id\"=?");

            statement.setString(1, dealID);

            ResultSet resultSet= statement.executeQuery();
            if(resultSet.next()){
                card= resultSet.getString("CardToTrade");
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new Exception("could not get card to trade");
        }


        return card;
    }

    public String getCreatorFromDeal(String dealID, UnitOfWork newUnit) throws Exception {
        String creator="";

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT \"creator\" FROM \"tradingDeals\" WHERE \"Id\"=?");

            statement.setString(1, dealID);

            ResultSet resultSet= statement.executeQuery();
            if(resultSet.next()){
                creator= resultSet.getString("creator");
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new Exception("could not get card to trade");
        }
        return creator;
    }

    public Trade getTrade(String dealID, UnitOfWork newUnit) throws Exception {

        Trade deal = new Trade();
        String Id="",  CardToTrade="", Type="";
        int MinimumDamage = 0;

        try{
            PreparedStatement statement= newUnit.getStatement("SELECT * FROM \"tradingDeals\" WHERE \"Id\"=?");
            statement.setString(1, dealID);

            ResultSet resultSet= statement.executeQuery();

            if(resultSet.next()){
                Id = resultSet.getString("Id");
                CardToTrade = resultSet.getString("CardToTrade");
                Type = resultSet.getString("Type");
                MinimumDamage = resultSet.getInt("MinimumDamage");





                deal.setId(Id);
                deal.setCardToTrade(CardToTrade);
                deal.setType(Type);
                deal.setMinDamage(MinimumDamage);
            }

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("Could not get card");
        }

        return deal;
    }

    public void executeTrade(String dealID, String newOwnerDealCard, String newOwnerOfferCard, String dealCard, String offerCard, UnitOfWork newUnit) throws Exception {
        try{
            PreparedStatement statement= newUnit.getStatement("UPDATE stack SET username=? WHERE card_id=? AND username=?");
            statement.setString(1, newOwnerDealCard);
            statement.setString(2, dealCard);
            statement.setString(3, newOwnerOfferCard);

            PreparedStatement statement2= newUnit.getStatement("UPDATE stack SET username=? WHERE card_id=? AND username=?");
            statement2.setString(1, newOwnerOfferCard);
            statement2.setString(2, offerCard);
            statement2.setString(3, newOwnerDealCard);


            statement.executeUpdate();
            statement2.executeUpdate();

            //after executed Deal, the deal is deleted
            deleteTradingDeal(dealID, newUnit);

        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("could not update user data");
        }
    }
}

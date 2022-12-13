package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.Trade;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TradingRepo {

    public void addTradingDeal(Trade tradeDeal, UnitOfWork newUnit) throws Exception {

        try{
            PreparedStatement statement= newUnit.getStatement("INSERT INTO \"tradingDeals\"(\"Id\", \"CardToTrade\", \"Type\", \"MinimumDamage\") VALUES(?,?,?,?)");
            statement.setString(1, tradeDeal.getId());
            statement.setString(2, tradeDeal.getCardToTrade());
            statement.setString(3, tradeDeal.getType());
            statement.setInt(4, tradeDeal.getMinDamage());

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
}

package at.fhtw.dataAccessLayer.repositories;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.mtcg_app.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GameRepo {
    public void updateELO(String winner, String loser, UnitOfWork newUnit) throws Exception {
        try{
            int newWinnerELO=0, newLoserELO=0;
            int newWinnerWins=0, newLoserLosses=0;

            //select winners previous ELO and wins
            PreparedStatement statementSelectWinnerELO= newUnit.getStatement("SELECT elo, wins FROM users WHERE \"username\" = ?");
            statementSelectWinnerELO.setString(1, winner);

            ResultSet rsWinnerELO=statementSelectWinnerELO.executeQuery();
            if(rsWinnerELO.next()){
                newWinnerELO= rsWinnerELO.getInt("elo");
                newWinnerWins= rsWinnerELO.getInt("wins");
            }else{
                throw new Exception("could not SELECT winner ELO");
            }

            // select losers previous ELO and losses
            PreparedStatement statementSelectLoserELO= newUnit.getStatement("SELECT elo, losses FROM users WHERE \"username\" = ?");
            statementSelectLoserELO.setString(1, loser);

            ResultSet rsLoserELO=statementSelectLoserELO.executeQuery();
            if(rsLoserELO.next()){
                newLoserELO= rsLoserELO.getInt("elo");
                newLoserLosses=rsLoserELO.getInt("losses");
            }else{
                throw new Exception("could not SELECT loser ELO");
            }

            newWinnerELO=newWinnerELO+3;
            newLoserELO=newLoserELO-5;
            newWinnerWins++;
            newLoserLosses++;

            //UPDATE winner ELO and wins
            PreparedStatement statementUpdateWinner= newUnit.getStatement("UPDATE users SET elo = ?, wins=? WHERE username = ?");
            statementUpdateWinner.setInt(1, newWinnerELO);
            statementUpdateWinner.setInt(2, newWinnerWins);
            statementUpdateWinner.setString(3, winner);


            //UPDATE user ELO and losses
            PreparedStatement statementUpdateLoser= newUnit.getStatement("UPDATE users SET elo = ?, losses=? WHERE username = ?");
            statementUpdateLoser.setInt(1, newLoserELO);
            statementUpdateLoser.setInt(2, newLoserLosses);
            statementUpdateLoser.setString(3, loser);


            statementUpdateWinner.executeUpdate();
            statementUpdateLoser.executeUpdate();


        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("could not update ELO");
        }
    }

    public void awardCardsToWinner(String winner, String loser, List<Card> loserDeck, UnitOfWork newUnit) throws Exception {
        try{

            PreparedStatement statementDeleteDeck= newUnit.getStatement("UPDATE decks SET \"firstCard_ID\" = ?, \"secondCard_ID\" = ?, \"thirdCard_ID\" = ?, \"fourthCard_ID\" = ? WHERE \"owner_id\" = ?");
            statementDeleteDeck.setString(1, null);
            statementDeleteDeck.setString(2, null);
            statementDeleteDeck.setString(3, null);
            statementDeleteDeck.setString(4, null);
            statementDeleteDeck.setString(5, loser);

            PreparedStatement statementAwardCards= newUnit.getStatement("UPDATE stack SET username=? WHERE card_id=? OR card_id=? OR card_id=? OR card_id=?");
            statementAwardCards.setString(1, winner);
            statementAwardCards.setString(2, loserDeck.get(0).getId());
            statementAwardCards.setString(3, loserDeck.get(1).getId());
            statementAwardCards.setString(4, loserDeck.get(2).getId());
            statementAwardCards.setString(5, loserDeck.get(3).getId());


            statementDeleteDeck.executeUpdate();
            statementAwardCards.executeUpdate();


        } catch(SQLException exception){
            exception.printStackTrace();
            throw new Exception("could not award cards to winner");
        }
    }
}

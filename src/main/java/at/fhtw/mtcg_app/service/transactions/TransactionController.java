package at.fhtw.mtcg_app.service.transactions;

import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;

import java.sql.SQLException;
import java.util.List;

import static at.fhtw.httpserver.server.Service.newUnit;

public class TransactionController extends Controller {
    private PackageRepo packageRepo;
    private UserRepo userRepo;

    public TransactionController(PackageRepo packageRepo, UserRepo userRepo) {
        this.packageRepo = packageRepo;
        this.userRepo = userRepo;
    }


    public Response purchasePackage(Request request) {
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "Authentication information is missing or invalid"
            );
        }

        boolean userExists=this.userRepo.checkUserExists(username, newUnit);
        if(!userExists){
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "User can not be found"
            );
        }

        boolean userHasEnoughMoney=this.userRepo.checkUserHasEnoughMoneyForPackage(username, newUnit);

        if(!userHasEnoughMoney){
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "Not enough money for buying a card package"
            );
        }

        try{
            //gives back all possible packages
            List<String> packageIDs= this.packageRepo.getPackages(newUnit);

            //checks if there is at least one package
            if(packageIDs.size()<1){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "No card package available for buying"
                );
            }

            //selects a random package from all possible Packages
            String buyPackageID=selectRandomPackage(packageIDs);

            //subtracts the amount of coins, that a package costs from the users coins
            this.userRepo.subtractPackageCoinsFromUser(username, newUnit);

            //assign the Packages cards to the user:
            this.packageRepo.addPackageToStack(username, buyPackageID, newUnit);

            newUnit.commit();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "A package has been successfully bought"
            );

        }catch(SQLException e){
            e.printStackTrace();
        }

        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    private String selectRandomPackage(List<String> packageIDs) {

    }
}

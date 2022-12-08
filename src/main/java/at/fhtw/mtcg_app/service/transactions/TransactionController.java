package at.fhtw.mtcg_app.service.transactions;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class TransactionController extends Controller {
    private PackageRepo packageRepo;
    private UserRepo userRepo;

    public TransactionController(PackageRepo packageRepo, UserRepo userRepo) {
        this.packageRepo = packageRepo;
        this.userRepo = userRepo;
    }


    public Response purchasePackage(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        String username = request.getTokenUser();

        if(username.equals("")){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }

        try{
            boolean userExists=this.userRepo.checkUserExists(username, newUnit);
            if(!userExists){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User can not be found"
                );
            }

            boolean userHasEnoughMoney=this.userRepo.checkUserHasEnoughMoneyForPackage(username, newUnit);

            if(!userHasEnoughMoney){
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "Not enough money for buying a card package"
                );
            }



            //gives back all possible packages
            List<String> packageIDs= this.packageRepo.getPackages(newUnit);

            //checks if there is at least one package
            if(packageIDs.isEmpty()){
                System.out.println("Empty");
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
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
                    ContentType.PLAIN_TEXT,
                    "A package has been successfully bought"
            );

        }catch(Exception e){
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
        String selectedPackage;

        int randomNum;

        int numberOfEntries=packageIDs.size();
        if(numberOfEntries<=1){
            randomNum=0;
        }else{
            randomNum = ThreadLocalRandom.current().nextInt(0, numberOfEntries-1);
        }

        selectedPackage= packageIDs.get(randomNum);


        return selectedPackage;
    }
}

package at.fhtw.mtcg_app.service.packages;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.Card;

public class PackageController extends Controller {

    private PackageRepo packageRepo;

    public PackageController(PackageRepo packageRepo) {
        this.packageRepo = packageRepo;
    }
    public Response createPackage(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        try {
            if(!request.checkAdminToken()){
                newUnit.close();
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "Provided user is not \"admin\""
                );
            }

            String packageID;
            do{
                packageID=generatePackageID(15);
            }while(this.packageRepo.checkPackageExists(packageID, newUnit));



            Card[] cards = this.getObjectMapper().readValue(request.getBody(), Card[].class);
            for (Card card : cards){
                if(card.getName().contains("Fire")){
                    card.setElement("fire");
                }else if(card.getName().contains("Water")){
                    card.setElement("water");
                }else{
                    card.setElement("normal");
                }

                if(card.getName().contains("Spell")){
                    card.setType("spell");
                }else{
                    card.setElement("monster");
                }

                if(this.packageRepo.checkCardExists(card.getId(), newUnit)){
                    newUnit.close();
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.PLAIN_TEXT,
                            "At least one card in the packages already exists"
                    );
                }


            }
            this.packageRepo.addPackage(cards, packageID, newUnit);

            newUnit.commit();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    "Package and cards successfully created"
            );


        } catch (Exception e) {
            e.printStackTrace();
        }

        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    private String generatePackageID(int length) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

}

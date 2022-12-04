package at.fhtw.mtcg_app.service.packages;

import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.Card;
import at.fhtw.mtcg_app.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static at.fhtw.httpserver.server.Service.newUnit;

public class PackageController extends Controller {

    private PackageRepo packageRepo;

    public PackageController(PackageRepo packageRepo) {
        this.packageRepo = packageRepo;
    }
    public Response createPackage(Request request) {
        if(!request.checkAdminToken()){
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "Authentication information is missing or invalid"
            );
        }

        try {
            Card[] cards = this.getObjectMapper().readValue(request.getBody(), Card[].class);
            boolean created=this.packageRepo.addPackage(cards, newUnit);

            if(created){
                newUnit.commit();

                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "Package and cards successfully created"
                );

            }else{
                newUnit.rollback();
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "At least one card in the packages already exists"
                );
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}

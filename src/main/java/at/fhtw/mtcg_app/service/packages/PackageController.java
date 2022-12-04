package at.fhtw.mtcg_app.service.packages;

import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import static at.fhtw.httpserver.server.Service.newUnit;

public class PackageController extends Controller {

    private PackageRepo packageRepo;

    public PackageController(PackageRepo packageRepo) {
        this.packageRepo = packageRepo;
    }
    public Response createPackage(Request request) {
        if(!request.checkAdminToken()){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "Authentication information is missing or invalid"
            );
        }
        /*
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            if(this.userRepo.checkUserExists(user.getUsername(), newUnit)){
                newUnit.rollback();
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "User with same username already registered"
                );
            }
            this.userRepo.addUser(user, newUnit);
            newUnit.commit();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "User successfully created"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}

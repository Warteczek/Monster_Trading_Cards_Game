package at.fhtw.mtcg_app.service.user;

import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import at.fhtw.dataAccessLayer.UnitOfWork;
import static at.fhtw.httpserver.server.Service.newUnit;

public class UserController extends Controller {
    private UserRepo userRepo;

    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    public Response addUser(Request request) {
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            if(this.userRepo.checkUserExists(user, newUnit)){
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
        }

        newUnit.rollback();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }

    public Response updateUser(Request request){

        return null;
    }

    public Response login(Request request){
        return null;

    }

    public Response getUserdata(Request request) {
        String username= request.getPathParts().get(1);
        try {
            User user = this.userRepo.getUserData(username);
            String userDataJSON = this.getObjectMapper().writeValueAsString(user);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userDataJSON
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
}

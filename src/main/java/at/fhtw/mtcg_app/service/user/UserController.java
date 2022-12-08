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

public class UserController extends Controller {
    private UserRepo userRepo;

    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    public Response addUser(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            if(this.userRepo.checkUserExists(user.getUsername(), newUnit)){
                newUnit.rollback();
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "User with same username already registered"
                );
            }
            this.userRepo.addUser(user, newUnit);
            newUnit.commit();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    "User successfully created"
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

    public Response updateUser(Request request){
        UnitOfWork newUnit = new UnitOfWork();
        if(!request.checkAuthenticationToken()){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }

        try {

            String username= request.getPathParts().get(1);
            if(!this.userRepo.checkUserExists(username, newUnit)){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found"
                );
            }

            User user = this.getObjectMapper().readValue(request.getBody(), User.class);

            this.userRepo.updateUserData(username, user, newUnit);

            newUnit.commit();

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "User successfully updated"
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

    public Response getUserdata(Request request) {
        UnitOfWork newUnit = new UnitOfWork();
        if(!request.checkAuthenticationToken()){
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Authentication information is missing or invalid"
            );
        }

        String username= request.getPathParts().get(1);
        try {
            if(!this.userRepo.checkUserExists(username, newUnit)){
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found"
                );
            }
            User user = this.userRepo.getUserData(username, newUnit);
            String userDataJSON = this.getObjectMapper().writeValueAsString(user);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userDataJSON
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
}

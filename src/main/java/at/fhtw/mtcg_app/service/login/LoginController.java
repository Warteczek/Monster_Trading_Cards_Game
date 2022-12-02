package at.fhtw.mtcg_app.service.login;

import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import static at.fhtw.httpserver.server.Service.newUnit;

public class LoginController extends Controller {

    private UserRepo userRepo;

    public LoginController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Response login(Request request){
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);

            String content=this.userRepo.checkCredentials(user, newUnit);

            if(content=="None"){
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "Invalid username/password provided"
                );
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "User login successful\n" + content
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}

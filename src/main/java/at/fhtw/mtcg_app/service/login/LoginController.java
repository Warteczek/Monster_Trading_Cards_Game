package at.fhtw.mtcg_app.service.login;

import at.fhtw.dataAccessLayer.UnitOfWork;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.mtcg_app.controller.Controller;
import at.fhtw.mtcg_app.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public class LoginController extends Controller {

    private UserRepo userRepo;

    public LoginController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Response login(Request request){
        UnitOfWork newUnit = new UnitOfWork();
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);

            String content=this.userRepo.checkCredentials(user, newUnit);

            if(content=="None"){
                newUnit.close();
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "Invalid username/password provided"
                );
            }

            newUnit.close();
            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "User login successful\n" + content
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        newUnit.close();
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Internal Server Error\" }"
        );
    }
}

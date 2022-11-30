package at.fhtw.mtcg_app.service.user;

import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class UserService implements Service {

    private final UserController userController;

    public UserService() {
        this.userController = new UserController(new UserRepo());
    }

    @Override
    public Response handleRequest(Request request) {

        if (request.getMethod() == Method.POST) {
            return this.userController.addUser(request);
        } else if (request.getMethod() == Method.GET) {
            //return this.userController.getUserdata();
        } else if (request.getMethod() == Method.PUT) {
            return this.userController.updateUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}

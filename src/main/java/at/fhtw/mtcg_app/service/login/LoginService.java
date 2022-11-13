package at.fhtw.mtcg_app.service.login;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class LoginService implements Service {

    private final LoginController loginController;

    public LoginService() {
        this.loginController = new LoginController();
    }

    @Override
    public Response handleRequest(Request request) {
        System.out.println(request.getPathParts().get(1));
        if (request.getMethod() == Method.POST) {
            return this.loginController.login(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}

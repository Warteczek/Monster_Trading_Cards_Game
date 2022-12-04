package at.fhtw.mtcg_app.service.packages;

import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mtcg_app.service.user.UserController;

public class PackageService implements Service {

    private final PackageController packageController;

    public PackageService() {
        this.packageController = new PackageController(new PackageRepo());
    }
    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return this.packageController.createPackage(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}

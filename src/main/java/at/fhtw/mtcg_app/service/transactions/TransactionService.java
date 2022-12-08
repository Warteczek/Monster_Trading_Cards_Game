package at.fhtw.mtcg_app.service.transactions;

import at.fhtw.dataAccessLayer.repositories.PackageRepo;
import at.fhtw.dataAccessLayer.repositories.UserRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.mtcg_app.service.login.LoginController;

public class TransactionService implements Service {

    private final TransactionController transactionController;

    public TransactionService() {
        this.transactionController = new TransactionController(new PackageRepo(), new UserRepo());
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST && request.getPathParts().get(1).equals("packages")) {
            return this.transactionController.purchasePackage(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}

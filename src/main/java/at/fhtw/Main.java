package at.fhtw;

import at.fhtw.httpserver.utils.Router;
import at.fhtw.httpserver.server.Server;
import at.fhtw.mtcg_app.service.cards.CardsService;
import at.fhtw.mtcg_app.service.echo.EchoService;
import at.fhtw.mtcg_app.service.login.LoginService;
import at.fhtw.mtcg_app.service.packages.PackageService;
import at.fhtw.mtcg_app.service.user.UserService;
import at.fhtw.mtcg_app.service.weather.WeatherService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/weather", new WeatherService());
        router.addService("/echo", new EchoService());
        router.addService("/users", new UserService());
        router.addService("/sessions", new LoginService());
        router.addService("/packages", new PackageService());
        router.addService("/cards", new CardsService());


        return router;
    }
}

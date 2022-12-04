package at.fhtw.httpserver.server;

import at.fhtw.dataAccessLayer.UnitOfWork;

public interface Service {

    UnitOfWork newUnit = new UnitOfWork();
    Response handleRequest(Request request);
}

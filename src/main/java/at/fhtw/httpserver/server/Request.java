package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.Method;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private Method method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private HeaderMap headerMap =  new HeaderMap();
    private String body;

    private boolean hasParams;

    public boolean hasParams() {
        return hasParams;
    }

    public String getServiceRoute(){
        if (this.pathParts == null ||
            this.pathParts.isEmpty()) {
            return null;
        }

        return '/' + this.pathParts.get(0);
    }

    public String getUrlContent(){
        return this.urlContent;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        hasParams = urlContent.indexOf("?") != -1;

        if (hasParams) {
            String[] pathParts =  urlContent.split("\\?");
            this.setPathname(pathParts[0]);
            this.setParams(pathParts[1]);
        }
        else
        {
            this.setPathname(urlContent);
            this.setParams(null);
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPathname() {
        return pathname;
    }


    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part :stringParts)
        {
            if (part != null &&
                part.length() > 0)
            {
                this.pathParts.add(part);
            }
        }

    }
    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public HeaderMap getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HeaderMap headerMap) {
        this.headerMap = headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getPathParts() {
        return pathParts;
    }

    public void setPathParts(List<String> pathParts) {
        this.pathParts = pathParts;
    }

    public boolean checkAuthenticationToken(){
        String token = this.headerMap.getHeader("Authorization");
        if (token == null) {
            return false;
        }

        String[] splitAuth = token.split(" ");
        String[] splitToken = splitAuth[1].split("-");
        if(splitToken[0].equals(getPathParts().get(1)) && splitToken[1].equals("mtcgToken")){
            return true;
        }
        return false;
    }

    public boolean checkAdminToken(){
        String token = this.headerMap.getHeader("Authorization");
        if (token == null) {
            return false;
        }

        String[] splitAuth = token.split(" ");
        String[] splitToken = splitAuth[1].split("-");
        if(splitToken[0].equals("admin") && splitToken[1].equals("mtcgToken")){
            return true;
        }
        return false;
    }

    public String getTokenUser(){
        String token = this.headerMap.getHeader("Authorization");

        if (token == null) {
            return "";
        }


        String[] splitAuth = token.split(" ");
        String[] splitToken = splitAuth[1].split("-");
        if(splitToken[1].equals("mtcgToken")){
            return splitToken[0];
        }
        return "";
    }
}

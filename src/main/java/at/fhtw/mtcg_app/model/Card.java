package at.fhtw.mtcg_app.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Card {

    @JsonAlias({"Id"})
    private String id;
    @JsonAlias({"Name"})
    private String name;
    @JsonAlias({"Damage"})
    private int damage;
    @JsonAlias({"Type"})
    private String type;
    @JsonAlias({"Element"})
    private String element;
    @JsonAlias({"PackageID"})
    private int packageID;
}

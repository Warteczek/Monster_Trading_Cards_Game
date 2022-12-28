create table users
(
    username   text    not null
        constraint pk_username
            primary key,
    password   text    not null,
    coins      integer not null,
    elo        integer not null,
    wins       integer not null,
    losses     integer not null,
    bio        text,
    image      text,
    name       text,
    mtcg_token text
);

alter table users
    owner to "TeMarcelo";

create table cards
(
    name           text,
    type           text,
    id             text                 not null
        constraint pk_id
            primary key,
    damage         integer,
    element_type   text,
    package_id     text,
    buyable        boolean default true not null,
    created_number integer generated always as identity (minvalue 0)
);

alter table cards
    owner to "TeMarcelo";

create table decks
(
    owner_id        text not null
        constraint key_name
            primary key
        constraint fk_owner_name
            references users
            on delete cascade,
    "firstCard_ID"  text,
    "secondCard_ID" text,
    "thirdCard_ID"  text,
    "fourthCard_ID" text
);

alter table decks
    owner to "TeMarcelo";

create table stack
(
    username text
        constraint fk_username
            references users
            on delete cascade,
    card_id  text
        constraint fk_card_id
            references cards
            on delete cascade
);

alter table stack
    owner to "TeMarcelo";

create table "tradingDeals"
(
    "Id"            text,
    "CardToTrade"   text
        constraint fk_card_id
            references cards
            on delete cascade,
    "Type"          text,
    "MinimumDamage" text,
    creator         text
        constraint foreign_key_name
            references users
            on delete cascade
);

alter table "tradingDeals"
    owner to "TeMarcelo";

drop table if exists FRIENDS cascade;
create table FRIENDS
(
    USER_ID       INTEGER,
    FRIEND_ID     INTEGER,
    FRIEND_STATUS CHARACTER VARYING(20),
    constraint FRIENDS_PK primary key (USER_ID, FRIEND_ID)
);

drop table if exists GENRES cascade;
create table if not exists GENRES
(
    GENRE_ID   INTEGER auto_increment primary key,
    GENRE_NAME CHARACTER VARYING(50)
);

drop table if exists FILM_GENRE cascade;
create table if not exists FILM_GENRE
(
    FILM_ID INTEGER,
    GENRE_ID INTEGER,
    constraint FILM_GENRE_PK primary key (FILM_ID, GENRE_ID)
);

drop table if exists LIKES cascade;
create table if not exists LIKES
(
    FILM_ID INTEGER,
    USER_ID INTEGER,
    constraint LIKES_PK primary key (FILM_ID, USER_ID)
);

drop table if exists RATINGS cascade;
create table if not exists RATINGS
(
    RATING_ID   INTEGER auto_increment primary key,
    RATING_NAME CHARACTER VARYING(10)
);

drop table if exists FILMS cascade;
create table if not exists FILMS
(
    FILM_ID          INTEGER auto_increment primary key,
    FILM_NAME        CHARACTER VARYING(50),
    FILM_DESCRIPTION CHARACTER VARYING(200),
    FILM_RELEASEDATE DATE,
    FILM_DURATION    INTEGER,
    RATING_ID        INTEGER
);

drop table if exists USERS cascade;
create table if not exists USERS
(
    USER_ID       INTEGER auto_increment primary key,
    USER_EMAIL    CHARACTER VARYING(50),
    USER_LOGIN    CHARACTER VARYING(50),
    USER_NAME     CHARACTER VARYING(50),
    USER_BIRTHDAY DATE
);

alter table FILM_GENRE add constraint FILM_GENRE_FILMS_FILM_ID_FK
    foreign key (FILM_ID) references FILMS (FILM_ID);

alter table FILM_GENRE add constraint FILM_GENRE_GENRES_GENRE_ID_FK
    foreign key (GENRE_ID) references GENRES (GENRE_ID);

alter table LIKES add constraint LIKES_FILMS_FILM_ID_FK
    foreign key (FILM_ID) references FILMS (FILM_ID);

alter table LIKES add constraint LIKES_USER_USER_ID_FK
    foreign key (USER_ID) references USERS (USER_ID);

alter table FILMS add constraint FILMS_RATINGS_RATING_ID_FK
    foreign key (RATING_ID) references RATINGS (RATING_ID);

alter table FRIENDS add constraint FRIENDS_USERS_USER_ID_FK
    foreign key (USER_ID) references USERS (USER_ID);

alter table FRIENDS add constraint FRIENDS_USERS_FRIEND_ID_FK
    foreign key (FRIEND_ID) references USERS (USER_ID);
create table users (
    id int primary key auto_increment,
    name nvarchar(100) not null ,
    user_email nvarchar(100) not null unique ,
    user_password nvarchar(255) not null
);
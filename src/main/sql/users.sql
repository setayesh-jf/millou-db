create table users (
    id int primary key auto_increment,
    name nvarchar(100) not null ,
    user_email nvarchar(100) not null unique ,
    user_password nvarchar(255) not null
);


select name, user_email
from users
where id = ?;


select
name, user_email
from users
group by user_email, name;

select * from users;

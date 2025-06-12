create table email_recipients (
    id int primary key auto_increment,
    email_id int not null ,
    user_id int not null ,
    is_read boolean not null default false,
    read_at timestamp not null ,
    foreign key (email_id) references emails(id),
    foreign key (user_id) references users(id)
);
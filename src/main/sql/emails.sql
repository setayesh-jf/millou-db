create table emails (
    id int primary key auto_increment,
    code char(6) not null unique,
    sender_id int not null ,
    email_subject nvarchar(255) not null ,
    email_body text not null ,
    sent_at timestamp not null default current_timestamp,
    original_email_id int not null,
    foreign key (sender_id) references users(id),
    foreign key (original_email_id) references emails(id)
);
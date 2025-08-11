create table email_recipients (
    id int primary key auto_increment,
    email_id int not null ,
    user_id int not null ,
    is_read boolean not null default false,
    read_at timestamp not null ,
    foreign key (email_id) references emails(id),
    foreign key (user_id) references users(id)
);

alter table email_recipients modify column read_at timestamp null;

select is_read, read_at
from email_recipients
where email_id = ? and user_id = ?;



select e.code, e.email_subject, e.sent_at,
u.name AS sender_name  from
email_recipients er
join emails e on er.email_id = e.id
join  users u on e.sender_id = u.id
where er.user_id = 3;





select * from email_recipients;

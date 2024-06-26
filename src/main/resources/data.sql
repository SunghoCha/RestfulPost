insert into users(user_id, join_date, name, password, ssn) values(90001, now(), 'User1', 'test1111', '701010-1111111');
insert into users(user_id, join_date, name, password, ssn) values(90002, now(), 'User1', 'test2222', '701010-2222222');
insert into users(user_id, join_date, name, password, ssn) values(90003, now(), 'User1', 'test3333', '701010-3333333');

insert into post(description, user_id) values('My first post', 90001);
insert into post(description, user_id) values('My second post', 90001);

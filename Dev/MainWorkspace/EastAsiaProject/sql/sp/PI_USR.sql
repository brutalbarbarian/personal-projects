drop procedure if exists PI_USR;
delimiter //
create procedure PI_USR(
	in $usr_id int,
	in $usr_name varchar(50),
	in $usr_password varchar(50),
	in $usr_description text,
	in $usr_timestamp varchar(50)
) begin

insert TR_USR_user
(usr_id, usr_name, usr_password, usr_description, usr_timestamp)
values
($usr_id, $usr_name, $usr_password, $usr_description, $usr_timestamp);

end //
delimiter ;
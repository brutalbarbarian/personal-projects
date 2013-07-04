drop procedure if exists PU_USR;
delimiter //
create procedure PU_USR(
	in $usr_id int,
	in $usr_name varchar(50),
	in $usr_password varchar(50),
	in $usr_description text,
	in $usr_timestamp varchar(50)
) begin

update TR_USR_user
set	usr_name = $usr_name,
	usr_password = $usr_password,
	usr_description = $usr_description,
	usr_timestamp = $usr_timestamp
where usr_id = $usr_id;

end //
delimiter ;
drop procedure if exists PS_USR;
delimiter //
create procedure PS_USR(
	in $usr_id int,
	in $usr_name varchar(50)
) begin

select	usr_id, usr_name, usr_password, usr_description, usr_timestamp
  from	TR_USR_user
 where	(not (($usr_id is null) and ($usr_name is null)))
   and	($usr_id is null or usr_id = $usr_id)
   and	($usr_name is null or usr_name = $usr_name);

end //
delimiter ;
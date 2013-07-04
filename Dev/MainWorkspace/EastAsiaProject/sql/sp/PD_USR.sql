drop procedure if exists PD_USR;
delimiter //
create procedure PD_USR(
	in $usr_id int
) begin

delete from TR_USR_user
where usr_id = $usr_id;

end //
delimiter ;
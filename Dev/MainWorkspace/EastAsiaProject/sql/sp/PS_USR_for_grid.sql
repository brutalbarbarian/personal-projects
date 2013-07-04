drop procedure if exists PS_USR_for_grid;
delimiter //
create procedure PS_USR_for_grid(
) begin

select	usr_id
  from	TR_USR_user;

end //
delimiter ;
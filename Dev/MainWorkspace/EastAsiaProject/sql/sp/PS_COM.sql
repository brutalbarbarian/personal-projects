drop procedure if exists PS_COM;
delimiter //
create procedure PS_COM(
in $com_id int
) begin

select	com_id, com_name
  from	TR_COM_company
 where	com_id = $com_id;

end //
delimiter ;
drop procedure if exists PU_COM;
delimiter //
create procedure PU_COM(
in $com_id int,
in $com_name varchar(50)
) begin

update TR_COM_company
set com_name = $com_name
where com_id = $com_id;

end //
delimiter ;
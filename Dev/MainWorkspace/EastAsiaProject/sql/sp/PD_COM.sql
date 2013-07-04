drop procedure if exists PD_COM;
delimiter //
create procedure PD_COM(
in $com_id int
) begin

delete from TR_COM_company
where com_id = $com_id;

end //
delimiter ;
drop procedure if exists PS_COM_all;
delimiter //
create procedure PS_COM_all(
) begin

select	com_id
  from	TR_COM_company;

end //
delimiter ;
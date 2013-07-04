drop procedure if exists PI_COM;
delimiter //
create procedure PI_COM(
in $com_id int,
in $com_name varchar(50)
) begin

insert TR_COM_company
(com_id, com_name)
values ($com_id, $com_name);

end //
delimiter ;
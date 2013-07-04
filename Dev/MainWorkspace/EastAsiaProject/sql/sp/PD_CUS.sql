drop procedure if exists PD_CUS;
delimiter //
create procedure PD_CUS(
in $cus_id int
) begin

delete from TM_CUS_customer
where cus_id = $cus_id;

end //
delimiter ;
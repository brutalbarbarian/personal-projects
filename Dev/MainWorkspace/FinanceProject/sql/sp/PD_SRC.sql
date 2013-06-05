drop procedure if exists PD_SRC;
delimiter //
create procedure PD_SRC(
	in $src_id int
) begin

delete from TR_SRC_source
where src_id = $src_id;

end //
delimiter ;
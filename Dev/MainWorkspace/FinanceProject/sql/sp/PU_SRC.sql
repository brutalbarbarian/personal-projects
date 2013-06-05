drop procedure if exists PU_SRC;
delimiter //
create procedure PU_SRC(
	in $src_id int,
	in $src_name varchar(50)
) begin

update TR_SRC_source
set src_name = $src_name
where src_id = $src_id;

end //
delimiter ;
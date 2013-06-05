drop procedure if exists PI_SRC;
delimiter //
create procedure PI_SRC(
	in $src_id int,
	in $src_name varchar(50)
) begin

insert TR_SRC_source
(src_id, src_name)
values
($src_id, $src_name);

end //
delimiter ;
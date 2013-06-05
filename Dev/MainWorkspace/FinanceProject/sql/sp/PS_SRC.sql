drop procedure if exists PS_SRC_for_set;
delimiter //
create procedure PS_SRC_for_set(
) begin

select	src_id
  from	TR_SRC_source;

end //
delimiter ;
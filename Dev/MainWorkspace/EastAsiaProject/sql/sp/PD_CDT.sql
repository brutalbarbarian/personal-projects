drop procedure if exists PD_CDT;
delimiter //
create procedure PD_CDT(
in $cdt_id int,
in $cdt_source_type int
) begin

delete from	TR_CDT_contact_details 
 where	cdt_id = $cdt_id
   and	cdt_source_type = $cdt_source_type;

end //
delimiter ;
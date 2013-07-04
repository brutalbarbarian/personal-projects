drop procedure if exists PS_CDT;
delimiter //
create procedure PS_CDT(
in $cdt_id int,
in $cdt_source_type int
) begin

select	cdt_id, cdt_source_type, cdt_address_1, cdt_address_2, cdt_address_3, cdt_city, 
		cdt_country, cdt_postcode, cdt_phone, cdt_mobile, cdt_fax, cdt_site 
  from	TR_CDT_contact_details 
 where	cdt_id = $cdt_id
   and	cdt_source_type = $cdt_source_type;


end //
delimiter ;
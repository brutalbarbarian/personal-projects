drop procedure if exists PU_CDT;
delimiter //
create procedure PU_CDT(
in $cdt_id int, 
in $cdt_source_type int,
in $cdt_address_1 varchar(50), 
in $cdt_address_2 varchar(50), 
in $cdt_address_3 varchar(50), 
in $cdt_city varchar(50), 
in $cdt_country varchar(50), 
in $cdt_postcode varchar(50), 
in $cdt_phone varchar(50), 
in $cdt_mobile varchar(50), 
in $cdt_fax varchar(50), 
in $cdt_site varchar(50)
) begin

update TR_CDT_contact_details
set cdt_address_1 = $cdt_address_1, 
	cdt_address_2 = $cdt_address_2, 
	cdt_address_3 = $cdt_address_3, 
	cdt_city = $cdt_city, 
	cdt_country = $cdt_country, 
	cdt_postcode = $cdt_postcode, 
	cdt_phone = $cdt_phone, 
	cdt_mobile = $cdt_mobile, 
	cdt_fax = $cdt_fax, 
	cdt_site = $cdt_site
where cdt_id = $cdt_id 
and cdt_source_type = $cdt_source_type;

end //
delimiter ;
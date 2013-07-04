drop procedure if exists PS_CUS_quick_find;
delimiter //
create procedure PS_CUS_quick_find(
in $cus_id int,
in $cus_name varchar(50),
in $cus_address varchar(50),
in $cus_contact varchar(50)
) begin

if not coalesce($cus_id, 0) = 0 then
	select cus_id 
	from TM_CUS_customer
	where cus_id = $cus_id
	and cus_is_active;
else if not coalesce($cus_name, '') = '' then
	select cus_id 
	from TM_CUS_customer
	where concat_ws(', ', if(cus_name_last = '', null, cus_name_last), if(cus_name_first = '', null, cus_name_first)) like concat('%', $cus_name, '%')
	and cus_is_active;
else if not coalesce($cus_address, '') = '' then
	select cus_id 
	from TM_CUS_customer cus
	inner join TR_CDT_contact_detail cdt
	on cdt_id = cus_id and cdt_source_type = 2
	where concat_ws(', ', if(cdt_address_1 = '', null, cdt_address_1), if(cdt_address_2 = '', null, cdt_address_2), if(cdt_address_3 = '', null, cdt_address_3),
		if(cdt_city = '', null, cdt_city), if(cdt_postcode = '', null, cdt_postcode), if(cdt_country = '', null, cdt_country)) like concat('%',$cus_address,'%')
	and cus_is_active;
else if not coalesce($cus_contact, '') = '' then
	select cus_id 
	from TM_CUS_customer
inner join TR_CDT_contact_detail cdt
	on cdt_id = cus_id and cdt_source_type = 2
	where 	(cdt_phone like concat('%', $cus_contact, '%')
		or	cdt_mobile like concat('%', $cus_contact, '%')
		or	cdt_fax like concat('%', $cus_contact, '%')
		or	cdt_site like concat('%', $cus_contact, '%'))
	and cus_is_active;
else
	select cus_id from TM_CUS_customer
	where 1 = 0;	-- return nothing
end if;
end if;
end if;
end if;

end //
delimiter ;
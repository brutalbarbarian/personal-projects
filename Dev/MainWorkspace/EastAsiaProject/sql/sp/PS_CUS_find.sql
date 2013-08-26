drop procedure if exists PS_CUS_find;
delimiter //
create procedure PS_CUS_find(
in $cus_name varchar(50),
in $cus_address varchar(50),
in $cus_number varchar(50),
in $student boolean,
in $allow_inactive boolean
) begin

select cus_id, 
		cus_name,
		concat_ws(', ', if(cdt_address_1 = '', null, cdt_address_1), if(cdt_address_2 = '', null, cdt_address_2), if(cdt_address_3 = '', null, cdt_address_3)) AS cdt_address,
		cdt_phone, cdt_mobile, cus_is_active, cus_is_student
  from TM_CUS_customer cus 
 inner join TR_CDT_contact_details cdt 
    on	cdt.cdt_id = cus.cus_id	
   and	cdt.cdt_source_type = 2 -- CDT_SOURCE_TYPE_CUSTOMER
 where ($cus_name is null or cus_name like concat('%', $cus_name, '%'))
   and	($cus_address is null or concat_ws(', ', if(cdt_address_1 = '', null, cdt_address_1), if(cdt_address_2 = '', null, cdt_address_2), if(cdt_address_3 = '', null, cdt_address_3),
		if(cdt_city = '', null, cdt_city), if(cdt_postcode = '', null, cdt_postcode), if(cdt_country = '', null, cdt_country)) like concat('%',$cus_address,'%'))
   and ($cus_number is null or $cus_number like cdt_phone or $cus_number like cdt_mobile)
   and ($student is null or $student = cus_is_student)
   and ($allow_inactive or cus_is_active);


end //
delimiter ;
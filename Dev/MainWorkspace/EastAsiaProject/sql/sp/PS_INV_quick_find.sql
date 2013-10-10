drop procedure if exists PS_INV_quick_find;
delimiter //
create procedure PS_INV_quick_find(
$inv_id int,
$wrk_id int,
$cus_name varchar(50)
) begin

if not coalesce($inv_id, 0) = 0 then
	select inv_id
	from TM_INV_invoice
	where inv_id = $inv_id;
else if not coalesce($wrk_id, 0) = 0 then
	select inv_id
	from TM_INV_invoice
	where wrk_id = $wrk_id;
else if not coalesce(@cus_name, '') = '' then 
	select inv_id
	from TM_INV_invoice inv
	inner join TM_WRK_work wrk
	on inv.wrk_id = wrk.wrk_id
	inner join TM_CUS_customer cus
	on wrk.cus_id = cus.cus_id
	where coalesce(cus_name, '') like concat('%', $cus_name, '%');
else 
	select inv_id
	from TM_INV_invoice
	where 1 = 0;
end if;
end if;
end if;

end //
delimiter ;
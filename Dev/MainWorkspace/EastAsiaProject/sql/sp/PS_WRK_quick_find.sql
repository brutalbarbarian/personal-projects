drop procedure if exists PS_WRK_quick_find;
delimiter //
create procedure PS_WRK_quick_find(
$cus_name varchar(50),
$wrk_id int
) begin

if not coalesce($cus_name, '') = '' then
	select wrk_id
	from TM_WRK_work wrk
	left join TM_CUS_customer cus
	on wrk.cus_id = cus.cus_id
	where coalesce(cus_name, '') like concat('%', $cus_name, '%');	
else if not coalesce($wrk_id, 0) = 0 then
	select wrk_id
	from TM_WRK_work wrk
	where wrk_id = $wrk_id;
else
	select wrk_id
	from TM_WRK_work
	where 1 = 0;	-- return nothing
end if;
end if;

end //
delimiter ;
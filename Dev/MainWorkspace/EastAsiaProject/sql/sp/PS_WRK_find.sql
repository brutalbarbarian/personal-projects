drop procedure if exists PS_WRK_find;
delimiter //
create procedure PS_WRK_find(
$cus_name varchar(50),
$wrk_date_due_start date,
$wrk_date_due_end date,
$usr_id_created int,
$wrk_has_outstanding boolean,
$wrk_value_min double(16,2),
$wrk_value_max double(16,2),
$wrk_stage int
) begin

create table $result
(wrk_id int,
cus_name varchar(50),
usr_name_created varchar(50),
wrk_date_create date,
wrk_date_due date,
wrk_stage int,
wrk_total double(16,2),
wrk_outstanding double(16,2)
);

-- insert the basic results
insert $result
(wrk_id, wrk_total)
select wrk.wrk_id, sum(coalesce(wki_price, 0) * coalesce(wki_quantity, 0))
from TM_WRK_work wrk
left join TM_WKI_work_item wki
on wrk.wrk_id = wki.wrk_id
left join TM_CUS_customer cus
on cus.cus_id = wrk.cus_id
where ($cus_name is null or cus_name like concat('%', $cus_name, '%'))
and ($wrk_date_due_start is null or wrk_date_due >= $wrk_date_due_start)
and ($wrk_date_due_end is null or wrk_date_due <= $wrk_date_due_end)
and ($usr_id_created is null or wrk.usr_id_created = $usr_id_created)
and ($wrk_stage is null or wrk.wrk_stage = $wrk_stage)
-- and ($wrk_value_min is null or sum(coalesce(wki_price, 0) * coalesce(wki_quantity, 0)) >= $wrk_value_min)
-- and ($wrk_value_max is null or sum(coalesce(wki_price, 0) * coalesce(wki_quantity, 0)) <= $wrk_value_max)
-- and ($wrk_has_outstanding is null or ($wrk_has_outstanding and sum(coalesce(wki_price, 0) * coalesce(wki_quantity, 0)) > 0))
group by wrk.wrk_id;

-- set the outstanding... todo
update $result
set wrk_outstanding = (wrk_total - 0);

-- remove the results for has outstanding
if ($wrk_has_outstanding is not null and $wrk_has_outstanding = 1) then
	delete from $result
	where wrk_outstanding = 0;
end if;

if ($wrk_value_min is not null) then
	delete from $result
	where wrk_total < $wrk_value_min;
end if;

if ($wrk_value_max is not null) then
	delete from $result
	where wrk_total > $wrk_value_max;
end if;

-- select the remaining details
update $result res
inner join TM_WRK_Work wrk
on res.wrk_id = wrk.wrk_id
left join TM_CUS_customer cus
on wrk.cus_id = cus.cus_id
left join TR_USR_user usr
on usr.usr_id = wrk.usr_id_created
set res.cus_name = cus.cus_name,
	res.usr_name_created = usr.usr_name,
	res.wrk_date_create = wrk.wrk_date_create,
	res.wrk_date_due = wrk.wrk_date_due,
	res.wrk_stage = wrk.wrk_stage;

select * from $result;

drop table $result;

end //
delimiter ;
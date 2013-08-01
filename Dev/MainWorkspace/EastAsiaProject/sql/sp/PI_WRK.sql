drop procedure if exists PI_WRK;
delimiter //
create procedure PI_WRK(
$wrk_id int,
$cus_id int,
$com_id int,
$usr_id_created int,
$wrk_date_create date,
$wrk_date_due date,
$wrk_notes text,
$wrk_stage int
) begin

insert TM_WRK_work
(wrk_id, cus_id, com_id, usr_id_created, wrk_date_create, wrk_date_due, wrk_notes, wrk_stage)
values
(
$wrk_id,
$cus_id,
$com_id,
$usr_id_created,
$wrk_date_create,
$wrk_date_due,
$wrk_notes,
$wrk_stage
);

end //
delimiter ;
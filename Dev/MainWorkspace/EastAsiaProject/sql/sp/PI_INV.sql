drop procedure if exists PI_INV;
delimiter //
create procedure PI_INV(
$inv_id int,
$wrk_id int,
$inv_date_created date,
$inv_comments text,
$usr_id_create int,
$inv_stage int
) begin

insert TM_INV_invoice 
(inv_id, wrk_id, inv_date_created, inv_comments, usr_id_create, inv_stage)
values
($inv_id, $wrk_id, $inv_date_created, $inv_comments, $usr_id_create, $inv_stage);

end //
delimiter ;
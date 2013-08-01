drop procedure if exists PI_WKI;
delimiter //
create procedure PI_WKI(
$wki_id int,
$wrk_id int,
$prd_id int,
$wki_status int,
$wki_price double(16,2),
$wki_comments text,
$wki_quantity int
) begin

insert TM_WKI_work_item
(wki_id, wrk_id, prd_id, wki_status, wki_price, wki_comments, wki_quantity)
values
($wki_id, $wrk_id, $prd_id, $wki_status, $wki_price, $wki_comments, $wki_quantity);

end //
delimiter ;
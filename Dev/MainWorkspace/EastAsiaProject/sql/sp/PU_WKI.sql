drop procedure if exists PU_WKI;
delimiter //
create procedure PU_WKI(
$wki_id int,
$wrk_id int,
$prd_id int,
$wki_status int,
$wki_price double(16,2),
$wki_comments text,
$wki_quantity int
) begin

update TM_WKI_work_item
set wrk_id = $wrk_id, 
	prd_id = $prd_id, 
	wki_status = $wki_status, 
	wki_price = $wki_price, 
	wki_comments = $wki_comments,
	wki_quantity = $wki_quantity
where wki_id = $wki_id;

end //
delimiter ;
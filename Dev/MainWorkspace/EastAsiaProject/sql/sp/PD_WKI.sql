drop procedure if exists PD_WKI;
delimiter //
create procedure PD_WKI(
$wki_id int
) begin

delete from TM_WKI_work_item
where wki_id = $wki_id;

end //
delimiter ;
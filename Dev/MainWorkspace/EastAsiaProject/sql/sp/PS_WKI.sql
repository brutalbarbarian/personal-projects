drop procedure if exists PS_WKI;
delimiter //
create procedure PS_WKI(
$wki_id int
) begin

select wki.wki_id, wrk_id, prd_id, wki_status, wki_price, wki_comments, wki_quantity, 
wki_quantity - coalesce(sum(ini_quantity), 0) as wki_avaliable
from TM_WKI_work_item wki
left join TM_INI_invoice_item ini
on wki.wki_id = ini.wki_id
where wki.wki_id = $wki_id
group by (wki.wki_id);

end //
delimiter ;
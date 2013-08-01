drop procedure if exists PU_PRD;
delimiter //
create procedure PU_PRD(
$prd_id int,
$prd_name varchar(50),
$prd_description text,
$prd_price double(16,2),
$prd_comments text
) begin

update TR_PRD_product
set prd_name = $prd_name,
	prd_description = $prd_description,
	prd_price = $prd_price,
	prd_comments = $prd_comments
where prd_id = $prd_id;

end //
delimiter ;
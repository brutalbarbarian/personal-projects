drop procedure if exists PI_PRD;
delimiter //
create procedure PI_PRD(
$prd_id int,
$prd_name varchar(50),
$prd_description text,
$prd_price double(16,2),
$prd_comments text
) begin

insert TR_PRD_product
(prd_id, prd_name, prd_description, prd_price, prd_comments)
values
($prd_id, $prd_name, $prd_description, $prd_price, $prd_comments);

end //
delimiter ;
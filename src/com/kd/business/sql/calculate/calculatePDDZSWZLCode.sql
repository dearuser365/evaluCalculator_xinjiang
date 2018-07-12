--分子dmsdiss
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'dmsdiss' as code,
	organ.organ_code as organ_code, 
	decode(dmsdiss.value, NULL, 0, dmsdiss.value) as value,
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
LEFT JOIN (
			select a.organ_code,count(*) as value 
			from EVALUSYSTEM.detail.dmscb a,
  					EVALUSYSTEM.detail.pmscb b
  			where a.cb=b.cb 
  					and a.type=b.type 
  					and a.type=0 
  					and a.organ_code = b.organ_code 
  					group by a.organ_code
) dmsdiss
on organ.organ_code = dmsdiss.organ_code;
--分母pmsdiss
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'pmsdiss' as code,
	organ.organ_code as organ_code, 
	decode(pmsdiss.value, NULL, 0, pmsdiss.value) as value,
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
LEFT JOIN (
			select a.organ_code,count(*) as value
			from (
					select organ_code,cb 
					from EVALUSYSTEM.detail.dmscb 
					where type=0 
					union 
					select organ_code,cb 
					from EVALUSYSTEM.detail.pmscb 
					where type=0
				) a
  			group by a.organ_code
) pmsdiss
on organ.organ_code = pmsdiss.organ_code;
--分子dmsbreakers
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'dmsbreakers' as code,
	organ.organ_code as organ_code, 
	decode(dmsbreakers.value, NULL, 0, dmsbreakers.value) as value,
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
  					and a.type=1 
  					and a.organ_code = b.organ_code 
  					group by a.organ_code
) dmsbreakers
on organ.organ_code = dmsbreakers.organ_code;
--分母pmsbreakers
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'pmsbreakers' as code,
	organ.organ_code as organ_code, 
	decode(pmsbreakers.value, NULL, 0, pmsbreakers.value) as value,
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
					where type=1 
					union 
					select organ_code,cb 
					from EVALUSYSTEM.detail.pmscb 
					where type=1
				) a
  				group by a.organ_code
) pmsbreakers
on organ.organ_code = pmsbreakers.organ_code;
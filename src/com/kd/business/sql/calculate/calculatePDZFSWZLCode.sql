--分子dmssts
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'dmssts' as code,
	organ.organ_code as organ_code, 
	decode(dmssts.value, NULL, 0, dmssts.value) as value,
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
LEFT JOIN (
			select a.organ_code,count(*) as value
			from EVALUSYSTEM.detail.dmsst a,
  					EVALUSYSTEM.detail.pmsst b 
  			where a.devid=b.devid 
  				and a.organ_code=b.organ_code 
  				group by a.organ_code
) dmssts
on organ.organ_code = dmssts.organ_code;
--分母pmssts
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'pmssts' as code,
	organ.organ_code as organ_code, 
	decode(pmssts.value, NULL, 0, pmssts.value) as value,
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
LEFT JOIN (
			select organ_code,count(*) as value
			from (
					select organ_code,devid 
					from EVALUSYSTEM.detail.dmsst 
					union 
					select organ_code,devid 
					from EVALUSYSTEM.detail.pmsst
				) 
  			group by organ_code
) pmssts
on organ.organ_code = pmssts.organ_code;
--分子dmsbuss
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'dmsbuss' as code,
	organ.organ_code as organ_code, 
	decode(dmsbuss.value, NULL, 0, dmsbuss.value) as value,
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
LEFT JOIN (
			select a.organ_code,count(*) as value 
			from EVALUSYSTEM.detail.dmsbus a,
  					EVALUSYSTEM.detail.pmsbus b 
  			where a.devid=b.devid 
  					and a.organ_code=b.organ_code 
  					group by a.organ_code
) dmsbuss
on organ.organ_code = dmsbuss.organ_code;
--分母pmsbuss
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'pmsbuss' as code,
	organ.organ_code as organ_code, 
	decode(pmsbuss.value, NULL, 0, pmsbuss.value) as value,
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
					from EVALUSYSTEM.detail.dmsbus 
					union 
					select organ_code,devid 
					from EVALUSYSTEM.detail.pmsbus
				) 
  			group by organ_code
) pmsbuss
on organ.organ_code = pmsbuss.organ_code;
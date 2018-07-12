--分子dmslds
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'dmslds' as code,
	organ.organ_code as organ_code, 
	decode(dmslds.value, NULL, 0, dmslds.value) as value,
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
		LEFT JOIN (
					select a.organ_code,count(*) as value
					from EVALUSYSTEM.detail.dmsld a,
							EVALUSYSTEM.detail.pmsld b 
					where a.ld=b.ld 
		  				and a.organ_code = b.organ_code 
		  				group by a.organ_code
		) dmslds
		on organ.organ_code = dmslds.organ_code;
--分母pmslds
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'pmslds' as code,
	organ.organ_code as organ_code, 
	decode(pmslds.value, NULL, 0, pmslds.value) as value,
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
							select organ_code,ld 
							from EVALUSYSTEM.detail.dmsld 
							union 
							select organ_code,ld 
		  					from EVALUSYSTEM.detail.pmsld
		  				)a 
		  			group by a.organ_code
		) pmslds
		on organ.organ_code = pmslds.organ_code;
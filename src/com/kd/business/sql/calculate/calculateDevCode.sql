--插入设备平均完整率
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
--数值
select 
	'_devfine' as code,
	organ.organ_code organ_code, 
	case 
		when fz.num is null then 0
		when fm.num is null then 0
		when fm.num = 0 then 0
		else fz.num/fm.num * 100
	end as value,
	'数值' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
from EVALUSYSTEM.config.organ organ 
     left join
     	(select sum(value) num, organ_code 
     	from ${TABLENAME} 
     	where code in ('dmslds','dmsdiss','dmsbreakers','dmsbuss','dmssts')
     	group by organ_code) fz 
     on organ.organ_code = fz.organ_code
     left join
     	(select sum(value) num, organ_code 
     	from ${TABLENAME} 
     	where code in ('pmslds','pmsdiss','pmsbreakers','pmsbuss','pmssts')
     	group by organ_code) fm  
     on organ.organ_code = fm.organ_code
UNION ALL
--百分比
select 
	'_devfine' as code,
	organ.organ_code organ_code, 
	case 
		when fz.num is null then 0
		when fm.num is null then 0
		when fm.num = 0 then 0
		else fz.num/fm.num
	end as value,
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
from EVALUSYSTEM.config.organ organ 
     left join
     	(select sum(value) num, organ_code 
     	from ${TABLENAME} 
     	where code in ('dmslds','dmsdiss','dmsbreakers','dmsbuss','dmssts')
     	group by organ_code) fz 
     on organ.organ_code = fz.organ_code
     left join
     	(select sum(value) num, organ_code 
     	from ${TABLENAME} 
     	where code in ('pmslds','pmsdiss','pmsbreakers','pmsbuss','pmssts')
     	group by organ_code) fm  
     on organ.organ_code = fm.organ_code
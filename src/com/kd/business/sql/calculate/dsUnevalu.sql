---sql(0)分子/分母：地市免考核分母
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select
	'${fmCode}' as code,
	'${organ_code}' as organ_code,
	 FLOOR(avg(value)) as value,
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag 
from (
	select 
		*
	from EVALUSYSTEM.config.organ organ 
	LEFT JOIN (
		select * 
		from ${TABLENAME} 
		where code = '${fmCode}' 
		and statistime = '${dateStr}' 
		and dimname = '无' 
		and dimvalue = '无' 
		and flag = 0 
		and datatype = '个数'
	) ypTotalNum
	on organ.organ_code = ypTotalNum.organ_code
	where organ.parent_code is null and organ.organ_code not in (${organcodes})) ;
	
--sql(1)分子/分母：地市免考核分子
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select
	'${fzCode}' as code,
	'${organ_code}' as organ_code,
	FLOOR(min(value) * 0.9 * FLOOR(avg(fenmu))),
	'个数' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag 
from (
	select 
		*
	from EVALUSYSTEM.config.organ organ 
	LEFT JOIN (
		select * 
		from ${TABLENAME} 
		where code = '${code}' 
		and statistime = '${dateStr}' 
		and dimname = '无' 
		and dimvalue = '无' 
		and flag = 0 
		and datatype = '百分比'
	) ypTotalNum
	on organ.organ_code = ypTotalNum.organ_code
	where organ.parent_code is null and organ.organ_code not in (${organcodes})
	) a,
	(
	select 
		value as fenmu,organ.organ_code
	from EVALUSYSTEM.config.organ organ 
	LEFT JOIN (
		select * 
		from ${TABLENAME} 
		where code = '${fmCode}' 
		and statistime = '${dateStr}' 
		and dimname = '无' 
		and dimvalue = '无' 
		and flag = 0 
		and datatype = '个数'
	) ypTotalNum
	on organ.organ_code = ypTotalNum.organ_code
	where organ.parent_code is null and organ.organ_code not in (${organcodes})
	) b where a.organ_code = b.organ_code;
	
---sql(2)日指标平均值的情况：地市免考核
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select
	'${code}' as code,
	'${organ_code}' as organ_code,
	 (min(value) * 0.9) as value,
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag 
from (
	select 
		*
	from EVALUSYSTEM.config.organ organ 
	LEFT JOIN (
		select * 
		from ${TABLENAME} 
		where code = '${code}' 
		and statistime = '${dateStr}' 
		and dimname = '无' 
		and dimvalue = '无' 
		and flag = 0 
		and datatype = '百分比'
	) ypTotalNum
	on organ.organ_code = ypTotalNum.organ_code
	where organ.parent_code is null and organ.organ_code not in (${organcodes})) 
union all 
select
	'${code}' as code,
	'${organ_code}' as organ_code,
	 (min(value) * 0.9) as value,
	'数值' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag 
from (
	select 
		*
	from EVALUSYSTEM.config.organ organ 
	LEFT JOIN (
		select * 
		from ${TABLENAME} 
		where code = '${code}' 
		and statistime = '${dateStr}' 
		and dimname = '无' 
		and dimvalue = '无' 
		and flag = 0 
		and datatype = '数值'
	) ypTotalNum
	on organ.organ_code = ypTotalNum.organ_code
	where organ.parent_code is null and organ.organ_code not in (${organcodes}));

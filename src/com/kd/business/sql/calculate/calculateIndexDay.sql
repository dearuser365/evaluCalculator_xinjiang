---------------------------------------------
--分子/分母的情况0
---------------------------------------------
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fm.value,0,${defaultValue},null,${defaultValue},(100.0 * fz.value)/fm.value) as value,
	'数值' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code
union all
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fm.value,0,${defaultValue}*1.0/100,null,${defaultValue}*1.0/100,fz.value * 1.0/fm.value) as value,
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code;
---------------------------------------------
--(1 - 分子/分母)的情况1
---------------------------------------------
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fm.value,0,${defaultValue},null,${defaultValue},100.0 * (1 - (fz.value * 1.0/fm.value))) as value,
	'数值' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code
union all
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fm.value,0,${defaultValue}*1.0/100,null,${defaultValue}*1.0/100,(1 - (fz.value * 1.0/fm.value))) as value,
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code;
---------------------------------------------
--分子/分母不包含县公司，县公司直接给默认值2
---------------------------------------------
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fm.value,0,${defaultValue},null,${defaultValue},(100.0 * fz.value)/fm.value) as value,
	'数值' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code
	where organ.parent_code is null
union all
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fm.value,0,${defaultValue}*1.0/100,null,${defaultValue}*1.0/100,fz.value * 1.0/fm.value) as value,
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code
	where organ.parent_code is null
union all
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	${defaultXianValue} as value,
	'数值' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
from EVALUSYSTEM.config.organ organ 
where organ.parent_code is not null
union all
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	(${defaultXianValue}/100) as value,
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
from EVALUSYSTEM.config.organ organ 
where organ.parent_code is not null;
--(1 - 分子/分母)的情况2(分子为0)
---------------------------------------------
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fz.value,0,${defaultValue},null,${defaultValue},100.0 * (1 - (fz.value * 1.0/fm.value))) as value,
	'数值' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code
union all
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fz.value,0,${defaultValue}*1.0/100,null,${defaultValue}*1.0/100,(1 - (fz.value * 1.0/fm.value))) as value,
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code;
	---------------------------------------------
--分子/分母（指标值大于等于60分就给100分）的情况4
---------------------------------------------
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	case 
		when fm.value = 0 then ${defaultValue}
		when fm.value is null then ${defaultValue}
		when (100.0 * fz.value)/fm.value > 60 OR (100.0 * fz.value)/fm.value = 60 then 100
	else (100.0 * fz.value)/fm.value end as value,
	'数值' as datatype,	
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code
union all
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code,
	case 
		when fm.value = 0 then ${defaultValue}*1.0/100
		when fm.value is null then ${defaultValue}*1.0/100
		when fz.value * 1.0/fm.value > 0.6 OR fz.value * 1.0/fm.value  = 0.6 then 100*1.0/100
	else fz.value * 1.0/fm.value end as value,	
	'百分比' as datatype,
	sysdate as updateTime,
	'${dateStr}' as statistime,
	'无' as dimname,
	'无'  as dimvalue,
	'0' as flag
	from EVALUSYSTEM.config.organ organ 
	left join(
		select * from ${TABLENAME} where code = '${fmCode}' and dimname='无' and dimvalue='无'
	) fm on fm.organ_code = organ.organ_code
	left join(
		select * from ${TABLENAME} where code = '${fzCode}' and dimname='无' and dimvalue='无'
	) fz on fz.organ_code = organ.organ_code;
---------------------------------------------
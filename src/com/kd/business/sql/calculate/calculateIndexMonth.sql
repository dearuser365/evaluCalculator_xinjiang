---------------------------------------------
--分子/分母的情况
---------------------------------------------
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	decode(fm.value,0,${defaultValue},null,${defaultValue},fz.value*100/fm.value) as value,
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
--(1 - 分子/分母)的情况
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
--专属‘故障转供应用率’分子/分母的情况
---------------------------------------------
INSERT INTO ${TABLENAME}(CODE,ORGAN_CODE,VALUE,DATATYPE,UPDATETIME,STATISTIME,DIMNAME,DIMVALUE,FLAG)
select 
	'${indexCode}' as code,
	organ.organ_code as organ_code, 
	case when fm.value>0 and fz.value>5 then 100.0
    when fm.value>0 and fz.value is null then 0.0  
    when fm.value=0 then 0.0 
    when fm.value is null  then 0.0 
    else fz.value * 20.0 
    end as value,
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
	case when fm.value>0 and fz.value>5 then 1.0
    when fm.value>0 and fz.value is null then 0.0  
    when fm.value=0 then 0.0 
    when fm.value is null  then 0.0 
    else fz.value * 0.2 
    end as value,
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
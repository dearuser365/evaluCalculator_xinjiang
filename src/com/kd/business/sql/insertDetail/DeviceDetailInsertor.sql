--地区
INSERT INTO EVALUSYSTEM.RESULT.DEVICE_STATIS(ID,ORGAN_CODE,KG_COUNT,KG_AVG,PB_COUNT,PB_AVG,DZ_COUNT,DZ_AVG,MX_COUNT,MX_AVG,ZF_COUNT,ZF_AVG,GZ_COUNT,GZ_AVG,EY_COUNT,EY_AVG,SY_COUNT,SY_AVG,TE_COUNT,TE_AVG,LINE_NUM,COUNT_TIME,UPDATETIME)
select 
organ.organ_code || '00' as ID,
organ.organ_code as ORGAN_CODE,
(select count(*) as value from detail.pmscb where type='1' and organ_code like (organ.organ_code||'%')) as KG_COUNT,
(select avg(value) from ${TABLENAME} where code = 'breakfineAver' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as KG_AVG,
(select count(*) from detail.pmsld where organ_code like (organ.organ_code||'%')) as PB_COUNT,
(select avg(value) from ${TABLENAME} where code = 'ldfineAver' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as PB_AVG,
(select count(*) from detail.pmscb where  type='0' and organ_code like (organ.organ_code||'%')) as DZ_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'disfineAver' and dimname='无' and datatype='数值'and organ_code = organ.organ_code) as DZ_AVG,
(select count(*) from detail.pmsbus where organ_code like (organ.organ_code||'%')) as MX_COUNT,
(select avg(value) from ${TABLENAME} where code = 'busfineAver' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as MX_AVG,
(select count(*) from detail.pmsst where organ_code like (organ.organ_code||'%')) as ZF_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'substionfineAver'  and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as ZF_AVG,
(select sum(value) as value  from ${TABLENAME} where code='indicatornum' and dimname='无' and organ_code like (organ.organ_code||'%')) as GZ_COUNT,
(select avg(value) from ${TABLENAME} where code = 'incovlineAver' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as GZ_AVG,
(select sum(value) as value  from ${TABLENAME} where code='tworemotenum' and dimname='无' and organ_code like (organ.organ_code||'%')) as EY_COUNT,
(select avg(value) from ${TABLENAME} where code = 'twocovlineAver' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as EY_AVG,
(select sum(value) from ${TABLENAME} where code='threeremotenum' and dimname='无' and organ_code like (organ.organ_code||'%')) as SY_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'threecovlineAver' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as SY_AVG,
(select sum(value) as value from ${TABLENAME} where (code='indicatornum' or code = 'tworemotenum' or code = 'threeremotenum') and dimname='无' and organ_code like (organ.organ_code||'%')) as TE_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'tercovlineAver' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as TE_AVG,
(select sum(value) from ${TABLENAME}  where code = 'linenum' and dimname='无' and datatype='个数' and organ_code like (organ.organ_code||'%')) as LINE_NUM,
'${dateStr}' as COUNT_TIME,
sysdate as UPDATETIME
from evalusystem.config.organ organ where organ.parent_code is null;



--所有公司：
INSERT INTO EVALUSYSTEM.RESULT.DEVICE_STATIS(ID,ORGAN_CODE,KG_COUNT,KG_AVG,PB_COUNT,PB_AVG,DZ_COUNT,DZ_AVG,MX_COUNT,MX_AVG,ZF_COUNT,ZF_AVG,GZ_COUNT,GZ_AVG,EY_COUNT,EY_AVG,SY_COUNT,SY_AVG,TE_COUNT,TE_AVG,LINE_NUM,COUNT_TIME,UPDATETIME)
select 
organ.organ_code as ID,
organ.organ_code as ORGAN_CODE,
(select count(*) as value from detail.pmscb where type='1' and organ_code = organ.organ_code) as KG_COUNT,
(select avg(value) from ${TABLENAME} where code = 'breakfine' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as KG_AVG,
(select count(*) from detail.pmsld where organ_code = organ.organ_code) as PB_COUNT,
(select avg(value) from ${TABLENAME} where code = 'ldfine' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as PB_AVG,
(select count(*) from detail.pmscb where  type='0' and organ_code = organ.organ_code) as DZ_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'disfine' and dimname='无' and datatype='数值'and organ_code = organ.organ_code) as DZ_AVG,
(select count(*) from detail.pmsbus where organ_code = organ.organ_code) as MX_COUNT,
(select avg(value) from ${TABLENAME} where code = 'busfine' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as MX_AVG,
(select count(*) from detail.pmsst where organ_code = organ.organ_code) as ZF_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'substionfine'  and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as ZF_AVG,
(select sum(value) as value  from ${TABLENAME} where code='indicatornum' and dimname='无' and organ_code = organ.organ_code) as GZ_COUNT,
(select avg(value) from ${TABLENAME} where code = 'incovline' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as GZ_AVG,
(select sum(value) as value  from ${TABLENAME} where code='tworemotenum' and dimname='无' and organ_code = organ.organ_code) as EY_COUNT,
(select avg(value) from ${TABLENAME} where code = 'twocovline' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as EY_AVG,
(select sum(value) from ${TABLENAME} where code='threeremotenum' and dimname='无' and organ_code = organ.organ_code) as SY_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'threecovline' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as SY_AVG,
(select sum(value) as value from ${TABLENAME} where (code='indicatornum' or code = 'tworemotenum' or code = 'threeremotenum') and dimname='无' and organ_code = organ.organ_code) as TE_COUNT,
(select avg(value) from ${TABLENAME}  where code = 'tercovline' and dimname='无' and datatype='数值' and organ_code = organ.organ_code) as TE_AVG,
(select sum(value) from ${TABLENAME}  where code = 'linenum' and dimname='无' and datatype='个数' and organ_code = organ.organ_code) as LINE_NUM,
'${dateStr}' as COUNT_TIME,
sysdate as UPDATETIME
from evalusystem.config.organ organ;
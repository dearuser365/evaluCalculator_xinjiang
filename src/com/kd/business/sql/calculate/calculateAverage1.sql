----有县公司(organ.subtype=0) 
----指标值 averall.code_type(0)
----市0.95&县0.05 averall.flag(0) 
insert into ${TABLENAME}(organ_code,code,value,datatype,updatetime,statistime,dimname,dimvalue) 
	select a.organ_code,a.code+'Aver',round(a.v*0.95+b.v*0.05,4),a.d,sysdate,'${dateStr}','无','无' 
	from 
		(
			select organ.code,organ.organ_code organ_code,decode(a.value, NULL, 0, a.value) v,decode(a.datatype, NULL, '百分比', a.datatype) d 
				from 
					(select * from ${TABLENAME} where datatype in('百分比', '数值') and dimname = '无') a 
					right join
					(
						select b.organ_code, a.code 
						from
						(select * from EVALUSYSTEM.config.averall where flag = 0 and code_type = 0) a,
						(select * from EVALUSYSTEM.config.organ where flag = 1 and subtype = 0) b
					)organ 
					on (a.organ_code = organ.organ_code and a.code = organ.code)
		) a, 
		(
			select organ.code,organ.organ_code,decode(b.v,NULL,0,b.v) v ,decode(b.d,NULL,'百分比',b.d) d 
				from 
					(
						select a.code code,substr(a.organ_code, 0, 4) organ_code,round(avg(a.value), 4) v,a.datatype d 
							from
								(select * from ${TABLENAME} where datatype in('百分比', '数值') and dimname = '无') a, 
								(select * from EVALUSYSTEM.config.averall where flag = 0 and code_type = 0) b, 
								(select * from EVALUSYSTEM.config.organ where flag = 0) c 
							where a.code = b.code and a.organ_code = c.organ_code 
							group by substr(a.organ_code, 0, 4),a.code,a.datatype
					) b 
					right join
					(
						select b.organ_code, a.code 
							from
								(select * from EVALUSYSTEM.config.averall where flag = 0 and code_type = 0) a, 
								(select * from EVALUSYSTEM.config.organ where flag = 1 and subtype = 0) b
					) organ 
					on(b.organ_code = organ.organ_code and organ.code = b.code)
		) b 
	where a.code = b.code and a.organ_code = b.organ_code and a.d = b.d;
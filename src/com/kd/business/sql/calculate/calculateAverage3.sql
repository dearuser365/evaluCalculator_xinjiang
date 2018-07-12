----没有县公司(organ.subtype=1) 
----指标值 (averall.code_type=0)
----市0.95&县0.05 (averall.flag=0) 
insert into ${TABLENAME}(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue) 
	select b.code+'Aver',b.organ_code,decode(a.value,NULL,0,a.value),decode(a.datatype,NULL,'百分比',a.datatype), sysdate, '${dateStr}', '无', '无' 
	from 
		(select * from ${TABLENAME} where datatype in('百分比', '数值') and dimname = '无') a 
		right join 
		(
			select b.organ_code, a.code 
			from
				(select * from EVALUSYSTEM.config.averall where flag = 0 and code_type = 0) a, 
				(select * from EVALUSYSTEM.config.organ where flag = 1 and subtype = 1) b
		) b 
		on(a.code = b.code and a.organ_code = b.organ_code) ;
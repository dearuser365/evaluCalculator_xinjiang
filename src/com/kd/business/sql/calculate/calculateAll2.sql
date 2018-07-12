----总数 (averall.code_type=1)
----地区=本部(averall.flag=1) 
insert into ${TABLENAME}(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue) 
	select a.code+'All',a.organ_code,sum(a.value),a.datatype,sysdate,'${dateStr}','无','无' 
	from 
		(select * from ${TABLENAME} where datatype in('个数','小时') and dimname = '无' ) a,
		(select * from EVALUSYSTEM.config.averall where flag = 1 and code_type = 1) b 
	where a.code = b.code 
	group by a.organ_code,a.code,a.datatype;
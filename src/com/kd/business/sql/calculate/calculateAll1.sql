----总数 (averall.code_type=1)
----地区=本部加县公司和(averall.flag=0) 
insert into ${TABLENAME}(code,organ_code,value,datatype,updatetime,statistime,dimname,dimvalue) 
	select a.code+'All',substr(a.organ_code,0,5),sum(a.value),a.datatype,sysdate,'${dateStr}','无','无' 
	from
		(select * from ${TABLENAME} where datatype in('个数','小时') and dimname = '无' ) a,
		(select * from EVALUSYSTEM.config.averall where flag = 0 and code_type = 1) b 
	where a.code = b.code 
	group by substr(a.organ_code,0,5),a.code,a.datatype;
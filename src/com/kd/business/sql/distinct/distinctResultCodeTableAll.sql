select * from 
(
	select code, organCode,datatype, dimvalue, dimname, maxUpdateTime, num from (
		select 
			code, 
			organ_code as organCode,
			datatype,
			dimvalue,
			dimname,
			max(updatetime) as maxUpdateTime, 
			count(*) as num
		from ${TABLENAME} group by code, organ_code, datatype, dimvalue, dimname

	) h2 where h2.num > 1
) rowTemp
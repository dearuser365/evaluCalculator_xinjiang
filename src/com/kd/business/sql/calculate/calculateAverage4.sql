----指标值(averall.code_type=0)
----地区=地区的分子CODE的和 除以 地区的分母CODE的和(averall.flag=2) 
insert into ${TABLENAME}(organ_code,code,value,datatype,updatetime,statistime,dimname,dimvalue) 
select organCode, (code || 'Aver'), decode(fenmu,0,1,null,1,fenzi/fenmu) as value, '百分比', sysdate,'${dateStr}','无','无' from (
	select organCode, div, divend,code,
		(
			select 
				sum(value)
			from ${TABLENAME} codeTable 
			where code = div
			and organ_code like (organCode || '%')
		) as fenzi,
		(
			select 
				sum(value)
			from ${TABLENAME} codeTable 
			where code = divend
			and organ_code like (organCode || '%')
		) as fenmu
	 from (
		select 
		organ.organ_code as organCode, 
		aver.code as code,
		aver.div as div,
		aver.divend as divend
			from EVALUSYSTEM.config.organ organ 
			left join "EVALUSYSTEM"."CONFIG"."AVERALL" aver
				on aver.flag='2'
				and aver.div is not null
				and aver.divend is not null
			where organ.parent_code is null
	)
)
union all
select organCode, (code || 'Aver'), decode(fenmu,0,100,null,100,fenzi*100/fenmu) as value, '数值', sysdate,'${dateStr}','无','无' from (
	select organCode, div, divend,code,
		(
			select 
				sum(value)
			from ${TABLENAME} codeTable 
			where code = div
			and organ_code like (organCode || '%')
		) as fenzi,
		(
			select 
				sum(value)
			from ${TABLENAME} codeTable 
			where code = divend
			and organ_code like (organCode || '%')
		) as fenmu
	 from (
		select 
		organ.organ_code as organCode, 
		aver.code as code,
		aver.div as div,
		aver.divend as divend
			from EVALUSYSTEM.config.organ organ 
			left join "EVALUSYSTEM"."CONFIG"."AVERALL" aver
				on aver.flag='2'
				and aver.code_type=0
				and aver.div is not null
				and aver.divend is not null
			where organ.parent_code is null
	)
)

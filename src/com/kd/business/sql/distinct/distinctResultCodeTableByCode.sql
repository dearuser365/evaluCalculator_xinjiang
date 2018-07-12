delete from 
	${TABLENAME} a
where a.code='${CODE}' 
and a.pos != (
	select max(pos) from ${TABLENAME} b
	where  a.code=b.code 
	and a.organ_code=b.organ_code 
	and a.datatype=b.datatype 
	and a.dimvalue=b.dimvalue
	and a.dimname=b.dimname
)
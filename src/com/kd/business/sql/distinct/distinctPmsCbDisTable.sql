delete from 
	EVALUSYSTEM.detail.PMSCB a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.PMSCB b
	where  a.organ_code = b.organ_code and a.cb = b.cb and a.type = b.type
)
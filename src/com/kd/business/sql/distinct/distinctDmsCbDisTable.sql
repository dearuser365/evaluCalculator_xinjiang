delete from 
	EVALUSYSTEM.detail.dmscb a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.dmscb b
	where  a.organ_code = b.organ_code and a.cb = b.cb and a.type = b.type
)
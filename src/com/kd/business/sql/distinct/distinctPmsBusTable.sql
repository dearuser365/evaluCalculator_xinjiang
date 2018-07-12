delete from 
	EVALUSYSTEM.detail.PMSBUS a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.PMSBUS b
	where  a.organ_code = b.organ_code and a.devid = b.devid
)
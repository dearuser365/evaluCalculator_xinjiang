delete from 
	EVALUSYSTEM.detail.PMSST a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.PMSST b
	where  a.organ_code = b.organ_code and a.devid = b.devid
)

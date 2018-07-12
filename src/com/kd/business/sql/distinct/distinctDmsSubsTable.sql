delete from 
	EVALUSYSTEM.detail.DMSST a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.DMSST b
	where  a.organ_code = b.organ_code and a.devid = b.devid
)

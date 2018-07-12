delete from 
	EVALUSYSTEM.detail.DMSBUS a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.DMSBUS b
	where  a.organ_code = b.organ_code and a.devid = b.devid
)
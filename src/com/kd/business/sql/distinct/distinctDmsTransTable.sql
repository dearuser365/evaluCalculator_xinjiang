delete from 
	EVALUSYSTEM.detail.DMSLD a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.DMSLD b
	where  a.organ_code = b.organ_code and a.ld = b.ld
)
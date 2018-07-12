delete from 
	EVALUSYSTEM.detail.PMSLD a 
where pos != (
	select max(pos) from EVALUSYSTEM.detail.PMSLD b
	where  a.organ_code = b.organ_code and a.ld = b.ld
)
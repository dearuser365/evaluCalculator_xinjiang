-----------------------------------
--插入市得分日
-----------------------------------
insert into ${TABLENAME}(code,organ_code,value,datatype, statistime,updatetime,dimname,dimvalue) 
        (
        select 
                'score',
                a.organ_code,
                round(sum(a.value*b.weight)/100,4),
                '得分', 
                '${dateStr}',
                sysdate,
                '无',
                '无' 
        from (select 
        		case when code ='zlpRight' and value >= 60 then 100
        		else value 
        		end as value ,organ_code,code,datatype,dimvalue,dimname
        	  from ${TABLENAME} a where updatetime=
                        (
                        select 
                                max(updatetime) 
                        from 
                                ${TABLENAME} b 
                        where 
                                a.code=b.code 
                            and a.organ_code      =b.organ_code 
                            and a.datatype  =b.datatype 
                            and a.dimname   =b.dimname 
                            and a.dimvalue  =b.dimvalue 
                            and datatype    = '数值' 
                            and code       not in ( 'score','_ypPublish','devD16G16','topogoodup','_ztgj')
                        )
                ) a,
                (select * from EVALUSYSTEM.config.weight_new a where '${dateStr2}' between startTime and endTime) 
                b,(select sum(weight) s from (select * from EVALUSYSTEM.config.weight_new a where '${dateStr2}' between startTime and endTime)) 
                c,(select organ_code from EVALUSYSTEM.config.organ where flag = 1) 
                d 
        where 
                a.code = b.code 
            and datatype   = '数值' 
            and dimname  = '无' 
            and a.code  not in ( 'score','_ypPublish','devD16G16','topogoodup','_ztgj') 
            and d.organ_code                 = a.organ_code 
        group by 
                a.organ_code
        );
-----------------------------------
--插入县得分日
-----------------------------------
insert into ${TABLENAME}(code,organ_code,value,datatype, statistime,updatetime,dimname,dimvalue) 
        (
        select 
                'score',
                a.organ_code,
                round(sum(a.value*b.weight)/100,4),
                '得分', 
                '${dateStr}',
                sysdate,
                '无',
                '无' 
        from (select 
                case when code ='zlpRight' and value >= 60 then 100
        		else value 
        		end as value ,organ_code,code,datatype,dimvalue,dimname 
        	  from ${TABLENAME} a where updatetime=
                        (
                        select 
                                max(updatetime) 
                        from 
                                ${TABLENAME} b 
                        where 
                                a.code=b.code 
                            and a.organ_code      =b.organ_code 
                            and a.datatype  =b.datatype 
                            and a.dimname   =b.dimname 
                            and a.dimvalue  =b.dimvalue 
                            and datatype    = '数值' 
                            and code       not in ( 'score','_ypPublish','devD16G16','topogoodup','_ztgj')
                        )) 
                a,
                (
                select 
                        * 
                from 
                        EVALUSYSTEM.config.weight_new a 
                where 
                        '${dateStr2}' between starttime and endtime  
                    and caflag = 0
                ) 
                b,(select sum(weight) s from (select * from EVALUSYSTEM.config.weight_new a where '${dateStr2}' between starttime and endtime )) 
                c,(select organ_code from EVALUSYSTEM.config.organ where flag = 0) 
                d 
        where 
                a.code = b.code 
            and datatype   = '数值' 
            and dimname  = '无' 
            and a.code  not in ( 'score','_ypPublish','devD16G16','topogoodup','_ztgj') 
            and d.organ_code                 = a.organ_code 
        group by 
                a.organ_code
        );
package com.kd.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kd.dao.SystemDao;
import com.kd.service.SystemService;

@Service("systemService")
@Scope("prototype")
@Transactional
public class SystemServiceImpl implements SystemService {

	private SystemDao systemDao;

	@Resource
	public void setCommonDao(SystemDao systemDao) {
		this.systemDao = systemDao;
	}

	public String getOneValue(String sql, Object... objs) {
		List<Map<String, Object>> getSqlData = systemDao.findForJdbc(sql, objs);
		if (getSqlData == null || getSqlData.size() == 0) {
			return "";
		} else {
			Map<String, Object> map = getSqlData.get(0);
			try {
				Iterator<String> it = map.keySet().iterator();
				if (it.hasNext()) {
					String key = it.next();
					// String value = (String) map.get(key);
					Object value = map.get(key);
					String realValue = "";
					if (value != null) {
						realValue = value.toString();
					} else {
						return "";
					}
					return realValue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}
		return "";
	}

	public Integer getAllDbTableSize() {
		return systemDao.getAllDbTableSize();
	}

	public <T> Serializable save(T entity) {
		return systemDao.save(entity);
	}

	public <T> void saveOrUpdate(T entity) {
		systemDao.saveOrUpdate(entity);

	}

	public <T> void delete(T entity) {
		systemDao.delete(entity);

	}

	/**
	 * 删除实体集合
	 * 
	 * @param <T>
	 * @param entities
	 */
	public <T> void deleteAllEntitie(Collection<T> entities) {
		systemDao.deleteAllEntitie(entities);
	}

	/**
	 * 根据实体名获取对象
	 */
	public <T> T get(Class<T> class1, Serializable id) {
		return systemDao.get(class1, id);
	}

	/**
	 * 根据实体名返回全部对象
	 * 
	 * @param <T>
	 * @param hql
	 * @param size
	 * @return
	 */
	public <T> List<T> getList(Class clas) {
		return systemDao.loadAll(clas);
	}

	/**
	 * 根据实体名获取对象
	 */
	public <T> T getEntity(Class entityName, Serializable id) {
		return systemDao.getEntity(entityName, id);
	}

	/**
	 * 根据实体名称和字段名称和字段值获取唯一记录
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param propertyName
	 * @param value
	 * @return
	 */
	public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value) {
		return systemDao.findUniqueByProperty(entityClass, propertyName, value);
	}

	/**
	 * 按属性查找对象列表.
	 */
	public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value) {

		return systemDao.findByProperty(entityClass, propertyName, value);
	}

	/**
	 * 加载全部实体
	 * 
	 * @param <T>
	 * @param entityClass
	 * @return
	 */
	public <T> List<T> loadAll(final Class<T> entityClass) {
		return systemDao.loadAll(entityClass);
	}

	/**
	 * 删除实体主键ID删除对象
	 * 
	 * @param <T>
	 * @param entities
	 */
	public <T> void deleteEntityById(Class entityName, Serializable id) {
		systemDao.deleteEntityById(entityName, id);
	}

	/**
	 * 更新指定的实体
	 * 
	 * @param <T>
	 * @param pojo
	 */
	public <T> void updateEntitie(T pojo) {
		systemDao.updateEntitie(pojo);

	}

	/**
	 * 通过hql 查询语句查找对象
	 * 
	 * @param <T>
	 * @param query
	 * @return
	 */
	public <T> List<T> findByQueryString(String hql) {
		return systemDao.findByQueryString(hql);
	}

	/**
	 * 根据sql更新
	 * 
	 * @param query
	 * @return
	 */
	public int updateBySqlString(String sql) {
		return systemDao.updateBySqlString(sql);
	}

	/**
	 * 根据sql查找List
	 * 
	 * @param <T>
	 * @param query
	 * @return
	 */
	public <T> List<T> findListbySql(String query) {
		return systemDao.findListbySql(query);
	}

	/**
	 * 通过属性称获取实体带排序
	 * 
	 * @param <T>
	 * @param clas
	 * @return
	 */
	public <T> List<T> findByPropertyisOrder(Class<T> entityClass, String propertyName, Object value, boolean isAsc) {
		return systemDao.findByPropertyisOrder(entityClass, propertyName, value, isAsc);
	}


	public Session getSession()

	{
		return systemDao.getSession();
	}

	public List findByExample(final String entityName, final Object exampleEntity) {
		return systemDao.findByExample(entityName, exampleEntity);
	}

	public void executeSqls(String[] sqls) {
		systemDao.executeSqls(sqls);
	}
	public Integer executeSql(String sql, List<Object> param) {
		return systemDao.executeSql(sql, param);
	}

	public Integer executeSql(String sql, Object... param) {
		return systemDao.executeSql(sql, param);
	}

	public Integer executeSql(String sql, Map<String, Object> param) {
		return systemDao.executeSql(sql, param);
	}

	public List<Map<String, Object>> findForJdbc(String sql, int page, int rows) {
		return systemDao.findForJdbc(sql, page, rows);
	}

	public List<Map<String, Object>> findForJdbc(String sql, Object... objs) {
		return systemDao.findForJdbc(sql, objs);
	}


	public Map<String, Object> findOneForJdbc(String sql, Object... objs) {
		return systemDao.findOneForJdbc(sql, objs);
	}

	public Long getCountForJdbc(String sql) {
		return systemDao.getCountForJdbc(sql);
	}

	public Long getCountForJdbcParam(String sql, Object[] objs) {
		return systemDao.getCountForJdbcParam(sql, objs);
	}

	public <T> void batchSave(List<T> entitys) {
		this.systemDao.batchSave(entitys);
	}

	/**
	 * 通过hql 查询语句查找对象
	 * 
	 * @param <T>
	 * @param query
	 * @return
	 */
	public <T> List<T> findHql(String hql, Object... param) {
		return this.systemDao.findHql(hql, param);
	}

	public <T> List<T> pageList(DetachedCriteria dc, int firstResult, int maxResult) {
		return this.systemDao.pageList(dc, firstResult, maxResult);
	}

	public <T> List<T> findByDetached(DetachedCriteria dc) {
		return this.systemDao.findByDetached(dc);
	}

	public int[] pushSqls(String [] sqls){
		 return systemDao.pushSqls(sqls);
	 }
}

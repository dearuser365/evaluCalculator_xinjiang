package com.kd.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;

public interface SystemService {
	public String getOneValue(String sql, Object... objs);
    /**
     * 获取所有数据库表
     * @return
     */
    public Integer getAllDbTableSize();

    public <T> Serializable save(T entity);

    public <T> void saveOrUpdate(T entity);

    public <T> void delete(T entity);

    public <T> void batchSave(List<T> entitys);

    /**
     * 根据实体名称和主键获取实体
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T get(Class<T> class1, Serializable id);

    /**
     * 根据实体名称和主键获取实体
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T getEntity(Class entityName, Serializable id);

    /**
     * 根据实体名称和字段名称和字段值获取唯一记录
     * @param <T>
     * @param entityClass
     * @param propertyName
     * @param value
     * @return
     */
    public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value);

    /**
     * 按属性查找对象列表.
     */
    public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value);

    /**
     * 加载全部实体
     * @param <T>
     * @param entityClass
     * @return
     */
    public <T> List<T> loadAll(final Class<T> entityClass);

    /**
     * 删除实体主键删除
     * @param <T>
     * @param entities
     */
    public <T> void deleteEntityById(Class entityName, Serializable id);

    /**
     * 删除实体集合
     * @param <T>
     * @param entities
     */
    public <T> void deleteAllEntitie(Collection<T> entities);

    /**
     * 更新指定的实体
     * @param <T>
     * @param pojo
     */
    public <T> void updateEntitie(T pojo);

    /**
     * 通过hql 查询语句查找对象
     * @param <T>
     * @param query
     * @return
     */
    public <T> List<T> findByQueryString(String hql);

    /**
     * 根据sql更新
     * @param query
     * @return
     */
    public int updateBySqlString(String sql);

    /**
     * 根据sql查找List
     * @param <T>
     * @param query
     * @return
     */
    public <T> List<T> findListbySql(String query);

    /**
     * 通过属性称获取实体带排序
     * @param <T>
     * @param clas
     * @return
     */
    public <T> List<T> findByPropertyisOrder(Class<T> entityClass, String propertyName, Object value, boolean isAsc);

    public <T> List<T> getList(Class clas);

    public Session getSession();

    public List findByExample(final String entityName, final Object exampleEntity);

    /**
     * 执行SQL
     */
    public Integer executeSql(String sql, List<Object> param);
    /**
     * 批量执行SQL
     */
    public void executeSqls(String[] sqls);

    /**
     * 执行SQL
     */
    public Integer executeSql(String sql, Object... param);

    /**
     * 执行SQL 使用:name占位符
     */
    public Integer executeSql(String sql, Map<String, Object> param);

    /**
     * 通过JDBC查找对象集合 使用指定的检索标准检索数据返回数据
     */
    public List<Map<String, Object>> findForJdbc(String sql, Object... objs);

    /**
     * 通过JDBC查找对象集合 使用指定的检索标准检索数据返回数据
     */
    public Map<String, Object> findOneForJdbc(String sql, Object... objs);

    /**
     * 使用指定的检索标准检索数据并分页返回数据For JDBC
     */
    public Long getCountForJdbc(String sql);

    /**
     * 使用指定的检索标准检索数据并分页返回数据For JDBC-采用预处理方式
     */
    public Long getCountForJdbcParam(String sql, Object[] objs);

    /**
     * 通过hql 查询语句查找对象
     * @param <T>
     * @param query
     * @return
     */
    public <T> List<T> findHql(String hql, Object... param);

    public <T> List<T> pageList(DetachedCriteria dc, int firstResult, int maxResult);

    public <T> List<T> findByDetached(DetachedCriteria dc);
    
    public int[] pushSqls(String [] sqls);
	
}

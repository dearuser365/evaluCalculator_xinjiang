package com.kd.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaQuery;

import org.apache.catalina.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;

import com.kd.exception.BusinessException;

public interface SystemDao {

    public Integer getAllDbTableSize();

    public <T> Serializable save(T entity);

    public <T> void batchSave(List<T> entitys);

    public <T> void saveOrUpdate(T entity);

    /**
     * 删除实体
     * @param <T>
     * @param <T>
     * @param <T>
     * @param entitie
     */
    public <T> void delete(T entitie);

    /**
     * 根据实体名称和主键获取实体
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T get(Class<T> entityName, Serializable id);

    /**
     * 根据实体名字获取唯一记录
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
     * 根据实体名称和主键获取实体
     * @param <T>
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T getEntity(Class entityName, Serializable id);

    /**
     * {根据实体名称和主键获取实体删除记录}
     * @param entityName
     * @param id
     * @author: zhursh
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

    public <T> void updateEntityById(Class entityName, Serializable id);

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

    public org.hibernate.Session getSession();

    public List findByExample(final String entityName, final Object exampleEntity);

    /**
     * 通过hql 查询语句查找HashMap对象
     * @param <T>
     * @param query
     * @return
     */
    public Map<Object, Object> getHashMapbyQuery(String query);

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

    /**
     * 执行HQL语句操作更新
     * @param hql
     * @return
     */
    public Integer executeHql(String hql);

    public <T> List<T> pageList(DetachedCriteria dc, int firstResult, int maxResult);

    public <T> List<T> findByDetached(DetachedCriteria dc);
    
    public int[] pushSqls(String [] sqls);

}

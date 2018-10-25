package net.coahr.three3.three.Util.JDBC;

import android.content.ContentValues;
import android.database.Cursor;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 李浩
 * 2018/5/7
 * 数据库操作工具类
 */
public class DataBaseWork {

    /*======================================添加===================================================*/

    /**
     *    批量添加
     * @param list
     *          集合
     * @param <T>
     */
    public static <T extends DataSupport> void   DBSaveAll(List<T> list){
          DataSupport.saveAll(list);
    }


    /*======================================删除==================================*/

    /**
     * 根据Id单个删除
     *
     * @param modelClass
     *                      表名
     * @param id
     *                      删除的id
     * @param <T>
     *                      泛型
     * @return
     *                      删除个数
     */
    public static <T  extends DataSupport> int DBDeleteById(Class<T> modelClass, long id) {
        int i = DataSupport.delete(modelClass, id);
        return i;
    }

    /**
     * 根据条件删除
     *
     * @param modelClass
     *                  表名
     * @param conditions
     *                  条件：例如["name=? and age=?","张三"，"20"]
     * @param <T>
     * @return
     *                  删除的个数
     */
    public static <T extends DataSupport> int DBDeleteByConditions(Class<T> modelClass, String... conditions) {

        int i = DataSupport.deleteAll(modelClass, conditions);

        return i;
    }

    /**
     *  清空数据库
     * @param modelClass
     * @param <T>
     * @return
     *      删除个数
     */
    public static <T extends DataSupport> int DBDeleteByAll(Class<T> modelClass) {
        int i = DataSupport.deleteAll(modelClass);
        return i;
    }

    /*====================================修改=================================================*/

    /**
     * 创建ContentValues根据Id修改
     * 例如：
     * ContentValues values = new ContentValues();
     *  values.put("字段名", "值");
     *  DataSupport.update(表名, values, id);
     * @param modelClass
     *          表名
     * @param values
     *          要修改的列表和值
     * @param id
     *           要修改的ID
     * @param <T>
     * @return
     */
    public static <T extends DataSupport> int DBUpdateById(Class<T> modelClass, ContentValues values, Long id) {

        int i = DataSupport.update(modelClass, values, id);
        return i;
    }

    /**
     * 创建ContentValues条件修改
     * 例如：
     * ContentValues values = new ContentValues();
     * values.put("字段名1", "值1");
     *  DataSupport.updateAll((表名, values, "字段名1= ?", "值");
     *  或者多条件
     *  DataSupport.updateAll((表名, values, "字段名1= ?and 字段名2=？", "值1","值2");
     * @param modelClass
     *              表名
     * @param values
     *              要修改的列表和值
     * @param conditions
     * @param <T>
     * @return
     */
    public static <T extends DataSupport> int DBUpdateByConditions(Class<T> modelClass, ContentValues values,String... conditions){
        int i = DataSupport.updateAll(modelClass, values, conditions);
        return i;
    }

    /**
     * 把表中的某一字段全部修改为
     * @param modelClass
     *              表名
     * @param values
     *            ContentValues
     * @param <T>
     *            泛型
     * @return
     *           返回修改的个数
     */
    public static <T extends DataSupport> int DBUpdateAll(Class<T> modelClass, ContentValues values){
        int i = DataSupport.updateAll(modelClass, values);
        return i;
    }
/*=====================================查询==============================================*/

    /**
     * 根据Id单条查询
     * @param modelClass
     *                  表名
     * @param id
     *                  查询的ID
     * @param <T>
     *                  泛型
     * @return
     *                  返回结果
     */
        public static <T extends DataSupport> T DBSelectById(Class<T> modelClass,long id){
            T t = DataSupport.find(modelClass, id);
            return t;
        }


    /**
     * 查询表中第一条数据
     * @param modelClass
     *              表名
     * @param <T>
     *            泛型
     * @return
     *          对应对象
     */
    public static <T extends DataSupport> T DBSelectByFirst(Class<T> modelClass){
            T first = DataSupport.findFirst(modelClass);
            return first;
        }

    /**
     * 查询表中最后一条数据
      * @param modelClass
     *              表名
     * @param <T>
     *              泛型
     * @return
     *              返回结果
     */
    public static <T extends DataSupport> T DBSelectByLast(Class<T> modelClass){
            T last = DataSupport.findLast(modelClass);
            return last;
     }

    /**
     * 根据id多条数据查询
     * sql语句：select * from 表名 where id=?,?,?...;
     * @param modelClass
     *          表名
     * @param ids
     *          ID数组
     * @param <T>
     *          泛型
     * @return
     *          返回结果
     */
     public static <T extends DataSupport> List<T> DBSelectByIds(Class<T> modelClass, List<Integer> ids){
         List<T> lists=new ArrayList<T>();
         for (int i = 0; i < ids.size(); i++) {
             T t = DataSupport.find(modelClass, ids.get(i));
             lists.add(t);
         }
         return lists;
     }

    public static <T extends DataSupport> List DBSelectByColumnName(Class<T> modelClass,String... ColumuName){
        List<T> ts =  DataSupport.select(ColumuName).find(modelClass);
        return ts;
    }

    /**条件语句全查询
     * 例如
     * select * from 表名 where 条件语句;
     * @param modelClass
     *                 表名
     * @param where
     *
     * @param <T>
     * @return
     */
    public static <T extends DataSupport> List<T> DBSelectByTogether_Where(Class<T> modelClass,String... where){
        List<T> ts = DataSupport.where(where).find(modelClass);
        return ts;
    }

    /**字段和条件语句查询
     * 例如
     * select 字段1,字段2 from 表名 where 条件;
     * @param modelClass
     *              表名
     * @param column_name
     *              要查询字段名
     * @param where
     *              where条件句
     * @param <T>
     *               泛型
     * @return
     *              对应结果
     */
    public static <T extends DataSupport> List<T> DBSelectBy_Where(Class<T> modelClass,String [] column_name,String... where){
        List<T> ts = DataSupport.select(column_name).where(where).find(modelClass);
        return ts;
    }

    /**
     * 查询表中所有数据
     * sql语句：select * from  表名
     * @param modelClass
     *                  表名（类）
     * @param <T>
     *                  泛型
     * @return
     *                  对应结果
     */
    public static <T extends DataSupport> List<T> DBSelectAll(Class<T> modelClass){
        List<T> all = DataSupport.findAll(modelClass);
        return all;
    }




    /**
     *order多条件查询
     * 例如
     * DataSupport.select(String [] 字段名)
     *.where(String[] 条件）
     *.find(表名);
     * sql语句：select 字段名 from 表名 where  条件 order by 排序字段+排序方式;
     * @param modelClass
     *              表名
     * @param where
     *              where条件
     * @param column_name
     *              要查询字段名
     *@param  order
     *             字段+desc/asc(正序或倒序)
     * @param <T>
     *              泛型
     * @return
     *              对应结果
     */
     public static <T extends  DataSupport> List<T> DBSelectBy_order(Class<T> modelClass,String[] where ,String order,String... column_name){
         List<T> ts = DataSupport.select(column_name)
                 .where(where)
                 .order(order)
                 .find(modelClass);
         return ts;
     }

    /**
     * order多条件全查询
     *
     * DataSupport.where(String[] 条件）.order(字段名).find(表名);
     *
     * sql语句：select * from 表名 where  条件 order by 条件 desc/asc;
     * @param modelClass
     *          表名
     * @param where
     *           条件
     * @param order
     *           字段+desc/asc(正序或倒序)
     * @param <T>
     * @return
     */
    public static <T extends  DataSupport> List<T> DBSelectByTogether_order(Class<T> modelClass,String[] where ,String order){
        List<T> ts = DataSupport
                .where(where)
                .order(order)
                .find(modelClass);
        return ts;
    }

    /**
     * limit多条件全查询
     * DataSupport.where(条件).order (排序字段+排序方式).limit(界限).find(表名);
     * sql语句：select * from 表名 where 查询条件 order by 排序条件 limit 界限
     * @param modelClass
     *              表名
     * @param order
     *               字段+desc/asc(正序或倒序)
     * @param limit
     *               界限0 - limit
     * @param where
     *                查询条件
     * @param <T>
     *                泛型
     * @return
     *                返回结果
     */
    public static <T extends  DataSupport> List<T> DBSelectByTogether_limit(Class<T> modelClass,String order,int  limit,String... where){
        List<T> ts = DataSupport
                .where(where)
                .order(order).limit(limit)
                .find(modelClass);
        return ts;
    }

    /**
     * limit 多条件查询
     * DataSupport.select（查询字段）.where(查询条件).order (排序字段+排序方式).limit(界限).find(表名);
     * limit多条件全查询
     * sql语句：select 查询字段 from 表名 where 查询条件 order by 排序条件 limit 界限
     * @param modelClass
     *              表名
     * @param order
     *               字段+desc/asc(正序或倒序)
     * @param limit
     *               界限0 - limit
     * @param where
     *                查询条件
     * @param column_name
     *                 查询字段
     * @param <T>
     *                泛型
     * @return
     *                返回结果
     */
    public static <T extends  DataSupport> List<T> DBSelectBy_limit(Class<T> modelClass,String [] where,String order,int  limit,String... column_name ){
        List<T> ts = DataSupport.select(column_name)
                .where(where)
                .order(order).limit(limit)
                .find(modelClass);
        return ts;
    }

    /**
     * * offset多条件全查询（分页查询）
     * 例如
     *  DataSupport
     * .where(String[] 条件）
     * .order(排序).limit().offset()
     * .find(表名);
     * sql语句：select * from 表名 where 条件 order by 字段 desc limit 0,0;
     * @param modelClass
     *              表名（类）
     * @param order
     *              字段+desc/asc(正序或倒序)
     * @param limit
     *              界限0 - limit
     * @param offset
     *              偏移量（分页查询）可为null
     * @param where
     *               查询条件
     * @param <T>
     *              泛型
     * @return
     *              对应结果
     */
    public static <T extends  DataSupport> List<T> DBSelectByTogether_offset(Class<T> modelClass,String [] where,String order,int limit,int offset){
        List<T> ts = DataSupport
                .where(where)
                .order(order).limit(limit).offset(offset)
                .find(modelClass);
        return ts;
    }


    /**
     * offset多条件查询（分页查询）
     * 例如
     *  DataSupport.select（查询字段）
     * .where(String[] 条件）
     * .order(排序).limit().offset()
     * .find(表名);
     * sql语句：select 查询字段 from 表名 where 条件 order by 字段 desc limit 0,0;
     *  @param modelClass
     *              表名（类）
     * @param order
     *              字段+desc/asc(正序或倒序)
     * @param limit
     *              界限0 - limit
     * @param offset
     *              偏移量（分页查询）可为null
     * @param where
     *               查询条件
     * @param column_name
     *               查询字段
     * @param <T>
     *              泛型
     * @return
     *              对应结果
     */
    public static <T extends  DataSupport> List<T> DBSelectBy_offset(Class<T> modelClass,String [] where,String order,int limit,int offset,String... column_name){
        List<T> ts = DataSupport.select(column_name)
                .where(where)
                .order(order).limit(limit).offset(offset)
                .find(modelClass);
        return ts;
    }
    /**
     * 根据编写的Sql语句查询
     * @param sql
     *          sql语句
     * @param <T>
     * @return
     *           对应结果
     */
    public static <T extends DataSupport> Cursor DBSelectBySQL(String sql){
        Cursor bySQL = DataSupport.findBySQL(sql);
        return bySQL;
    }
}

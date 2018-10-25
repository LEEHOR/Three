package net.coahr.three3.three.Util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadManage extends ThreadPoolExecutor {
    private ThreadManage(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {

        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

    }



    public static ThreadManage getInstance() {

        /**

         * 获取CPU数量

         */

        int processors = Runtime.getRuntime().availableProcessors();



        /**

         * 核心线程数量

         */

        int corePoolSize = processors + 1;

        /**

         * 最大线程数量

         */

        int maximumPoolSize = processors * 2 + 1;

        /**

         * 空闲有效时间

         */

        long keepAliveTime = 60;

        /**

         * 创建自定义线程池

         */



        return new ThreadManage(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new PriorityBlockingQueue());



    }



    /**

     * 用于控制线程开始与停止执行的方法

     */

    private boolean isPaused;

    private ReentrantLock pauseLock = new ReentrantLock();

    private Condition unpaused = pauseLock.newCondition();





    /**

     * 任务执行前要执行的方法

     *

     * @param t

     * @param r

     */

    @Override

    protected void beforeExecute(Thread t, Runnable r) {

        super.beforeExecute(t, r);

        System.out.println(Thread.currentThread().getName() + "  任务执行开始 ");

        pauseLock.lock();

        try {

            while (isPaused) unpaused.await();

        } catch (InterruptedException ie) {

            t.interrupt();

        } finally {

        }

    }



    /**

     * 任务执行后要执行的方法

     *

     * @param r

     * @param t

     */

    @Override

    protected void afterExecute(Runnable r, Throwable t) {

        super.afterExecute(r, t);

        System.out.println(Thread.currentThread().getName() + "  任务执行over ");

    }



    /**

     * 线程池关闭后要执行的方法

     */

    @Override

    protected void terminated() {

        super.terminated();

    }



    /**

     * 暂停执行任务的方法

     */

    public void pause() {

        pauseLock.lock();

        try {

            isPaused = true;

        } finally {

            pauseLock.unlock();

        }

    }



    /**

     * 恢复执行任务的方法

     */

    public void resume() {

        pauseLock.lock();

        try {

            isPaused = false;

            unpaused.signalAll();

        } finally {

            pauseLock.unlock();

        }

    }
}

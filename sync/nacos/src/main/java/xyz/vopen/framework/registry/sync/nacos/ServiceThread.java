package xyz.vopen.framework.registry.sync.nacos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link ServiceThread}
 *
 * <p>Class ServiceThread Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/14
 */
public abstract class ServiceThread implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(ServiceThread.class);

  private static final long JOIN_TIME = 90 * 1000;

  private Thread thread;
  protected final CountDownLatch2 waitPoint = new CountDownLatch2(1);
  protected volatile AtomicBoolean hasNotified = new AtomicBoolean(false);
  protected volatile boolean stopped = false;
  protected boolean isDaemon = false;

  // Make it able to restart the thread
  private final AtomicBoolean started = new AtomicBoolean(false);

  public ServiceThread() {}

  public abstract String getServiceName();

  public void start() {
    log.info("Try to start service thread:{} started:{} lastThread:{}", getServiceName(), started.get(), thread);
    if (!started.compareAndSet(false, true)) {
      return;
    }
    stopped = false;
    this.thread = new Thread(this, getServiceName());
    this.thread.setDaemon(isDaemon);
    this.thread.start();
  }

  public void shutdown() {
    this.shutdown(false);
  }

  public void shutdown(final boolean interrupt) {
    log.info("Try to shutdown service thread:{} started:{} lastThread:{}", getServiceName(), started.get(), thread);
    if (!started.compareAndSet(true, false)) {
      return;
    }
    this.stopped = true;
    log.info("shutdown thread " + this.getServiceName() + " interrupt " + interrupt);

    if (hasNotified.compareAndSet(false, true)) {
      waitPoint.countDown(); // notify
    }

    try {
      if (interrupt) {
        this.thread.interrupt();
      }

      long beginTime = System.currentTimeMillis();
      if (!this.thread.isDaemon()) {
        this.thread.join(this.getJoinTime());
      }
      long elapsedTime = System.currentTimeMillis() - beginTime;
      log.info("join thread " + this.getServiceName() + " elapsed time(ms) " + elapsedTime + " " + this.getJoinTime());
    } catch (InterruptedException e) {
      log.error("Interrupted", e);
    }
  }

  public long getJoinTime() {
    return JOIN_TIME;
  }

  public void makeStop() {
    if (!started.get()) {
      return;
    }
    this.stopped = true;
    log.info("makestop thread " + this.getServiceName());
  }

  public void wakeup() {
    if (hasNotified.compareAndSet(false, true)) {
      waitPoint.countDown(); // notify
    }
  }

  protected void waitForRunning(long interval) {
    if (hasNotified.compareAndSet(true, false)) {
      this.onWaitEnd();
      return;
    }

    // entry to wait
    waitPoint.reset();

    try {
      waitPoint.await(interval, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      log.error("Interrupted", e);
    } finally {
      hasNotified.set(false);
      this.onWaitEnd();
    }
  }

  protected void onWaitEnd() {}

  public boolean isStopped() {
    return stopped;
  }

  public boolean isDaemon() {
    return isDaemon;
  }

  public void setDaemon(boolean daemon) {
    isDaemon = daemon;
  }
}

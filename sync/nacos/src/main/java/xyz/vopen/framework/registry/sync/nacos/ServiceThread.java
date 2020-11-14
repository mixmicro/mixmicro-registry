package xyz.vopen.framework.registry.sync.nacos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  protected final Thread thread;
  protected volatile boolean hasNotified = false;
  protected volatile boolean stopped = false;

  public ServiceThread() {
    this.thread = new Thread(this, this.getServiceName());
  }

  public abstract String getServiceName();

  public void start() {
    this.thread.start();
  }

  public void shutdown() {
    this.shutdown(false);
  }

  public void shutdown(final boolean interrupt) {
    this.stopped = true;
    log.info("shutdown thread " + this.getServiceName() + " interrupt " + interrupt);
    synchronized (this) {
      if (!this.hasNotified) {
        this.hasNotified = true;
        this.notify();
      }
    }

    try {
      if (interrupt) {
        this.thread.interrupt();
      }

      long beginTime = System.currentTimeMillis();
      this.thread.join(this.getJoinTime());
      long eclipseTime = System.currentTimeMillis() - beginTime;
      log.info("join thread " + this.getServiceName() + " eclipse time(ms) " + eclipseTime + " " + this.getJoinTime());
    } catch (InterruptedException e) {
      log.error("Interrupted", e);
    }
  }

  public long getJoinTime() {
    return JOIN_TIME;
  }

  public boolean isStopped() {
    return stopped;
  }
}

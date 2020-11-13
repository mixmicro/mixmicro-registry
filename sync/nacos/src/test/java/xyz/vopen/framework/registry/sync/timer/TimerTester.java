package xyz.vopen.framework.registry.sync.timer;

import xyz.vopen.mixmicro.kits.timer.HashedWheelTimer;
import xyz.vopen.mixmicro.kits.timer.WaitStrategy;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * {@link TimerTester}
 *
 * <p>Class TimerTester Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/11/13
 */
public class TimerTester {

  public static void main(String[] args) throws InterruptedException {

    HashedWheelTimer timer =
        new HashedWheelTimer(
            "sync-hashed-wheel-timer",
            TimeUnit.MILLISECONDS.toNanos(10),
            1024,
            new WaitStrategy.YieldingWait(),
            Executors.newFixedThreadPool(8));

    timer.scheduleWithFixedDelay(
        () -> System.out.println("==" + new Date()), 2, 5, TimeUnit.SECONDS);

    timer.scheduleWithFixedDelay(
        () -> System.out.println(">>" + new Date()), 3, 6, TimeUnit.SECONDS);

    Thread.sleep(30000);

    timer.shutdown();
  }
}

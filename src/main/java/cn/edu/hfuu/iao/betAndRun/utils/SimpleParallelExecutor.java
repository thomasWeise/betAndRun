package cn.edu.hfuu.iao.betAndRun.utils;

import java.util.function.Consumer;

/**
 * This is a trivial, low-overhead parallel engine. It allows you to submit
 * a couple of tasks and to run them in parallel. A task must not submit
 * call {@link #waitForAll()}.
 */
public final class SimpleParallelExecutor {

  /** the task queue */
  private static volatile __Task s_queue;
  /** the number of running tasks */
  private static volatile int s_running;
  /** the queue synchronizer */
  private static final Object SYNCH = new Object();

  static {
    final int workerCount = Math.max(1,
        (Runtime.getRuntime().availableProcessors() - 1));
    ConsoleIO.stdout((stdout) -> stdout.println(//
        "Setting up " + //$NON-NLS-1$
            workerCount + " parallel workers.")); //$NON-NLS-1$
    for (int i = workerCount; (--i) >= 0;) {
      final Thread thread = new Thread(
          () -> SimpleParallelExecutor.__executeNext());
      thread.setDaemon(true);
      thread.start();
    }
  }

  /**
   * Execute the task
   *
   * @param task
   *          the task
   */
  public static final void execute(final Runnable task) {
    synchronized (SimpleParallelExecutor.SYNCH) {
      SimpleParallelExecutor.s_queue = new __Task(task,
          SimpleParallelExecutor.s_queue);
      SimpleParallelExecutor.SYNCH.notifyAll();
    }
  }

  /**
   * Execute multiple tasks: The task factory's {@code accept} method will
   * be called exactly once, providing it with a consumer for new tasks. It
   * can then call this consumer's {@code accept} method arbitrarily often,
   * each time providing a new task. The advantage of this method over
   * {@link #execute(Runnable)} is that it will create the tasks all in a
   * single synchronized block, right after which the task processing
   * begins. This should reduce overhead of synchronization.
   *
   * @param taskFactory
   *          the task factory
   */
  public static final void executeMultiple(
      final Consumer<Consumer<Runnable>> taskFactory) {
    synchronized (SimpleParallelExecutor.SYNCH) {
      taskFactory.accept(
          (task) -> SimpleParallelExecutor.s_queue = new __Task(task,
              SimpleParallelExecutor.s_queue));
      SimpleParallelExecutor.SYNCH.notifyAll();
    }
  }

  /** wait for all tasks to finish */
  public static final void waitForAll() {
    int interruptedCount = 0;
    wait: for (;;) {
      try {
        synchronized (SimpleParallelExecutor.SYNCH) {
          if (SimpleParallelExecutor.s_queue == null) {
            if (SimpleParallelExecutor.s_running <= 0) {
              break wait;
            }
          }
          SimpleParallelExecutor.SYNCH.wait();
        }
      } catch (final InterruptedException ie) {
        // we can ignore this, but if it happens too often, let's crash to
        // be on the safe side
        if ((++interruptedCount) > 10000) {
          throw new Error((//
          "Waiting for tasks has been interrupted "//$NON-NLS-1$
              + interruptedCount + //
              " times. There may be something wrong, we better escalate this as an Error."), //$NON-NLS-1$
              ie);
        }
      }
    }
  }

  /** execute the next task */
  private static final void __executeNext() {
    __Task next;
    boolean justFinished;

    justFinished = false;
    try {
      for (;;) {
        synchronized (SimpleParallelExecutor.SYNCH) {
          next = SimpleParallelExecutor.s_queue;
          if (next != null) {
            SimpleParallelExecutor.s_queue = next.m_next;
            if (!justFinished) {
              ++SimpleParallelExecutor.s_running;
              justFinished = true;
            }
          } else {
            if (justFinished) {
              justFinished = false;
              if ((--SimpleParallelExecutor.s_running) <= 0) {
                SimpleParallelExecutor.SYNCH.notifyAll();
                continue;
              }
            }
            SimpleParallelExecutor.SYNCH.wait();
            continue;
          }
        }
        next.m_task.run();
      }
    } catch (final InterruptedException ie) {
      ie.printStackTrace();
    }
  }

  /** the task class */
  private static final class __Task {

    /** the next individual task */
    final __Task m_next;
    /** the task */
    final Runnable m_task;

    /**
     * create
     *
     * @param task
     *          the task
     * @param next
     *          the next task
     */
    __Task(final Runnable task, final __Task next) {
      super();
      this.m_task = task;
      this.m_next = next;
    }
  }
}

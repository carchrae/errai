package org.jboss.errai.ioc.tests.wiring.client;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.test.AbstractErraiIOCTest;
import org.jboss.errai.ioc.tests.wiring.client.res.DependentBeanWithDisposer;
import org.jboss.errai.ioc.tests.wiring.client.res.SingletonBeanWithDisposer;

/**
 * @author Mike Brock
 */
public class DisposerTest extends AbstractErraiIOCTest {
  @Override
  public String getModuleName() {
    return "org.jboss.errai.ioc.tests.wiring.IOCWiringTests";
  }

  public void testDisposerFailsToDestroyAppScope() {
    runAfterInit(new Runnable() {
      @Override
      public void run() {
        SingletonBeanWithDisposer bean = IOC.getBeanManager().lookupBean(SingletonBeanWithDisposer.class).getInstance();

        assertNotNull(bean);
        assertNotNull(bean.getDependentBeanDisposer());

        try {
          bean.dispose();
        }
        catch (IllegalStateException e) {
          finishTest();
        }

        assertFalse("bean should have been disposed", IOC.getBeanManager().isManaged(bean.getBean()));
        assertFalse("outer bean should have been disposed", IOC.getBeanManager().isManaged(bean));

        assertTrue("bean's destructor should have been called", bean.getBean().isPreDestroyCalled());

        finishTest();
      }
    });
  }

  public void testDisposerWorksWithDependentScope() {
    runAfterInit(new Runnable() {
      @Override
      public void run() {
        DependentBeanWithDisposer bean = IOC.getBeanManager().lookupBean(DependentBeanWithDisposer.class).getInstance();

        assertNotNull(bean);
        assertNotNull(bean.getDependentBeanDisposer());

        bean.dispose();

        assertFalse("bean should have been disposed", IOC.getBeanManager().isManaged(bean.getBean()));
        assertFalse("outer bean should have been disposed", IOC.getBeanManager().isManaged(bean));

        assertTrue("bean's destructor should have been called", bean.getBean().isPreDestroyCalled());

        finishTest();
      }
    });
  }
}

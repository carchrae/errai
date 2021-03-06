package org.jboss.errai.cdi.producer.client.test;


import org.jboss.errai.cdi.producer.client.BeanConstrConsumersMultiProducers;
import org.jboss.errai.cdi.producer.client.BeanConstrConsumesOwnProducer;
import org.jboss.errai.cdi.producer.client.BeanConsumesOwnProducer;
import org.jboss.errai.cdi.producer.client.BeanConsumesOwnProducerB;
import org.jboss.errai.cdi.producer.client.DepBeanConstrConsumesOwnProducer;
import org.jboss.errai.cdi.producer.client.DependentProducedBeanDependentBean;
import org.jboss.errai.cdi.producer.client.Fooblie;
import org.jboss.errai.cdi.producer.client.FooblieDependentBean;
import org.jboss.errai.cdi.producer.client.FooblieMaker;
import org.jboss.errai.cdi.producer.client.Kayak;
import org.jboss.errai.cdi.producer.client.ProducerDependentTestBean;
import org.jboss.errai.cdi.producer.client.ProducerDependentTestBeanWithCycle;
import org.jboss.errai.cdi.producer.client.ProducerTestModule;
import org.jboss.errai.cdi.producer.client.SingletonProducedBeanDependentBean;
import org.jboss.errai.ioc.client.IOCClientTestCase;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import java.util.List;

/**
 * Tests CDI producers.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
//@RunWith(IOCSimulatedTestRunner.class)
public class ProducerIntegrationTest extends IOCClientTestCase {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.cdi.producer.ProducerTestModule";
  }

  ProducerTestModule module;
  ProducerDependentTestBean testBean;

  @Override
  public void gwtSetUp() throws Exception {
    super.gwtSetUp();
    module = IOC.getBeanManager().lookupBean(ProducerTestModule.class).getInstance();
    testBean = IOC.getBeanManager().lookupBean(ProducerDependentTestBean.class).getInstance();
  }

  public void testInjectionUsingProducerField() {
    assertEquals("Failed to inject produced @A",
        module.getNumberA(),
        testBean.getIntegerA());
  }

  public void testInjectionUsingProducerMethod() {
    assertEquals("Failed to inject produced @B",
        module.getNumberB(),
        testBean.getIntegerB());
  }

  public void testInjectionUsingDependentProducerMethods() {
    assertEquals("Failed to inject produced @C",
        module.getNumberC(),
        testBean.getIntegerC());

    assertEquals("Failed to inject produced String depending on @C",
        module.getNumberC().toString(),
        testBean.getProducedString());
  }

  public void testAnyQualifiedInjection() {
    assertEquals("Failed to inject produced @D @E as @Any",
        module.getFloatDE(),
        testBean.getUnqualifiedFloat());
  }

  public void testSubsetQualifiedInjection() {
    assertEquals("Failed to inject produced @D @E as @D",
        module.getFloatDE(),
        testBean.getFloatD());
  }

  public void testCyclicalDependencyWasSatisfied() {
    assertEquals(testBean.getFloatD(), testBean.getFloatD());
    assertEquals(testBean.getIntegerA(), testBean.getIntegerA());
    assertEquals(testBean.getIntegerB(), testBean.getIntegerB());

    final String val = "TestFieldABC";
    testBean.setTestField(val);
    assertEquals(val, testBean.getTestField());
  }

  public void testStaticProducers() {
    assertNotNull("bean was not injected!", testBean.getStaticallyProducedBean());
    assertNotNull("bean was not injected!", testBean.getStaticallyProducedBeanB());
  }

  public void testCycleThroughAProducedInterface() {
    final ProducerDependentTestBeanWithCycle bean = IOC.getBeanManager()
        .lookupBean(ProducerDependentTestBeanWithCycle.class).getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getFooface());
    assertEquals("HiThere", bean.getFooface().getMessage());
  }

  public void testBeanCanConsumeProducerFromItself() {
    final BeanConsumesOwnProducer bean = IOC.getBeanManager().lookupBean(BeanConsumesOwnProducer.class).getInstance();

    assertNotNull(bean);
    assertNotNull("bean did not inject its own producer", bean.getMagic());
  }

  public void testBeanCanConsumerProducerFromItselfThroughConstrCycle() {
    final BeanConstrConsumesOwnProducer bean = IOC.getBeanManager().lookupBean(BeanConstrConsumesOwnProducer.class).getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getThing());
    assertNotNull(bean.getThing().getThing());
  }

  public void testDependentBeanCanConsumerProducerFromItselfThroughConstrCycle() {
    final DepBeanConstrConsumesOwnProducer bean = IOC.getBeanManager().lookupBean(DepBeanConstrConsumesOwnProducer.class).getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getThang());
    assertNotNull(bean.getThang().getThang());
  }

  public void testNormalBeanCanConsumeOwnProducerAsFieldInjectionMixedWithConstrInjection() {
    final BeanConsumesOwnProducerB bean = IOC.getBeanManager().lookupBean(BeanConsumesOwnProducerB.class).getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getFactory());
    assertNotNull(bean.getThing());
    assertNotNull(bean.getThung());
  }

  public void testProducersObserveSingletonScope() {
    final IOCBeanManager beanManager = IOC.getBeanManager();

    final IOCBeanDef<Kayak> kayakBean = beanManager.lookupBean(Kayak.class);
    assertNotNull(kayakBean);

    final SingletonProducedBeanDependentBean bean = beanManager.lookupBean(SingletonProducedBeanDependentBean.class)
        .getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getKayakA());
    assertNotNull(bean.getKayakB());
    assertEquals("singleton scope for producer violated!", bean.getKayakA().getId(), bean.getKayakB().getId());

    final DependentProducedBeanDependentBean beanB = beanManager.lookupBean(DependentProducedBeanDependentBean.class)
        .getInstance();

    assertNotNull(beanB);
    assertNotNull(beanB.getKayakA());
    assertNotNull(beanB.getKayakB());
    assertEquals("singleton scope for producer violated!", bean.getKayakA().getId(), beanB.getKayakA().getId());
    assertEquals("singleton scope for producer violated!", bean.getKayakA().getId(), beanB.getKayakB().getId());

    final Kayak kayakBeanInstance = kayakBean.getInstance();

    assertNotNull(kayakBeanInstance);
    assertEquals("manual lookup should produce the same bean",
        kayakBeanInstance.getId(), beanB.getKayakA().getId());

    final Kayak newKayak = kayakBean.newInstance();

    assertNotNull(newKayak);
    assertFalse("new Kayak should have new ID", kayakBeanInstance.getId() == newKayak.getId());
  }

  public void testComplexConstructorInjectionScenario() {
    final BeanConstrConsumersMultiProducers bean = IOC.getBeanManager().lookupBean(BeanConstrConsumersMultiProducers.class)
        .getInstance();

    assertNotNull(bean);
    assertNotNull(bean.getGreeting());
    assertNotNull(bean.getParting());
    assertNotNull(bean.getResponse());
    assertNotNull(bean.getTestEvent());
  }

  public void testDisposerMethod() {
    final FooblieMaker maker = IOC.getBeanManager().lookupBean(FooblieMaker.class).getInstance();

    final FooblieDependentBean fooblieDependentBean1
        = IOC.getBeanManager().lookupBean(FooblieDependentBean.class).getInstance();
    final FooblieDependentBean fooblieDependentBean2
        = IOC.getBeanManager().lookupBean(FooblieDependentBean.class).getInstance();

    IOC.getBeanManager().destroyBean(fooblieDependentBean1);
    IOC.getBeanManager().destroyBean(fooblieDependentBean2);

    final List<Fooblie> foobliesResponse = maker.getDestroyedFoobliesResponse();

    assertEquals("there should be two destroyed beans", 2, foobliesResponse.size());
    assertEquals(fooblieDependentBean1.getFooblieResponse(), foobliesResponse.get(0));
    assertEquals(fooblieDependentBean2.getFooblieResponse(), foobliesResponse.get(1));

    final List<Fooblie> foobliesGreets = maker.getDestroyedFoobliesGreets();

    assertEquals("there should be two destroyed beans", 2, foobliesGreets.size());
    assertEquals(fooblieDependentBean1.getFooblieGreets(), foobliesGreets.get(0));
    assertEquals(fooblieDependentBean2.getFooblieGreets(), foobliesGreets.get(1));

    final List<Fooblie> foobliesParts = maker.getDestroyedFoobliesParts();

    assertEquals("there should be two destroyed beans", 2, foobliesParts.size());
    assertEquals(fooblieDependentBean1.getFooblieParts(), foobliesParts.get(0));
    assertEquals(fooblieDependentBean2.getFooblieParts(), foobliesParts.get(1));
  }
}
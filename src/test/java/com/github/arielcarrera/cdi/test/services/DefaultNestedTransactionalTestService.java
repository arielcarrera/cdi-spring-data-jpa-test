package com.github.arielcarrera.cdi.test.services;

import javax.enterprise.inject.Alternative;
import javax.transaction.Transactional;

@Transactional @Alternative
public class DefaultNestedTransactionalTestService extends DefaultTransactionalTestService {

}

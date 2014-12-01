package com.demo2do;

import com.demo2do.core.utils.ClassUtilTest;
import com.demo2do.core.utils.CommonUtilTest;
import junit.extensions.ActiveTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCase {

    public static Test suite() {
        TestSuite suite = new ActiveTestSuite();

        suite.addTestSuite(CommonUtilTest.class);
        suite.addTestSuite(ClassUtilTest.class);

        return suite;
    }
}
